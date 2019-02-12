package ru.zagulin.twitterlikeaccountpage;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, new AuthFragment()).commit();


    }
}
