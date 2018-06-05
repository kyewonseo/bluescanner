package net.bluehack.bluescanner;

import android.app.Application;

import net.bluehack.bluescanner.firebase.FirebaseDatabaseHelper;

public class BlueScannerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabaseHelper.getInstance().init(this);


    }
}
