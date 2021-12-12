package es.jaf.myshortcuts;


import android.app.*;
import android.util.Log;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    static GlobalApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    static void saveException(String text, Throwable ex) {
        Log.e(BuildConfig.APPLICATION_ID + "_LOG", text, ex);
    }
}