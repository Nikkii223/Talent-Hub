package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.app.talenthub.R;
import com.app.talenthub.databinding.ActivityProfileEditBinding;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    ActivityProfileEditBinding binding;
    User user;

    int age;
    DatabaseReference mRef;
    Uri imageUri;
    String profileUrl;

    private static final int REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolBar();
        mRef = FirebaseDatabase.getInstance().getReference();

        user = getIntent().getParcelableExtra(Const.KEY_OBJECT);
        if (user != null) {
            updateUI(user);
        }

        binding.ivProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                getImageFromGallery();

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    showAlert();

                } else {
                    // You can directly ask for the permission.
                    ActivityCompat.requestPermissions(ProfileEditActivity.this,
                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            REQUEST_CODE);
                }
            }

        });

        binding.btnEdit.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String bio = binding.etBio.getText().toString().trim();
            String age = binding.etAge.getText().toString().trim();


            resetFocus();

            if (TextUtils.isEmpty(name)) {
                setError(binding.etName);
            } else {
                updateProfile(name, bio, profileUrl);
            }
        });

    }

    private void setError(EditText editText) {
        editText.setBackgroundResource(R.drawable.et_bg_error);
        editText.requestFocus();
    }

    private void resetFocus() {

        binding.etName.setBackgroundResource(R.drawable.et_bg);
        binding.etBio.setBackgroundResource(R.drawable.et_bg);
        binding.etAge.setBackgroundResource(R.drawable.et_bg);
    }

    private void updateUI(User user) {
        Glide.with(getApplicationContext())
                .load(user.getProfileUrl())
                .placeholder(R.drawable.user)
                .into(binding.ivProfile);

        binding.etName.setText(user.getName());
        binding.etUserName.setText(user.getUserName());
        binding.etAge.setText((user.getAge() != 0) ? String.valueOf(user.getAge()) : "");
        binding.etContact.setText(user.getContact());
        binding.etEmail.setText(user.getEmail());
       // binding.etLocation.setText(user.getAddress());
        binding.etBio.setText((user.getAbout() != null) ? user.getAbout() : "");
        binding.tvType.setText("Type : " + user.getType());
        profileUrl = user.getProfileUrl();
        age = user.getAge();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getImageFromGallery();

                }  else {

                    showAlert();
                }

                break;
        }

    }

    private void showAlert() {

        new MaterialAlertDialogBuilder(ProfileEditActivity.this)
                .setTitle("Permission")
                .setMessage("please allow storage permission so that you can get image from gallery")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void getImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GALLERY_IMAGE_REQUEST_CODE:
                if(data!=null && resultCode == RESULT_OK){

                    imageUri = data.getData();
                    //ivProfile.setImageURI(imageUri);

                    uploadImageToStorage(imageUri);
                }

                break;
        }

    }

    String fileName = "";


    private void uploadImageToStorage(Uri imageUri) {
        // progressBar.setVisibility(View.VISIBLE);

        fileName = System.currentTimeMillis() + ".png";     //5654322345.png

        String folderName = "profile";

        StorageReference reference = FirebaseStorage.getInstance().getReference();

        reference.child(folderName).child(fileName)
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.child(folderName).child(fileName).getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        profileUrl = uri.toString();

                                        binding.ivProfile.setImageURI(imageUri);

                                    }
                                });




                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // failure
                // progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void updateProfile(String name, String bio, String profileUrl) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("about", bio);
        map.put("age", (binding.etAge.getText().toString().trim().equals(""))? 0 : Integer.parseInt(binding.etAge.getText().toString().trim()));
        if(profileUrl!=null){
            map.put("profileUrl", profileUrl);
        }

        mRef.child(Const.NODE_USER).child(user.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ProfileEditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }

    private void initToolBar() {

        binding.appBar.toolBar.setTitle("Profile");
        setSupportActionBar(binding.appBar.toolBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}