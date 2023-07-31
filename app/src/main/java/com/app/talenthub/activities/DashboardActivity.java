package com.app.talenthub.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.app.talenthub.R;
import com.app.talenthub.databinding.ActivityDashboardBinding;
import com.app.talenthub.fragments.ContentFragment;
import com.app.talenthub.fragments.HomeFragment;
import com.app.talenthub.fragments.NotificationFragment;
import com.app.talenthub.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;
    public static final String TAG_VIDEO = "video";
    public static final String TAG_SEARCH = "search";
    public static final String TAG_ADD_VIDEO = "add_video";
    public static final String TAG_NOTIFICATION = "notification";
    public static final String TAG_PROFILE = "profile";
    public static String CURRENT_TAG = TAG_VIDEO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addFragment(new HomeFragment(), TAG_VIDEO);


        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //  Toast.makeText(MainActivity.this, ""+item.getTitle(), Toast.LENGTH_SHORT).show();

                switch (item.getItemId())
                {
                    case R.id.action_home:
                        CURRENT_TAG = TAG_VIDEO;
                        addFragment(new HomeFragment(), TAG_VIDEO);
                        break;
                    case R.id.action_search:
                        CURRENT_TAG = TAG_SEARCH;
                        addFragment(new SearchFragment(), TAG_SEARCH);
                        break;
                    case R.id.action_notification:
                        CURRENT_TAG = TAG_NOTIFICATION;
                        addFragment(new NotificationFragment(), TAG_NOTIFICATION);
                        break;
                    case R.id.action_add:
                        CURRENT_TAG = TAG_ADD_VIDEO;
                        addFragment(new ContentFragment(), TAG_ADD_VIDEO);
                        break;
                    case R.id.action_profile:
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (CURRENT_TAG) {
            case TAG_VIDEO:
                binding.bottomNavigation.getMenu().findItem(R.id.action_home).setChecked(true);
                break;
            case TAG_SEARCH:
                CURRENT_TAG = TAG_SEARCH;
                binding.bottomNavigation.getMenu().findItem(R.id.action_search).setChecked(true);
                break;
            case TAG_ADD_VIDEO:
                CURRENT_TAG = TAG_ADD_VIDEO;
                binding.bottomNavigation.getMenu().findItem(R.id.action_add).setChecked(true);
                break;
            case TAG_NOTIFICATION:
                CURRENT_TAG = TAG_NOTIFICATION;
                binding.bottomNavigation.getMenu().findItem(R.id.action_notification).setChecked(true);
                break;
        }

    }

    private void addFragment(Fragment fragment, String tag)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();

       // getSupportActionBar().setTitle(tag);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}