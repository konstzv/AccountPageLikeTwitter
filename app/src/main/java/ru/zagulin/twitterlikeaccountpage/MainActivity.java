package ru.zagulin.twitterlikeaccountpage;

import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements IAuthCallback {




    @Override
    public void onUserLogIn() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container,new ProfileFragment()).commit();
    }

    @Override
    public void onUserLogOut() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container,new AuthFragment()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState==null) {


            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                onUserLogIn();
            } else {
                onUserLogOut();
            }

        }

    }
}
