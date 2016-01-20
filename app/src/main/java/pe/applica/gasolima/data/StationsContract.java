package pe.applica.gasolima.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract description for the Stations
 * Created by jhoon on 1/13/16.
 */
public class StationsContract {
    public static final String CONTENT_AUTHORITY = "pe.applica.gasolima";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STATION = "station";

    /* Inner class that defines the table contents of the station table */
    public static final class StationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_STATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_STATION;

        // Table name
        public static final String TABLE_NAME = "station";

        public static final String COLUMN_ID = "server_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_DISTANCE= "distance";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_GAS84 = "gas84";
        public static final String COLUMN_GAS90 = "gas90";
        public static final String COLUMN_GAS95 = "gas95";
        public static final String COLUMN_GAS97 = "gas97";
        public static final String COLUMN_GAS98 = "gas98";

        public static Uri buildStationUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildNearbyStationsUri(String latitude, String longitude){
            return CONTENT_URI.buildUpon().appendPath("nearby").appendPath(latitude).appendPath(longitude).build();
        }

        public static String getLatitudeFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }
        public static String getLongitudeFromUri(Uri uri){
            return uri.getPathSegments().get(3);
        }
    }
}
