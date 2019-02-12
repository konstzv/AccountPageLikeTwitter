package ru.zagulin.twitterlikeaccountpage;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AuthFragment extends Fragment {

    private static final String TAG = "AuthFragment";

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button mLoginBtn;

    private Button mRegisterBtn;

    private Button mResetPswrdBtn;

    private TextView mPasswordTextView;

    private TextView mEmailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setListeners();
    }

    private void setListeners() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String password = mPasswordTextView.getText().toString().trim();
                String email = mEmailTextView.getText().toString().trim();
                login(email, password);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String password = mPasswordTextView.getText().toString().trim();
                String email = mEmailTextView.getText().toString().trim();
                register(email, password);
            }
        });

        mResetPswrdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = mEmailTextView.getText().toString().trim();
                resetPassword(email);
            }
        });
    }

    private void resetPassword(@NonNull String email) {

        if (checkIfEmailCorrectElseShowError(email)) {
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull final Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Password reset success");
                } else {
                    Log.d(TAG, "Password reset failure");
                }
            }
        });

    }

    private void login(@NonNull String email, @NonNull String password) {

        if (!checkIfEmailCorrectElseShowError(email)) {
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showMsg(getString(R.string.password_is_empty));
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login success");
                        } else {
                            Log.d(TAG, "Login failure");
                        }
                    }
                });
    }


    private void register(@NonNull String email, @NonNull String password) {
        if (checkIfEmailCorrectElseShowError(email)) {
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showMsg(getString(R.string.password_is_empty));
            return;
        }

        if (password.length() < 6) {
            showMsg(getString(R.string.the_password_is_too_short));
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Register success");
                        } else {
                            Log.d(TAG, "Register failure");
                        }
                    }
                });

    }

    private boolean checkIfEmailCorrectElseShowError(@NonNull final String email) {
        if (TextUtils.isEmpty(email)) {
            showMsg(getString(R.string.email_is_empty));
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMsg(getString(R.string.email_is_incorrect));
            return false;
        }
        return true;
    }


    private void findViews(@NonNull final View view) {
        mEmailTextView = view.findViewById(R.id.fragment_auth_text_view_email);
        mPasswordTextView = view.findViewById(R.id.fragment_auth_text_view_password);

        mLoginBtn = view.findViewById(R.id.fragment_auth_btn_login);
        mRegisterBtn = view.findViewById(R.id.fragment_auth_btn_register);
        mResetPswrdBtn = view.findViewById(R.id.fragment_auth_btn_reset_pwrd);
    }

    private void showMsg(@NonNull String msg) {
        if (getActivity() == null) {
            Log.e(TAG, "Activity is null");
            return;
        }
        Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .show();
    }
}
