package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.app.talenthub.R;
import com.app.talenthub.databinding.ActivitySignUpBinding;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    token = task.getResult();
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        binding.btnSignUp.setOnClickListener(v -> {
            signUp();
        });

        binding.tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
        });

    }

    private void signUp() {

        String userName = binding.etUserName.getText().toString().trim();
        String name = binding.etName.getText().toString().trim();
        String compName = binding.etCompanyName.getText().toString().trim();
        String contact = binding.etContact.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        resetFocus();

        if (TextUtils.isEmpty(userName)) {
            setError(binding.etUserName);
        } else if (TextUtils.isEmpty(name)) {
            setError(binding.etName);
        } else if (TextUtils.isEmpty(contact)) {
            setError(binding.etContact);
        } else if (type.equals("Recruiter") && TextUtils.isEmpty(compName)) {
            setError(binding.etCompanyName);
        } else if (!isValidEmail(email)) {
            setError(binding.etEmail);
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
        } else if (!validatePassword(password)) {
            setError(binding.etPassword);
            Toast.makeText(this, "Password should be between 6-12 of characters", Toast.LENGTH_SHORT).show();
        } else {

            createAccount(name, email, contact, password, compName, userName);
        }

    }

    private void createAccount(String name, String email, String contact, String password, String compName, String userName) {

        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                FirebaseUser mUser = task.getResult().getUser();
                String userId = mUser.getUid();

                User user = new User(userId, name, email, contact, type, compName, System.currentTimeMillis(), userName, token);

                mRef.child(Const.NODE_USER).child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {


                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finishAffinity();
                            Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else {
                // failed
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void setError(View editText) {
        editText.setBackgroundResource(R.drawable.et_bg_error);
        editText.requestFocus();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if (password.length() >= 6 && password.length() <= 12)
            return true;
        else
            return false;
    }

    private void resetFocus() {

        binding.etUserName.setBackgroundResource(R.drawable.et_bg);
        binding.etName.setBackgroundResource(R.drawable.et_bg);
        binding.etContact.setBackgroundResource(R.drawable.et_bg);
        binding.etEmail.setBackgroundResource(R.drawable.et_bg);
        binding.etPassword.setBackgroundResource(R.drawable.et_bg);
        binding.etCompanyName.setBackgroundResource(R.drawable.et_bg);


    }

    private String type = Const.KEY_SEEKER;

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rb_recruiter:
                if (checked) {
                    binding.etCompanyName.setVisibility(View.VISIBLE);
                    type = Const.KEY_RECRUITER;
                }


                break;
            case R.id.rb_seeker:
                if (checked) {
                    binding.etCompanyName.setVisibility(View.GONE);
                    type = Const.KEY_SEEKER;
                }

                break;

        }
    }
}