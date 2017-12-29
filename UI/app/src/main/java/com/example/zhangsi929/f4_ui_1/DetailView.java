package com.example.zhangsi929.f4_ui_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {
    // this class show the detail of found/lost items to users
    TextView nameTextView;
    TextView found_locationTextView;
    TextView found_timeTextView;
    TextView reporter_emailTextView;
    TextView posted_timeTextView;
    TextView found_descTextView;
    TextView report_phoneTextView;
    TextView report_nameTextView;
    TextView catTextView;
    ImageView image;
    String defaultImage = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        nameTextView = (TextView) findViewById(R.id.nameId);
        found_locationTextView = (TextView) findViewById(R.id.found_location);
        found_timeTextView = (TextView) findViewById(R.id.found_time);
        reporter_emailTextView = (TextView) findViewById(R.id.reporter_email);
        posted_timeTextView = (TextView) findViewById(R.id.posted_time);
        found_descTextView = (TextView) findViewById(R.id.desc);
        report_phoneTextView = (TextView) findViewById(R.id.reporter_phone);
        report_nameTextView = (TextView) findViewById(R.id.reporter_name);
        catTextView = (TextView) findViewById(R.id.Cat);
        image = (ImageView) findViewById(R.id.DetailImage);
        //receive data from previous view
        String name = (String) getIntent().getExtras().get("name");
        String found_loc = (String) getIntent().getExtras().get("found_location");
        String found_tm = (String) getIntent().getExtras().get("found_time");
        String reporter_em = (String) getIntent().getExtras().get("reporter_email");
        String posted_tm = (String) getIntent().getExtras().get("posted_time");
        String found_des = (String) getIntent().getExtras().get("found_desc");
        String report_phone = (String) getIntent().getExtras().get("reporter_phone");
        String report_name = (String) getIntent().getExtras().get("reporter_name");
        String encodedImage = (String) getIntent().getExtras().get("image");
        int temp_category = (Integer) getIntent().getExtras().get("category");
        // decode image and set image in imageView
        try {
            byte[] decodedString = Base64.decode(encodedImage == "" ? defaultImage : encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        } catch (Exception e) {
            byte[] decodedString = Base64.decode(defaultImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        }
        //set TextView value
        nameTextView.setText(name);
        found_locationTextView.setText(found_loc);
        found_timeTextView.setText(found_tm);
        reporter_emailTextView.setText(reporter_em);
        posted_timeTextView.setText(posted_tm);
        found_descTextView.setText(found_des);
        report_phoneTextView.setText(report_phone);
        report_nameTextView.setText(report_name);
        //distinguish categories and output to textfield
        String category = "other";
        if (temp_category == 0) {
            category = "Pet";
        }
        if (temp_category == 1) {
            category = "Clothing";
        }
        if (temp_category == 2) {
            category = "Book";
        }
        if (temp_category == 3) {
            category = "Electronics";
        }
        if (temp_category == 4) {
            category = "Daily Necessities";
        }
        catTextView.setText(category);
    }
}
