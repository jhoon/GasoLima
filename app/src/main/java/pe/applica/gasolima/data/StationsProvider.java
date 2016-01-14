package pe.applica.gasolima.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Provider for the Stations List and the Station Detail
 * Created by jhoon on 1/14/16.
 */
public class StationsProvider extends ContentProvider {
    // Ints that represent the URI code for each kind of Uri
    public static final int STATION = 100;
    public static final int STATION_NEARBY = 101;
    public static final int STATION_ID = 102;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Method to create the appropriate Uri Matcher for this app.
     * @return an UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StationsContract.CONTENT_AUTHORITY;

        // For each type of Uri that is added, assign a code
        matcher.addURI(authority, StationsContract.PATH_STATION, STATION);
        matcher.addURI(authority, StationsContract.PATH_STATION + "/nearby/*/*", STATION_NEARBY);
        matcher.addURI(authority, StationsContract.PATH_STATION + "/#", STATION_ID);

        return matcher;
    }
}
