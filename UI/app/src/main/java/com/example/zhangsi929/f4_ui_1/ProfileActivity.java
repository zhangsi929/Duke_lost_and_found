package com.example.zhangsi929.f4_ui_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {
    // user can check their report history, manully match, edit their password and email and logout

    Button matchButton;
    Button logOutButton;
    Button myReportButton;
    Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        matchButton = (Button) findViewById(R.id.Match);
        logOutButton = (Button) findViewById(R.id.logOut);
        myReportButton = (Button) findViewById(R.id.MyReport);
        editProfileButton = (Button) findViewById(R.id.editProfile);

        // user can manually send matching request by this button
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/match/?username=" + User.username;
                Intent goToLostView = new Intent(getApplicationContext(), MatchingResults.class);
                startActivity(goToLostView);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFoundView = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToFoundView);
            }
        });

        // this button send report history request to backend
        myReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLostView = new Intent(getApplicationContext(), InfoView.class);
                User.url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/reported/?username=" + User.username;
                startActivity(goToLostView);
            }
        });

        //this button goes to edit profile
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLostView = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(goToLostView);
            }
        });

    }
}

