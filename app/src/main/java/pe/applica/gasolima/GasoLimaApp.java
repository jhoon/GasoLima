package pe.applica.gasolima;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * Holds the calls to initialize libraries (as required), mainly the Analytics one
 * Created by jhoon on 1/30/16.
 */
public class GasoLimaApp extends Application{
    public Tracker mTracker;

    public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.tracker_app);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker() {
        startTracking();
        return mTracker;
    }
}
