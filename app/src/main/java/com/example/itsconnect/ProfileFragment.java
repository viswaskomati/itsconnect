package com.example.itsconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.example.itsconnect.model.UserModel;
//import com.example.itsconnect.utils.AndroidUtil;
//import com.example.itsconnect.utils.FirebaseUtil;
//import com.github.dhaval2404.imagepicker.ImagePicker;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.messaging.FirebaseMessaging;


import com.example.itsconnect.Model.UserModel;
import com.example.itsconnect.utilities.Androidutil;
import com.example.itsconnect.utilities.Firebaseutil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
//                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profle_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        logoutBtn.setOnClickListener((v)-> {
            Firebaseutil.logout();
            Intent intent = new Intent(getContext(), openpage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

//            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Firebaseutil.logout();
//                        Intent intent = new Intent(getContext(),openpage.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                }
//            });
//
//
//
        });


//        profilePic.setOnClickListener((v)->{
//            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
//                    .createIntent(new Function1<Intent, Unit>() {
//                        @Override
//                        public Unit invoke(Intent intent) {
//                            imagePickLauncher.launch(intent);
//                            return null;
//                        }
//                    });
//        });
//        getUserData();

        return view;
    }


    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);


//        if (selectedImageUri != null) {
//            Firebaseutil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
//                    .addOnCompleteListener(task -> {
//                        updateToFirestore();
//                    });
//        } else {
        updateToFirestore();
    }



//
//

    //
    void updateToFirestore () {
        Firebaseutil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Androidutil.showToast(getContext(), "Updated successfully");
                    } else {
                        Androidutil.showToast(getContext(), "Updated failed");
                    }
                });
    }



    void getUserData(){
        setInProgress(true);

//        Firebaseutil.getCurrentProfilePicStorageRef().getDownloadUrl()
//                        .addOnCompleteListener(task -> {
//                                if(task.isSuccessful()){
//                                    Uri uri  = task.getResult();
//                                    AndroidUtil.setProfilePic(getContext(),uri,profilePic);
//                                }
//                        });

        Firebaseutil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());

        });//this will take time

    }


    void setInProgress ( boolean inProgress){
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}
