package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.talenthub.R;
import com.app.talenthub.databinding.ActivityRequestBinding;
import com.app.talenthub.model.Comment;
import com.app.talenthub.model.Notification;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestActivity extends AppCompatActivity {

    ActivityRequestBinding binding;
    String receiverId, userId;
    DatabaseReference mRef;
    String message, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mRef = FirebaseDatabase.getInstance().getReference();
        receiverId = getIntent().getStringExtra(Const.KEY_STRING);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = binding.etTitle.getText().toString().trim();
                message = binding.etMessage.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(RequestActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(message)) {
                    Toast.makeText(RequestActivity.this, "Enter your message", Toast.LENGTH_SHORT).show();

                } else {

                    mRef = FirebaseDatabase.getInstance().getReference();
                    mRef.child(Const.NODE_USER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            User user = snapshot.getValue(User.class);

                            if (user != null) {

                                String nId = mRef.push().getKey();
                                String msg =  message ;

                                Notification notification = new Notification(nId, user.getId(), user.getName(), user.getProfileUrl(), receiverId, msg, System.currentTimeMillis(), "");
                                mRef.child(Const.NODE_NOTIFICATION).child(receiverId).child(notification.getId()).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            mRef.child(Const.NODE_USER).child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    User u = snapshot.getValue(User.class);

                                                    if (u != null) {

                                                        sendNotification(user.getName(),message, u.getToken());

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }

                                    }
                                });


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


            }
        });


    }

    public void sendNotification(String title, String message, String token) {


        JSONObject json = new JSONObject();

        try {

            JSONObject notificationJson = new JSONObject();
            notificationJson.put("body", message);
            notificationJson.put("title", title);


            json.put("data", notificationJson);
            json.put("notification", notificationJson);
            json.put("to", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .header("Authorization", "key=" + "AAAAuU89TXM:APA91bEH3i64r83_t6dJEjWKH23PQy8pNJuMr0dS2Yl8rsgMSu3vdtmUfSnFHqAJtACOgHTYjw84lSLRMsF9qrjaqLnOD0AfuxScTcPat0l6h5G6He4KjjV4JGV2R0k6jOGiSvE_KpKC")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("FCM_MESSAGE", "remoteMessage");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("FCM_MESSAGE", "remoteMessage");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.etTitle.setText("");
                        binding.etMessage.setText("");
                        Toast.makeText(RequestActivity.this, "request send successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}