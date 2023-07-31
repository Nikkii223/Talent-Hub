package com.app.talenthub.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.app.talenthub.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        listView = findViewById(R.id.listview);

        String options[] = {"About us","Privacy policy","Share this app","Logout"};
        ArrayAdapter<String> optionAdp = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,options);
        listView.setAdapter(optionAdp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                    Toast.makeText(SettingActivity.this, "About us", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),AboutUsActivity.class);
                    startActivity(intent);
                }
                if(position==1)
                {
                    Toast.makeText(SettingActivity.this, "Privacy policy", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),PrivacyPolicyActivity.class);
                    startActivity(intent);
                }
                if(position==2)
                {
                    Toast.makeText(SettingActivity.this, "share", Toast.LENGTH_SHORT).show();
                    Intent shareIntent =   new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
                    String app_url = " https://play.google.com/store/apps/details?id=com.app.talenthub";
                    shareIntent.putExtra(Intent.EXTRA_TEXT,app_url);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
                if(position==3){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finishAffinity();
                }

            }
        });
    }
}