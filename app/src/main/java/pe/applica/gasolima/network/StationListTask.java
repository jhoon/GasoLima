package pe.applica.gasolima.network;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import pe.applica.gasolima.R;
import pe.applica.gasolima.data.StationsContract;
import pe.applica.gasolima.network.model.BaseResponse;
import pe.applica.gasolima.network.model.Venue;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * AsyncTask that retrieves Gas Stations
 * Created by jhoon on 1/30/16.
 */
public class StationListTask extends AsyncTask<Location, Void, Void>{
    private Context mContext;

    public StationListTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Location... params) {
        Location location = params[0];

        // Connecting to retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.app_endpoint))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GasoLimaAPI service = retrofit.create(GasoLimaAPI.class);
        Call<BaseResponse> stationsCall = service.getStations(location.getLatitude(), location.getLongitude());

        try {
            Response<BaseResponse> response = stationsCall.execute();

            if (response.isSuccess()) {

                List<Venue> venues = response.body().response;
                ContentValues[] values = new ContentValues[venues.size()];
                int i = 0;

                for (Venue venue : venues) {
                    ContentValues stationValues = new ContentValues();
                    stationValues.put(StationsContract.StationEntry.COLUMN_ID, venue.venue.id);
                    stationValues.put(StationsContract.StationEntry.COLUMN_NAME, venue.venue.nombre);
                    stationValues.put(StationsContract.StationEntry.COLUMN_ADDRESS, venue.venue.direccion);
                    stationValues.put(StationsContract.StationEntry.COLUMN_DISTANCE, venue.venue.distancia);
                    stationValues.put(StationsContract.StationEntry.COLUMN_LATITUDE, venue.venue.lat);
                    stationValues.put(StationsContract.StationEntry.COLUMN_LONGITUDE, venue.venue.lng);
                    stationValues.put(StationsContract.StationEntry.COLUMN_GASES, venue.venue.generateGasesString());

                    values[i] = stationValues;
                    i++;
                }

                mContext.getContentResolver().bulkInsert(StationsContract.StationEntry.CONTENT_URI, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
