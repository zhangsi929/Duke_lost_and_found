package com.example.zhangsi929.f4_ui_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class EditProfileActivity extends AppCompatActivity {

    public EditText updatePassword;
    public EditText updateEmail;
    public String updatedPassword;
    public String updatedEmail;
    public int sFlag = 0;
    public String rep;
    Button submit;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        CharSequence name = "username";
        updatePassword = (EditText) findViewById(R.id.updatePassword);
        updateEmail = (EditText) findViewById(R.id.updateEmail);
        submit = (Button) findViewById(R.id.update);
        //put jason data to sever and check the response from server
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedPassword = updatePassword.getText().toString();
                updatedEmail = updateEmail.getText().toString();
                if(updatedPassword.equals("")  && updatedEmail.equals("")){
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
                    builder2.setMessage("Please input your new password or email address");
                    builder2.setCancelable(true);
                    builder2.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder2.create();
                    dialog.show();
                    return;
                }

                EditProfileActivity.AsyncT asyncT = new EditProfileActivity.AsyncT();
                asyncT.execute();
                try {
                    checkBack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //function for reporting jason data to sever and update the account information in backend
    class AsyncT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            URL url = null;
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("username", User.username);

                if (!updatedEmail.equals("")) {
                    jsonObject.put("email", updatedEmail);
                }

                if (!updatedPassword.equals("")) {
                    jsonObject.put("password", updatedPassword);
                }
                rep = post("http://colab-sbx-pvt-10.oit.duke.edu:8000/app/profile/update", jsonObject.toString());
                if(rep.equals("{\"status\": \"success\"}")) {
                    sFlag = 1;
                }else {
                    sFlag = 0;
                }

            } catch (MalformedURLException e) {
                System.out.println("error1");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("error2");
                e.printStackTrace();
            } catch (JSONException e) {
                System.out.println("error3");
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
            sleep(200);
            return response.body().string();
        }

    }
    //function for checking the response from server
    void checkBack() throws InterruptedException {
        sleep(400);
        if(sFlag == 1) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
            builder2.setMessage("Profile updated successfully");
            builder2.setCancelable(true);
            builder2.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AlertDialog dialog = builder2.create();
            dialog.show();
        }
        else{
            AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
            builder2.setMessage("Failed to update profile");
            builder2.setCancelable(true);
            builder2.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder2.create();
            dialog.show();
        }
        return;
    }

}

