package com.example.zhangsi929.f4_ui_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.URLEncoder;

public class QueryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    //this class collect users query input and send http request to backend

    public EditText nameInput;
    public EditText descInput;
    public EditText locationInput;
    public String item_name = "";
    public String desc = "";
    public String location = "";
    public Button submit;
    public String foundBoolean = "false";
    public String lostBoolean = "false";
    public Integer positionString;
    public String positionS = "0";
    public int qFlag = 0;

    //check if the user want to query lost or found items
    public void selectLost(View view){
        boolean checked = ((CheckBox) view).isChecked();
        if(checked){
            lostBoolean = "true";
        }
        else{
            lostBoolean = "false";
        }
    }
    public void selectFound(View view){
        boolean checked = ((CheckBox) view).isChecked();
        if(checked){
            foundBoolean = "true";
        }
        else{
            foundBoolean = "false";
        }
    }

    //convert the spinner selection
    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        String sSelected = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, sSelected, Toast.LENGTH_SHORT).show();
        positionString = position - 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        nameInput = (EditText) findViewById(R.id.name);
        descInput = (EditText) findViewById(R.id.descreption);
        locationInput = (EditText) findViewById(R.id.location);
        Spinner category = (Spinner) findViewById(R.id.category);
        ArrayAdapter<String> categoryArray = new ArrayAdapter<String>(QueryActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        categoryArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryArray);
        category.setOnItemSelectedListener(this);

        submit = (Button) findViewById(R.id.queryButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_name = nameInput.getText().toString();
                desc = descInput.getText().toString();
                location = locationInput.getText().toString();

                if (lostBoolean.equals("false") && foundBoolean.equals("false")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(QueryActivity.this);
                    builder1.setMessage("You have to check \"found\" or \"lost\"");
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
                    qFlag = 1;
                }
                else{
                    qFlag = 0;
                }

                if (positionString == 6 || positionString == -1) {
                    positionS = "";
                } else {
                    positionS = positionString.toString();
                }
                try {
                    User.url = "http://colab-sbx-pvt-10.oit.duke.edu:8000/app/query/?name=" + URLEncoder.encode(item_name, "UTF-8") + "&category=" + URLEncoder.encode(positionS, "UTF-8") + "&found=" + foundBoolean + "&lost=" + lostBoolean + "&description=" + URLEncoder.encode(desc, "UTF-8") + "&location=" + URLEncoder.encode(location, "UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    System.out.println("unsupported encoding exception");
                }
                if (qFlag == 0) {
                    Intent goToLostView = new Intent(getApplicationContext(), InfoView.class);
                    startActivity(goToLostView);
                }
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}