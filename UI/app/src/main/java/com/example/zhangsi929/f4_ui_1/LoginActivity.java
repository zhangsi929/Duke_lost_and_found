package com.example.zhangsi929.f4_ui_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    //this class handles user login request

    public String usernames;
    public String passwords;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    public String response;
    public int flag = 0;
    private VideoView mVideoView;
    public static String url1;

    //the following method is to set up background animation
    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null) {
            mVideoView.resume();
            mVideoView.start();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // set up video view for login background
        mVideoView = (VideoView) findViewById(R.id.bgVideoView);
        Uri uri  = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.abc);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.passWord);
        final Button bLogin = (Button) findViewById(R.id.LoginButton);
        final Button toRegister = (Button) findViewById(R.id.toRegister);
        final Button toQuery = (Button) findViewById(R.id.query);

        // report Json data to sever and check the account information for login
        class AsyncT extends AsyncTask<Object, Object, String> {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", usernames);
                    jsonObject.put("password", passwords);
                    //System.out.println(jsonObject.toString());
                    response = post("http://colab-sbx-pvt-10.oit.duke.edu:8000/accounts/login", jsonObject.toString());
                    //System.out.println(response);
                    if(response.equals("{\"status\": \"success\"}")){
                        flag=1;
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(mainIntent);
                    } else {
                        flag=0;
                    }
                } catch (MalformedURLException e) {
                    //System.out.println("error1");
                    e.printStackTrace();
                } catch (IOException e) {
                    //System.out.println("error2");
                    e.printStackTrace();
                } catch (JSONException e) {
                    //System.out.println("error3");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            String post(String url, String json) throws IOException, InterruptedException {
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Thread.sleep(200);
                return response.body().string();
            }
        }
        // buttons for going to different views
        toQuery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent queryIntent = new Intent(LoginActivity.this, QueryActivity.class);
                startActivity(queryIntent);
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                usernames = userName.getText().toString();
                passwords = password.getText().toString();
                //username validation
                if (usernames.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("You have to input the username");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }
                //password and username validation
                else if (passwords.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("You have to input the password");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }
                AsyncT asyncT = new AsyncT();
                asyncT.execute();
                try {
                    Thread.sleep(400);
                } catch (java.lang.InterruptedException e) {
                    System.out.println("error");
                }
                if (flag==0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("Login failed");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder1.create();
                    dialog.show();
                }
                User.username = usernames;
            }
        });

        // set up a loop for video
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer){
                mediaPlayer.setLooping(true);
            }
        });
    }

}
