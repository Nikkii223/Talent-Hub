package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.talenthub.R;
import com.app.talenthub.databinding.ActivityLoginBinding;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);

        binding.btnSignUp.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        binding.btnForgot.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        binding.btnSignIn.setOnClickListener(v -> {

            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            resetFocus();

            if (!isValidEmail(email)) {
                binding.etEmail.setBackgroundResource(R.drawable.et_bg_error);
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
            }else if (!validatePassword(password)) {
                binding.etPassword.setBackgroundResource(R.drawable.et_bg_error);
                Toast.makeText(this, "Enter valid password", Toast.LENGTH_SHORT).show();
            }else {
                loginRequest(email, password);
            }

        });
    }

    private void loginRequest(String email, String password) {

        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    dialog.dismiss();

                    FirebaseUser user = task.getResult().getUser();

                    if(user!=null){

                        String uid = user.getUid();

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        finishAffinity();

                      /*  DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                        mRef.child(Const.NODE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                User user = snapshot.getValue(User.class);
                                if(user!=null){

                                    if(user.getType().equals(Const.KEY_SEEKER)){

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/
                    }

                }

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

        binding.etEmail.setBackgroundResource(R.drawable.et_bg);
        binding.etPassword.setBackgroundResource(R.drawable.et_bg);
    }
}