package ru.zagulin.twitterlikeaccountpage;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IAuthCallback {

    @Override
    public void onUserLogIn() {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, new ProfileFragment()).commit();
    }

    @Override
    public void onUserLogOut() {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, new AuthFragment()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            onUserLogIn();
        }else{
            onUserLogOut();
        }
        setContentView(R.layout.activity_main);


    }
}
