package com.example.zhangsi929.f4_ui_1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class LostReport extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    //this class allows users to report the lost item

    private static Button date, time;
    private static TextView set_date, set_time;
    private static final int Date_id = 0;
    private static final int Time_id = 1;
    Button submit1;
    public EditText itemNameInput1;
    public EditText descInput1;
    public EditText locationInput1;
    public TextView reportTime1;
    public int theday;
    public int theyear;
    public int themonth;
    public int thehour;
    public int themin;
    public String item_name;
    public String lost_time;
    public String lost_location;
    public String lost_desc;
    public String user;
    public int lflag = 0;
    public Integer positionStringLost;
    public String positionSLost = "0";

    //add image
    public String encodedImage;
    private final int PICK_IMAGE = 12345;
    private Bitmap bitmap;
    private ImageView imageView;
    private Button fromGallery;
    //==========
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    static Semaphore semaphore = new Semaphore(0);

    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        String sSelected = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, sSelected, Toast.LENGTH_SHORT).show();
        positionStringLost = position - 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_report);
        // add image
        imageView = (ImageView) findViewById(R.id.upload_img);
        fromGallery = (Button) findViewById(R.id.upload_btn);
        fromGallery.setOnClickListener(this);
        //=========
        itemNameInput1 = (EditText) findViewById(R.id.itemText1);
        descInput1 = (EditText) findViewById(R.id.descText1);
        locationInput1 = (EditText) findViewById(R.id.locationText1);

        Spinner category = (Spinner) findViewById(R.id.categoryLost);
        ArrayAdapter<String> categoryArray = new ArrayAdapter<String>(LostReport.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        categoryArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryArray);
        category.setOnItemSelectedListener(this);

        submit1 = (Button) findViewById(R.id.LostSubmit);
        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = User.username;
                if (itemNameInput1.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LostReport.this);
                    builder1.setMessage("You have to write the item name");
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
                } else if (set_date.getText().toString().equals("") || set_time.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LostReport.this);
                    builder1.setMessage("you need to select time and date");
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

                if (positionStringLost == 6 || positionStringLost == -1) { //gai2
                    positionSLost = "";
                } else {
                    positionSLost = positionStringLost.toString();
                }

                item_name = itemNameInput1.getText().toString();
                lost_desc = descInput1.getText().toString().equals("") ? "Not Available" : descInput1.getText().toString();
                lost_location = locationInput1.getText().toString().equals("") ? "Not Available" : locationInput1.getText().toString();
                LostReport.AsyncT asyncT = new LostReport.AsyncT();
                asyncT.execute();
                try {
                    checkBack();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });



        /*======================Time Picker View==============================*/
        date = (Button) findViewById(R.id.selectdate);
        time = (Button) findViewById(R.id.selecttime);
        set_date = (TextView) findViewById(R.id.set_date);

        set_time = (TextView) findViewById(R.id.set_time);
        date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Show Date dialog
                showDialog(Date_id);

            }
        });
        time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Show time dialog
                showDialog(Time_id);
            }
        });
    }
    // add image
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_btn:
                getImageFromGallery();
                break;
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }
//=============

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    //show picture on screen here
                    // imageView.setImageBitmap(bitmap);
                    //convert to string
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    //convert string to image
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //=============

    protected Dialog onCreateDialog(int id) {

        // Get the calander
        Calendar c = Calendar.getInstance();

        // From calander get the year, month, day, hour, minute
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        switch (id) {
            case Date_id:

                // Open the datepicker dialog
                return new DatePickerDialog(LostReport.this, date_listener, year,
                        month, day);
            case Time_id:

                // Open the timepicker dialog
                return new TimePickerDialog(LostReport.this, time_listener, hour,
                        minute, false);

        }
        return null;
    }



    // Date picker dialog
    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // store the data in one string and set it to text
            String date1 = String.valueOf(month + 1) + "/" + String.valueOf(day)
                    + "/" + String.valueOf(year);
            set_date.setText(date1);

            theyear = year;
            themonth = month;
            theday = day;

        }
    };
    TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            // store the data in one string and set it to text
            String time1 = String.valueOf(hour) + ":" + String.valueOf(minute);
            set_time.setText(time1);
            thehour = hour;
            themin = minute;
        }
    };

    private boolean checkDigit(String str){
        for(int i = 0; i < str.length(); i++){
            if ((str.charAt(i) > '9') || (str.charAt(i) < '0')) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    /*========================================================================================*/


    class AsyncT extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                lost_time=theyear + "-" ;
                if (themonth < 9) {
                    lost_time += "0" + (themonth + 1) + "-";
                } else {
                    lost_time += (themonth + 1) + "-";
                }
                if (theday < 10) {
                    lost_time += "0" + theday + "T";
                } else {
                    lost_time += theday + "T";
                }
                if (thehour < 10) {
                    lost_time += "0" + thehour + ":";
                } else {
                    lost_time += thehour + ":";
                }
                if (themin < 10) {
                    lost_time += "0" + themin + ":";
                } else {
                    lost_time += themin + ":";
                }
                lost_time += "00Z";
                jsonObject.put("item_name", item_name);
                jsonObject.put("lost_time", lost_time);
                jsonObject.put("lost_location", lost_location);
                jsonObject.put("lost_desc", lost_desc);
                jsonObject.put("username", user);
                jsonObject.put("image", encodedImage);

                if(!(positionSLost.equals(""))){
                    jsonObject.put("lost_category", positionSLost);
                }

//                System.out.println(jsonObject.toString());
                String rep = post("http://colab-sbx-pvt-10.oit.duke.edu:8000/app/lost", jsonObject.toString());
//                System.out.println(rep);
                if(rep.equals("{\"status\": \"success\"}")) {
                    lflag = 1;
                }else {
                    lflag = 0;
                }

            } catch (MalformedURLException e) {
//                System.out.println("error1");
                e.printStackTrace();
            } catch (IOException e) {
//                System.out.println("error2");
                e.printStackTrace();
            } catch (JSONException e) {
//                System.out.println("error3");
                e.printStackTrace();
            }
            catch (InterruptedException e) {
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
    void checkBack() throws InterruptedException {
        sleep(1500);
        if(lflag == 1) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(LostReport.this);
            builder2.setMessage("Item has been reported");
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
            AlertDialog.Builder builder2 = new AlertDialog.Builder(LostReport.this);
            builder2.setMessage("Failed to report the item");
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