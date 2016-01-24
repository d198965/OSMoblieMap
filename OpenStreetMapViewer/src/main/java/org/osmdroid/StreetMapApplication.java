package org.osmdroid;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by zdh on 15/12/16.
 */
public class StreetMapApplication extends MultiDexApplication{
    private static RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        StreetMapApplication application = (StreetMapApplication) context
                .getApplicationContext();
        return application.refWatcher;
    }

}
