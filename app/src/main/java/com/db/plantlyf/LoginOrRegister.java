package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Utilities.BitmapStringConverter;
import com.db.plantlyf.databinding.ActivityLoginOrRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginOrRegister extends AppCompatActivity {

    private ActivityLoginOrRegisterBinding binding;
    private boolean onResumeFlag = false;
    private String navigation = "LoginOrRegister";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private final int PERMISSION_READ_EXTERNAL_STORAGE = 100;
    private final int PERMISSION_READ_MEDIA_IMAGES = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOrRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        startBgVideo();
        startBtnListeners();

    }

    private void startBtnListeners() {

        //LOGIN OR REGISTER PAGE------------------------------------------------------
        binding.loginOrRegisterLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage();
            }
        });

        binding.loginOrRegisterRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterPage();
            }
        });

        //LOGIN PAGE---------------------------------------------------------------------
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginAuth();
                Toast.makeText(LoginOrRegister.this, "LOGIN", Toast.LENGTH_SHORT).show();
            }
        });


        binding.loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterPage();
            }
        });

        //REGISTER PAGE--------------------------------------------------------------
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegisterAuth();
//                startSetProfilePicturePage();
            }
        });

        binding.registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage();
            }
        });

        //SET PROFILE PICTURE PAGE--------------------------------------------------------
        binding.addProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginOrRegister.this, "Profile photo button clicked", Toast.LENGTH_SHORT).show();

                setProfilePicture();

            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegisterProfilePictureAuth();

            }
        });

    }

    //LOGIN OR REGISTER PAGE--------------------------------------
    private void startLoginRegisterPage(){

        if(navigation.equals("Login"))
            binding.loginContainerLL.animate().alpha(0).setDuration(500).start();
        else if(navigation.equals("Register"))
            binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "LoginOrRegister";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginContainerLL.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.GONE);
                binding.loginOrRegisterBtnLL.setVisibility(View.VISIBLE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.VISIBLE);
                binding.loginOrRegisterAppNameTV.animate().alpha(1).setDuration(500).start();
                binding.loginOrRegisterBtnLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    //REGISTER PAGE-----------------------------------------------
    private void startRegisterPage() {

        if(navigation.equals("LoginOrRegister")) {
            binding.loginOrRegisterAppNameTV.animate().alpha(0).setDuration(500).start();
            binding.loginOrRegisterBtnLL.animate().alpha(0).setDuration(500).start();
        }
        else if(navigation.equals("Login"))
            binding.loginContainerLL.animate().alpha(0).setDuration(500).start();
        else if(navigation.equals("SetProfilePicture"))
            binding.setProfilePictureContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "Register";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginOrRegisterBtnLL.setVisibility(View.GONE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.GONE);
                binding.loginContainerLL.setVisibility(View.GONE);
                binding.setProfilePictureContainerLL.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.VISIBLE);
                binding.registerContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);
    }

    private void userRegisterAuth() {
        initializeFirebaseAuth();

        if(!(TextUtils.isEmpty(binding.registerNameET.getText())
        || TextUtils.isEmpty(binding.registerEmailET.getText())
        || TextUtils.isEmpty(binding.registerPasswordET.getText())
        || TextUtils.isEmpty(binding.registerConfirmpwET.getText()))){

            String userFullName = String.valueOf(binding.registerNameET.getText());
            String userEmail = String.valueOf(binding.registerEmailET.getText());
            String userPassword = String.valueOf(binding.registerPasswordET.getText());
            String userConfirmPassword = String.valueOf(binding.registerConfirmpwET.getText());

            if (invalidUserName(userFullName)) {
                Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            } else if (invalidUserEmail(userEmail)) {
                Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
            } else if (invalidUserPassword(userPassword)) {
                Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show();
            } else if (!userPassword.equals(userConfirmPassword)) {
                Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d("Firebase Authentication : ", authResult.getUser().getUid());

                                Data.UID = authResult.getUser().getUid();
                                Data.USER_FULLNAME = userFullName;
                                Data.USER_EMAIL = userEmail;
                                Data.USER_PASSWORD = userPassword;

                                startSetProfilePicturePage();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginOrRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean invalidUserPassword(String userPassword) {
        return false;
    }

    private boolean invalidUserEmail(String userEmail) {
        return false;
    }

    private boolean invalidUserName(String userFullName) {
        return false;
    }

    //SET PROFILE PICTURE PAGE------------------------------------
    private final int PICK_IMAGE_REQUEST = 1;

    private void startSetProfilePicturePage(){

//        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.default_profile_picture);
//        Canvas canvas = new Canvas();
//        assert drawable != null;
//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        Data.USER_PROFILE_PICTURE = bitmap;
//        Log.d("PLANTLYF : ", "Set Profile Picture Page : Profile picture = " + Data.USER_PROFILE_PICTURE.toString());

        binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "SetProfilePicture";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.registerContainerLL.setVisibility(View.GONE);
                binding.setProfilePictureContainerLL.setVisibility(View.VISIBLE);
                binding.setProfilePictureContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    private void userRegisterProfilePictureAuth() {

        initializeFirebaseFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("full_name", Data.USER_FULLNAME);
        data.put("email", Data.USER_EMAIL);
        if(Data.USER_PROFILE_PICTURE.equals("no_profile_picture")) {
            //TODO: dialog to register with no profile picture
            data.put("profile_picture", Data.USER_PROFILE_PICTURE);
        }
        else{
            data.put("profile_picture", Data.USER_PROFILE_PICTURE);
        }


        firebaseFirestore.collection("userData")
                .document(Data.UID).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginOrRegister.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginOrRegister.this, Dashboard.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginOrRegister.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
                    }
                });
    }

    private void setProfilePicture() {
        checkPerm();
    }

    private void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                Data.USER_PROFILE_PICTURE = BitmapStringConverter.bitmapToString(bitmap);
                binding.userProfilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("Image picker : ", e.getLocalizedMessage());
            }
        }
    }

    private void checkPerm(){
        // Check for permission
        if(Data.ANDROIDSDKVERSION <= 32) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            else
                openImagePicker();
        }
        else{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_READ_MEDIA_IMAGES);
            else
                openImagePicker();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE
                || requestCode == PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openImagePicker();
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

        }
    }

    //LOGIN PAGE---------------------------------------------------
    private void startLoginPage() {

        if(navigation.equals("LoginOrRegister")) {
            binding.loginOrRegisterAppNameTV.animate().alpha(0).setDuration(500).start();
            binding.loginOrRegisterBtnLL.animate().alpha(0).setDuration(500).start();
        }
        else if(navigation.equals("Register"))
            binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "Login";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginOrRegisterBtnLL.setVisibility(View.GONE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.GONE);
                binding.loginContainerLL.setVisibility(View.VISIBLE);
                binding.loginContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    private void userLoginAuth() {

        initializeFirebaseAuth();

        if(!(TextUtils.isEmpty(binding.loginEmailET.getText())
                || TextUtils.isEmpty(binding.loginPasswordET.getText()))){

            String userEmail = String.valueOf(binding.loginEmailET.getText());
            String userPassword = String.valueOf(binding.loginPasswordET.getText());

            if (invalidUserEmail(userEmail)) {
                Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
            } else if (invalidUserPassword(userPassword)) {
                Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show();
            }  else {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Log.d("PLANTLYF : ", "Login Page : User uid = " + authResult.getUser().getUid());
                                        Data.UID = authResult.getUser().getUid();
                                        Data.USER_EMAIL = userEmail;
                                        Data.USER_PASSWORD = userPassword;

                                        initializeFirebaseFirestore();

                                        firebaseFirestore.collection("userData")
                                                .document(Data.UID).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Data.USER_FULLNAME = documentSnapshot.getString("full_name");
                                                        Data.USER_PROFILE_PICTURE =  documentSnapshot.getString("profile_picture");

                                                        Intent intent = new Intent(LoginOrRegister.this, Dashboard.class);
                                                        startActivity(intent);
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginOrRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                startSetProfilePicturePage();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        }
        else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }


    }

    //GLOBAL------------------------------------------------------
    private void initializeFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initializeFirebaseFirestore(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void startBgVideo(){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        //Toast.makeText(this, dpHeight + "," + dpWidth, Toast.LENGTH_SHORT).show();

        VideoView videoView = binding.LoginOrRegisterBgVV;
        ImageView imageView = binding.TempBgImageIV;
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = imageView.getLayoutParams();
        layoutParams.width = (int)width + ((int)((width * 37.0) / 100.0));
        layoutParams1.width = (int)width + ((int)((width * 37.0) / 100.0));
        videoView.setLayoutParams(layoutParams);
        imageView.setLayoutParams(layoutParams1);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfbganim);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if(navigation.equals("LoginOrRegister")) {
            super.onBackPressed();
        }
        else if(navigation.equals("SetProfilePicture")){
            startRegisterPage();
        }
        else {
            startLoginRegisterPage();
        }
    }

    @Override
    protected void onResume() {

        if(onResumeFlag)
            startBgVideo();
        else
            onResumeFlag = true;
        super.onResume();
    }
}