package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.app.talenthub.R;
import com.app.talenthub.adapter.CustomPagerAdapter;
import com.app.talenthub.databinding.ActivityOtherProfileBinding;
import com.app.talenthub.databinding.ActivityProfileBinding;
import com.app.talenthub.fragments.ImageFragment;
import com.app.talenthub.fragments.VideoFragment;
import com.app.talenthub.model.Content;
import com.app.talenthub.model.Notification;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

public class OtherProfileActivity extends AppCompatActivity {

    ActivityOtherProfileBinding binding;
    DatabaseReference mRef;
    User user;


    int[] images = {R.drawable.ic_action_video, R.drawable.ic_action_image};
    CustomPagerAdapter adapter;
    String uid;
    String key;
    Content content;
    boolean isLoaded = false;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBar.toolBar);

        mRef = FirebaseDatabase.getInstance().getReference();

        key = getIntent().getStringExtra(Const.KEY_STRING);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (id != null) {
            mRef.child(Const.NODE_USER).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        if (user.getType().equals(Const.KEY_RECRUITER))
                            menu.getItem(0).setVisible(true);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (key.equals("search")) {
            user = getIntent().getParcelableExtra(Const.KEY_OBJECT);
            //updateProfile(user);
            //loadFragment(user);
            getUserData(user.getId());
        } else {
            content = getIntent().getParcelableExtra(Const.KEY_OBJECT);
            getUserData(content.getUserId());
        }


        binding.tvFollowUser.setOnClickListener(v -> {
            isLoaded = true;
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (binding.tvFollowUser.getText().toString().trim().equals("Follow")) {
                mRef.child(Const.NODE_FOLLOWING).child(userId).child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.tvFollowUser.setText("Following");
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("following", user.getFollowing() + 1);
                        mRef.child(Const.NODE_USER).child(userId).updateChildren(map);

                        //binding.tvFollower.setText((user.getFollowing()+1) + " Following");
                        updateFollower("+", userId);


                    }
                });
            } else {
                mRef.child(Const.NODE_FOLLOWING).child(userId).child(user.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.tvFollowUser.setText("Follow");

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("following", user.getFollowing() - 1);
                        mRef.child(Const.NODE_USER).child(userId).updateChildren(map);
                        getUserData(user.getId());
                        //binding.tvFollower.setText((user.getFollowing()-1) + " Following");
                        updateFollower("-", userId);

                    }
                });
            }

        });

        // updateUserData();
        // loadFragment();
        //  getUserData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_recruiter, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_request:
                Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
                intent.putExtra(Const.KEY_STRING, user.getId());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFollower(String status, String userId) {


        if (userId != null) {


            mRef.child(Const.NODE_USER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User u = snapshot.getValue(User.class);

                    if (u != null && status.equals("+")) {
                        mRef.child(Const.NODE_FOLLOWER).child(user.getId()).child(u.getId()).setValue(u);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("follower", user.getFollower() + 1);
                        mRef.child(Const.NODE_USER).child(user.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                getUserData(user.getId());

                                addNotification();

                            }
                        });
                    } else {
                        mRef.child(Const.NODE_FOLLOWER).child(user.getId()).child(userId).removeValue();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("follower", user.getFollower() - 1);
                        mRef.child(Const.NODE_USER).child(user.getId()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                getUserData(user.getId());
                                // binding.tvFollower.setText((user.getFollower() - 1) + " Followers");
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

    private void addNotification() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {

            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Const.NODE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User muser = snapshot.getValue(User.class);

                    if (muser != null) {

                        String id = mRef.push().getKey();
                        String message = muser.getName() + " is following you.";

                        Notification notification = new Notification(id, uid, muser.getName(), muser.getProfileUrl(), user.getId(), message, System.currentTimeMillis(), "");
                        mRef.child(Const.NODE_NOTIFICATION).child(content.getUserId()).child(notification.getId()).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    mRef.child(Const.NODE_USER).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            User u = snapshot.getValue(User.class);

                                            if (u != null) {

                                                sendNotification(notification.getMessage(), u.getToken());

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

    public void sendNotification(String message, String token) {


        JSONObject json = new JSONObject();

        try {

            JSONObject notificationJson = new JSONObject();
            notificationJson.put("body", message);
            notificationJson.put("title", "Talent Hub");


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
            }
        });

    }

    private void getUserData(String uid) {

        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {

            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Const.NODE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    user = snapshot.getValue(User.class);

                    if (user != null) {
                        updateProfile(user);


                        if (!isLoaded)
                            loadFragment(user);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loadFragment(User user) {

        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(VideoFragment.newInstance(user.getId()));
        adapter.addFragment(ImageFragment.newInstance(user.getId()));
        //adapter.addFragment(UserMusicFragment.newInstance(new PrefManager(ProfileActivity.this).getUserDetail().getId()));

        binding.fragmentPager.setAdapter(adapter);
        binding.fragmentPager.setOffscreenPageLimit(3);
        binding.tabLayout.setupWithViewPager(binding.fragmentPager);

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            binding.tabLayout.getTabAt(i).setIcon(images[i]);
        }

        binding.tabLayout.getTabAt(binding.tabLayout.getSelectedTabPosition()).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Fragment fragment = adapter.getItem(tab.getPosition());

              /*  if (fragment instanceof UserPostFragment) {
                    ((UserPostFragment) fragment).stopPlaying();
                }*/

                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_500), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void updateProfile(User user) {

        Glide.with(OtherProfileActivity.this)
                .load(user.getProfileUrl())
                .placeholder(R.drawable.user)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.circleImageView);

        binding.tvName.setText(user.getName());
        binding.tvUserId.setText(user.getUserName());
        binding.tvUserAbout.setText((user.getAbout() != null) ? user.getAbout() : "");
        binding.tvFollower.setText(user.getFollower() + " Followers");
        binding.tvPost.setText(user.getPost() + " Post");
        binding.tvFollowing.setText(user.getFollowing() + " Following");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRef.child(Const.NODE_FOLLOWING).child(userId).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0) {
                    binding.tvFollowUser.setText("Following");
                } else {
                    binding.tvFollowUser.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}