package ru.zagulin.twitterlikeaccountpage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    private static int IMG_REQ_CODE = 17;

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    private ImageView mImageView;

    private String mUserImageLocationInStorage;

    private String mProfileImageLocalFilePath;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageView = view.findViewById(R.id.fragment_profile_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_profile_picture)),
                        IMG_REQ_CODE);
            }
        });

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Activity activity = getActivity();
            if (activity instanceof IAuthCallback) {
                ((IAuthCallback) activity).onUserLogOut();
            }
            return;
        }

        mUserImageLocationInStorage = String.format("images/profiles/%s.jpg", currentUser.getUid());
        loadAndDisplayProfileImage();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
            @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            uploadUserProfileImage(filePath);
        }
    }

    private void uploadUserProfileImage(final Uri filePath) {
        StorageReference profileRef = mFirebaseStorage.getReference(mUserImageLocationInStorage);
        profileRef.putFile(filePath).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Loading succesed");
                        loadAndDisplayProfileImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Log.d(TAG, "Loading failed");
            }
        });
    }

    private void loadAndDisplayProfileImage() {
        if (mUserImageLocationInStorage == null) {
            return;
        }
        StorageReference ref = mFirebaseStorage.getReference().child(mUserImageLocationInStorage);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            mProfileImageLocalFilePath = localFile.getAbsolutePath();
                            Bitmap imageBitmap = BitmapFactory
                                    .decodeFile(mProfileImageLocalFilePath);
                            mImageView.setImageBitmap(imageBitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
