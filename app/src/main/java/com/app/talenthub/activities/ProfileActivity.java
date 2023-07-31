package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import com.app.talenthub.R;
import com.app.talenthub.adapter.CustomPagerAdapter;
import com.app.talenthub.databinding.ActivityProfileBinding;
import com.app.talenthub.fragments.ImageFragment;
import com.app.talenthub.fragments.VideoFragment;
import com.app.talenthub.model.User;
import com.app.talenthub.others.Const;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

   ActivityProfileBinding binding;

    DatabaseReference mRef;
    User user;

    int[] images = {R.drawable.ic_action_video, R.drawable.ic_action_image};
    CustomPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserData();


        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                intent.putExtra(Const.KEY_OBJECT, user);
                startActivity(intent);
            }
        });

        binding.ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), SettingActivity.class));

            }
        });

    }

    private void loadFragment() {

        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(VideoFragment.newInstance(user.getId()));
        adapter.addFragment(ImageFragment.newInstance(user.getId()));
        //adapter.addFragment(new VideoFragment());
        //adapter.addFragment(new ImageFragment());
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


    private void getUserData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {

            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Const.NODE_USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    user = snapshot.getValue(User.class);

                    if (user != null) {
                        updateProfile(user);
                        loadFragment();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    private void updateProfile(User user) {

        Glide.with(ProfileActivity.this)
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

    }
}