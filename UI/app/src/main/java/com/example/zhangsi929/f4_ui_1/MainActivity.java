package com.example.zhangsi929.f4_ui_1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.zhangsi929.f4_ui_1.R.drawable.bg1;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    //this is the home page of the App, contains four buttons

    Button lostButton;
    Button foundButton;
    Button infoButton;
    Button profileButton;
    String username;
    private String TAG = InfoView.class.getSimpleName();
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lostButton = (Button) findViewById(R.id.LostButton);
        foundButton = (Button) findViewById(R.id.FoundButton);
        infoButton = (Button) findViewById(R.id.InfoButton);
        profileButton = (Button) findViewById(R.id.ProfileButton);
        username = User.username;

        // different buttons going to different views
        lostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLostView = new Intent(getApplicationContext(), LostReport.class);
                startActivity(goToLostView);
            }
        });

        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFoundView = new Intent(getApplicationContext(), FoundReport.class);
                startActivity(goToFoundView);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLostView = new Intent(getApplicationContext(), QueryActivity.class);
                startActivity(goToLostView);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLostView = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(goToLostView);
            }
        });

        //pull the match notification from server per 6 hours
        Global.timer = new Timer();
        Global.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pushMatching();
            }
        },0, 21600000);

    }

    // pushMatching query the server for matching items send notification to user
    private void pushMatching(){
        HttpHandler sh = new HttpHandler();
        User.url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/match/?username=" + User.username;
        String murl = User.url;
        String jsonStr = sh.makeServiceCall(murl);
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(jsonStr);
                JSONArray foundArray = jsonObj.getJSONArray("found_matching");

                JSONArray lostArray = jsonObj.getJSONArray("lost_matching");
                // if any matching item is returned from backend, it will notify users
                if((foundArray.length() != 0) || (lostArray.length() != 0)){
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.login40)
                                    .setContentTitle("NEW MATCH!")
                                    .setContentText("Possible matching items reported, click to check");
                    Intent resultIntent = new Intent(getApplicationContext(), MatchingResults.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addParentStack(MatchingResults.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(bg1, mBuilder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
