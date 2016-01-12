package pe.applica.gasolima.network;

import pe.applica.gasolima.network.model.BaseResponse;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Backend descriptor for Retrofit
 * Created by jhoon on 1/11/16.
 */
public interface GasoLimaAPI {
    /**
     * Nearby gas stations, relative to the user
     * @param latitude the user's current latitude
     * @param longitude the user's current longitude
     * @return a response containing the list of nearby stations
     */
    @Headers("Cache-control: max-age=640000")
    @GET("nearby/{lat}/{lon}/")
    Call<BaseResponse> getStations(@Path("lat") double latitude, @Path("lon") double longitude);
}
