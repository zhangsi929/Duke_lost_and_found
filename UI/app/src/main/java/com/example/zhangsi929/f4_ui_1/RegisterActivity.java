package com.example.zhangsi929.f4_ui_1;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import android.app.AlertDialog;
public class RegisterActivity extends AppCompatActivity {
    //this class handle the user register request

    public String names;
    public String usernames;
    public String passwords;
    public String emails;
    public String phones;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    public String response;
    public int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText normalName = (EditText) findViewById(R.id.normalName);
        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.passWord);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText phone = (EditText) findViewById(R.id.phone);
        final Button bRegister = (Button) findViewById(R.id.buttonRegister);
        class AsyncT extends AsyncTask<Object, Object, String> {
            //parse user input to json and send backend
            @Override
            protected String doInBackground(Object... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", names);
                    jsonObject.put("username", usernames);
                    jsonObject.put("password", passwords);
                    jsonObject.put("email", emails);
                    jsonObject.put("phone", phones);
//                    System.out.println(jsonObject.toString());
                    response = post("http://colab-sbx-pvt-10.oit.duke.edu:8000/accounts/signup", jsonObject.toString());
//                    System.out.println(response);
                    if(response.equals("{\"status\": \"success\"}")){
                        flag=1;
                    } else {
                        flag=0;
                    }
                } catch (MalformedURLException e) {
//                    System.out.println("error1");
                    e.printStackTrace();
                } catch (IOException e) {
//                    System.out.println("error2");
                    e.printStackTrace();
                } catch (JSONException e) {
//                    System.out.println("error3");
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
        bRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                names = normalName.getText().toString();
                usernames = userName.getText().toString();
                passwords = password.getText().toString();
                emails = email.getText().toString();
                phones = phone.getText().toString();
                //register input validation
                if (names.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Invalid name");
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

                else if (usernames.equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Invalid username");
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

                else if (passwords.equals("") || passwords.length() < 6) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Password should be at least more than 6 characters");
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
                else if(emails.equals("") || !Patterns.EMAIL_ADDRESS.matcher(emails).matches()){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Invalid email address");
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
                else if(phones.equals("") || !checkDigit(phones) || !Patterns.PHONE.matcher(phones).matches()){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("Invalid phone number");
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
                }

                // alert pop up
                if (flag==0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("username has been used");
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
                }else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("register success");
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
            }
        });
    }

    // check if any no-number in the string
    private boolean checkDigit(String str){
        for(int i = 0; i < str.length(); i++){
            if ((str.charAt(i) > '9') || (str.charAt(i) < '0')) {
                return false;
            }
        }
        return true;
    }
}

