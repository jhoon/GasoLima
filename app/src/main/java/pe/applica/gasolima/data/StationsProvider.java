package pe.applica.gasolima.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import pe.applica.gasolima.data.StationsContract.StationEntry;

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
    private StationsDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = StationsDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case STATION: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        StationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            }
            case STATION_ID: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        StationEntry.TABLE_NAME,
                        projection,
                        StationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null, null, null,
                        sortOrder
                );
                break;
            }
            case STATION_NEARBY: {
                returnCursor = getNearbyStations(uri, projection);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (getContext() != null) {
            returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        String returnType;

        switch (match) {
            case STATION:
            case STATION_NEARBY:
                returnType = StationEntry.CONTENT_TYPE;
                break;
            case STATION_ID:
                returnType = StationEntry.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnType;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STATION: {
                long _id = db.insert(StationEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = StationEntry.buildStationUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case STATION:
                rowsDeleted = db.delete(StationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case STATION:
                rowsUpdated = db.update(StationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
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

    /**
     * Returns a cursor with nearby stations
     * @param uri Uri with current location
     * @param projection columns to be extracted from the db
     * @return a cursor with the resulting stations
     */
    private Cursor getNearbyStations(Uri uri, String[] projection) {
        String latitude = StationEntry.getLatitudeFromUri(uri);
        String longitude = StationEntry.getLongitudeFromUri(uri);

        // Set the ordering by distance
        String sOrderByLatLong = " ABS(" + StationEntry.COLUMN_LATITUDE + " - "+ latitude +") + " +
                " ABS(" + StationEntry.COLUMN_LONGITUDE + " - " + longitude +") ASC ";

        return mOpenHelper.getReadableDatabase().query(
                StationEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sOrderByLatLong);
    }
}
