package ru.zagulin.twitterlikeaccountpage;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;



public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    private static int IMG_REQ_CODE = 17;

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    private ImageView mImageView;

    private String mUserImageLocationInStorage;

    private String mProfileImageLocalFilePath;

    private TextView mEmailTextView;

    private TextView mMTitleTextView;

    private CollapsingToolbarLayout mCollapsingToolbar;

    private AppBarLayout mAppbar;

    private String mUserEmail;

    private ViewPager mViewPager;

    private TabLayout mTabLayout;

    private View mLogOutBtn;

    private String mUserUid;

    private SimpleImageCache mSimpleImageCache;


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
        final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if (currentUser == null) {
            logout();
            return;
        }

        findViews(view);
        setListeners();

        setupTabs();

        mUserUid = currentUser.getUid();
        mUserImageLocationInStorage = String.format("images/profiles/%s.jpg", mUserUid);

        mSimpleImageCache = new SimpleImageCache(getActivity());

        mUserEmail = currentUser.getEmail();
        mEmailTextView.setText(mUserEmail);

        loadAndDisplayProfileImage();

    }

    private void logout() {
        Activity activity = getActivity();
        if (activity instanceof IAuthCallback) {
            ((IAuthCallback) activity).onUserLogOut();
        }
    }


    private void findViews(@NonNull final View view) {
        mImageView = view.findViewById(R.id.fragment_profile_image_view_profile);
        mEmailTextView = view.findViewById(R.id.fragment_profile_text_view_email);
        mMTitleTextView = view.findViewById(R.id.fragment_profile_text_view_title);
        mCollapsingToolbar = view.findViewById(R.id.fragment_profile_toolbar_layout);
        mAppbar = view.findViewById(R.id.fragment_profile_appbar);
        mViewPager = view.findViewById(R.id.fragment_profile_view_pager);
        mTabLayout = view.findViewById(R.id.fragment_profile_tab_layout);
        mLogOutBtn = view.findViewById(R.id.fragment_profile_button_logout);
    }

    private void setListeners() {
        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mFirebaseAuth.signOut();
                logout();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startImageChooser();
            }
        });

        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {

                actionOnOffsetChange(verticalOffset);


            }
        });
    }

    private void actionOnOffsetChange(final int verticalOffset) {
        mAppbar.post(new Runnable() {
            @Override
            public void run() {
                boolean isContentHide =
                        mCollapsingToolbar.getScrimVisibleHeightTrigger() + Math.abs(verticalOffset)
                                > mCollapsingToolbar
                                .getHeight();
                if (isContentHide) {
                    mMTitleTextView.setText(mUserEmail);
                } else {
                    mMTitleTextView.setText("");
                }
                float scaleFactor = 1F - verticalOffset * .005F;
                mImageView.setScaleX(1 / scaleFactor);
                mImageView.setScaleY(1 / scaleFactor);
            }
        });

    }

    private void startImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_profile_picture)),
                IMG_REQ_CODE);
    }

    private void setupTabs() {

        mViewPager.setAdapter(
                new PageAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
            @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ_CODE && resultCode == Activity.RESULT_OK
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
                        mSimpleImageCache.removeFromCache(mUserUid);
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
        Bitmap imageFromCache = mSimpleImageCache.getBitmapFromCache(mUserUid);
        if (imageFromCache != null) {
            mImageView.setImageBitmap(imageFromCache);
            return;
        }

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
                            mSimpleImageCache.saveToCache(mUserUid, mProfileImageLocalFilePath);

                            mImageView
                                    .setImageBitmap(mSimpleImageCache.getBitmapFromCache(mUserUid));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
