package pe.applica.gasolima.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pe.applica.gasolima.data.StationsContract.StationEntry;

/**
 * SQLite Database Helper for GasoLima data
 * Created by jhoon on 1/13/16.
 */
public class StationsDbHelper extends SQLiteOpenHelper {
    private static StationsDbHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gasolima.db";

    public StationsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static StationsDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StationsDbHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_STATION_TABLE = "CREATE TABLE " + StationEntry.TABLE_NAME + " (" +
                StationEntry._ID + " INTEGER PRIMARY KEY, " +
                StationEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL, " +
                StationEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                StationEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                StationEntry.COLUMN_DISTANCE + " TEXT NOT NULL, " +
                StationEntry.COLUMN_COMPANY + " TEXT, " +
                StationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                StationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                StationEntry.COLUMN_GAS84 + " REAL, " +
                StationEntry.COLUMN_GAS90 + " REAL, " +
                StationEntry.COLUMN_GAS95 + " REAL, " +
                StationEntry.COLUMN_GAS97 + " REAL, " +
                StationEntry.COLUMN_GAS98 + " REAL, " +

                " UNIQUE (" + StationEntry.COLUMN_ID + ") ON CONFLICT REPLACE " +

                " );";

        db.execSQL(SQL_CREATE_STATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StationEntry.TABLE_NAME);
        onCreate(db);
    }
}
