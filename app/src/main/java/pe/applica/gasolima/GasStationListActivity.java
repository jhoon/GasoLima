package pe.applica.gasolima;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import pe.applica.gasolima.data.StationsContract.StationEntry;
import pe.applica.gasolima.network.GasoLimaAPI;
import pe.applica.gasolima.network.model.BaseResponse;
import pe.applica.gasolima.network.model.Venue;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * An activity representing a list of GasStations. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GasStationDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class GasStationListActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LoaderCallbacks<Cursor> {
    private static final String TAG = "GasStationListActivity";
    public static final int LOCATION_PERMISSION = 0;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView mRecyclerView;
    private Location mLastLocation;
    private SimpleItemRecyclerViewAdapter mStationsAdapter;

    private static final String[] STATION_COLUMNS = {
            StationEntry.TABLE_NAME + "." + StationEntry._ID,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_ID,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_NAME,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_DISTANCE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasstation_list);
        buildGoogleApiClient();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.gasstation_list);
        assert recyclerView != null;
        mRecyclerView = (RecyclerView)recyclerView;

        if (findViewById(R.id.gasstation_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Connecting to retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.app_endpoint))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GasoLimaAPI service = retrofit.create(GasoLimaAPI.class);
        Call<BaseResponse> stationsCall = service.getStations(-12.08, -76.99);
        stationsCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Response<BaseResponse> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.i(TAG, "onResponse: WE DID IT!!!");
                    List<Venue> venues = response.body().response;
                    for (Venue venue : venues) {
                        Log.i(TAG, "onResponse: venues~ " + venue.venue.id + " nombre: " + venue.venue.nombre);
                    }
                    mStationsAdapter = new SimpleItemRecyclerViewAdapter(venues);
                    mRecyclerView.setAdapter(mStationsAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "onFailure: ZOMG, ERROR!", t);
            }
        });

    }

    void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(null));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Venue> mValues;

        public SimpleItemRecyclerViewAdapter(List<Venue> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gasstation_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).venue.GAS84);
            holder.mContentView.setText(mValues.get(position).venue.distancia);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(GasStationDetailFragment.ARG_ITEM_ID, holder.mItem.venue.nombre);
                        GasStationDetailFragment fragment = new GasStationDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.gasstation_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, GasStationDetailActivity.class);
                        intent.putExtra(GasStationDetailFragment.ARG_ITEM_ID, holder.mItem.venue.nombre);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Venue mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView)view.findViewById(R.id.id);
                mContentView = (TextView)view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        // Check for Location Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        } else {
            // Permission has been granted, continue as usual
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                Log.d(TAG, "onConnected: location obtained, lat " + location.getLatitude() +
                        ", long " + location.getLongitude());
                mLastLocation = location;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended: wat.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: wat again.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (mGoogleApiClient.isConnected()) {
                    // let's call the onConnected method again.
                    mGoogleApiClient.reconnect();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri stationsNearby;

        if (mLastLocation != null) {
            stationsNearby = StationEntry
                    .buildNearbyStationsUri(Double.toString(mLastLocation.getLatitude()),
                            Double.toString(mLastLocation.getLongitude()));
        } else {
            // Setting a default origin location for when no location was obtained
            stationsNearby = StationEntry.buildNearbyStationsUri("-12.08", "-76.99");
        }

        return new CursorLoader(this, stationsNearby, STATION_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
