package com.nuvola.gxpenses;

import android.app.Application;
import com.nuvola.gxpenses.ioc.MainModule;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.JavaLoggingHandler;
import roboguice.RoboGuice;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getName();
    private static final boolean DEBUG = Constants.DEBUG;

    static {
        Logger.getLogger("org.jnuvola.buxfy").addHandler(new JavaLoggingHandler());
        Logger.getLogger("org.jnuvola.buxfy").setLevel(Level.ALL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new MainModule());
    }
}
