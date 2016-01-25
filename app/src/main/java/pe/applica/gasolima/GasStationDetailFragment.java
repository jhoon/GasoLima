package pe.applica.gasolima;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.applica.gasolima.data.StationsContract.StationEntry;

/**
 * A fragment representing a single GasStation detail screen.
 * This fragment is either contained in a {@link GasStationListActivity}
 * in two-pane mode (on tablets) or a {@link GasStationDetailActivity}
 * on handsets.
 */
public class GasStationDetailFragment extends Fragment
        implements OnMapReadyCallback , LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GasStationDetail";

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String DETAIL_URI = "detail_uri";

    private Double mLat;
    private Double mLong;

    private Uri mUri;

    public static final int DETAIL_LOADER = 0;

    private static final String[] STATION_COLUMNS = {
            StationEntry.TABLE_NAME + "." + StationEntry._ID,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_ID,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_NAME,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_DISTANCE,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_LATITUDE,
            StationEntry.TABLE_NAME + "." + StationEntry.COLUMN_LONGITUDE
    };

    // These are indices tied to STATION_COLUMNS
    public static final int COL_STATION_ID = 0;
    public static final int COL_STATION_SERVER_ID = 1;
    public static final int COL_STATION_NAME = 2;
    public static final int COL_STATION_DISTANCE = 3;
    public static final int COL_STATION_LATITUDE = 4;
    public static final int COL_STATION_LONGITUDE = 5;

    @Bind(R.id.gasstation_detail) TextView mTitleView;
    MapView mapView;

    GoogleMap mMap;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GasStationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gasstation_detail, container, false);

        if (getArguments().containsKey(DETAIL_URI)) {
            mUri = getArguments().getParcelable(DETAIL_URI);

            Log.d(TAG, "onCreate: mUri: " + mUri);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                // Referencing the map from the toolbar
                // (won't work on tablets... for now)
                mapView = (MapView)activity.findViewById(R.id.detail_map);
                mapView.onCreate(savedInstanceState);
                mapView.getMapAsync(this);
            }
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateGoogleMap();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: mUri: " + mUri.toString());
        if (null != mUri) {
            return new CursorLoader(getActivity(), mUri, STATION_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.d(TAG, "onLoadFinished: THERE'S DATA!");

            mTitleView.setText(data.getString(COL_STATION_NAME));
            mLat = Double.parseDouble(data.getString(COL_STATION_LATITUDE));
            mLong = Double.parseDouble(data.getString(COL_STATION_LONGITUDE));
            updateGoogleMap();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateGoogleMap() {
        if (mMap != null && mLat != null) {
            // Add a marker in Sydney and move the camera
            LatLng markerLoc = new LatLng(mLat, mLong);
            mMap.addMarker(new MarkerOptions().position(markerLoc).title("Marker for Station"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLoc, 15));
        }
    }
}
