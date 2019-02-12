package ru.zagulin.twitterlikeaccountpage;

import com.google.firebase.FirebaseApp;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
