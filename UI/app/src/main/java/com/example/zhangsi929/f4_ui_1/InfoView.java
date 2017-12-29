package com.example.zhangsi929.f4_ui_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static java.lang.Thread.sleep;


//import org.joda.time.DateTime;
public class InfoView extends AppCompatActivity implements AdapterView.OnItemClickListener{
    //this class shows all of the return items from backend

    ArrayList<FoundItem> foundItemList = new ArrayList<FoundItem>();
    ArrayList<LostItem> lostItemList = new ArrayList<LostItem>();
    ArrayList<String> infoDisplayName;
    ArrayList<String> infoDisplayLocation;
    ArrayList<String> infoDisplayTime;
    ArrayList<String> infoDisplayImage;
    //foundMap and lostMap are used to store the found/lost items retrieved from backend
    HashMap<Integer, FoundItem> foundMap= new HashMap<Integer, FoundItem>();
    HashMap<Integer, LostItem> lostMap= new HashMap<Integer, LostItem>();
    private static String url = User.url;
    private String TAG = InfoView.class.getSimpleName();
    private ProgressDialog pDialog;
    ListView infoListView;
    final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSZ";

    public static Calendar getCalendarFromISO(String datestring) {
        //System.out.println("calendar start");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
        SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601DATEFORMAT, Locale.getDefault());
        try {
            Date date = dateformat.parse(datestring);
            date.setHours(date.getHours()-1);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println("calendar end");
        return calendar;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_view);
        infoListView = (ListView) findViewById(R.id.InfoListView);
        //infoDisplayName will show the name of the found/lost items
        infoDisplayName = new ArrayList<>();
        infoDisplayLocation = new ArrayList<>();
        infoDisplayTime = new ArrayList<>();
        infoDisplayImage = new ArrayList<>();
        new GetContacts().execute();
        infoListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //System.out.println("this is position" + position);
        //System.out.println(infoDisplay.get(position));
        Intent moveToDetailIntent = new Intent (getBaseContext(), DetailView.class);
        if(foundMap.containsKey(position)) {
            moveToDetailIntent.putExtra("name", foundMap.get(position).getItemName() + " (Found)");
            moveToDetailIntent.putExtra("found_location", foundMap.get(position).getFoundLocation());
            moveToDetailIntent.putExtra("found_time", foundMap.get(position).getFoundTime().toString());
            moveToDetailIntent.putExtra("reporter_email", foundMap.get(position).getReportEmail());
            moveToDetailIntent.putExtra("posted_time", foundMap.get(position).getPostedTime().toString());
            moveToDetailIntent.putExtra("reporter_phone", String.valueOf(foundMap.get(position).getReportPhone()));
            //System.out.println(foundMap.get(position).getPostedTime().toString());
            moveToDetailIntent.putExtra("found_desc", foundMap.get(position).getFoundDescription());
            //System.out.println(foundMap.get(position).getFoundDescription());
            moveToDetailIntent.putExtra("reporter_name", foundMap.get(position).getReporterName());
            moveToDetailIntent.putExtra("image", foundMap.get(position).getEncodedImage());
            moveToDetailIntent.putExtra("category", foundMap.get(position).getCategory());
            //System.out.println(66666666);
            //System.out.println(foundMap.get(position).getCategory());
        } else if(lostMap.containsKey(position)){
            System.out.println("xxx33333");
            moveToDetailIntent.putExtra("name", lostMap.get(position).getItemName() + " (Lost)");
            moveToDetailIntent.putExtra("found_location", lostMap.get(position).getLostLocation());
            moveToDetailIntent.putExtra("found_time", lostMap.get(position).getLostTime().toString());
            moveToDetailIntent.putExtra("reporter_email", lostMap.get(position).getReportEmail());
            moveToDetailIntent.putExtra("posted_time", lostMap.get(position).getPostedTime().toString());
            moveToDetailIntent.putExtra("found_desc", lostMap.get(position).getLostDescription());
            moveToDetailIntent.putExtra("reporter_phone", (String.valueOf(lostMap.get(position).getReportPhone())));
            moveToDetailIntent.putExtra("reporter_name", lostMap.get(position).getReporterName());
            moveToDetailIntent.putExtra("image", lostMap.get(position).getEncodedImage());
            moveToDetailIntent.putExtra("category", lostMap.get(position).getCategory());
            //System.out.println("zzzz33333");
        }
        startActivity(moveToDetailIntent);
    }

    // private class GetContacts send http url to retrieve data from backend
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(InfoView.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(User.url);
            try{
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStr);

            //check the response from backend, if it is not null,
            //put the response to the found/lost map
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray foundArray = jsonObj.getJSONArray("found_items");

                    JSONArray lostArray = jsonObj.getJSONArray("lost_items");
                    foundMap.clear();
                    lostMap.clear();
                    int lines = 0;
                    // looping through All Contacts
                    for (int i = 0; i < foundArray.length(); i++) {
                        //System.out.println("this is i found" + i);
                        JSONObject c = foundArray.getJSONObject(i);
                        FoundItem found = new FoundItem();
                        found.setFoundDescription(c.getString("found_desc"));
                        found.setFoundLocation(c.getString("found_location"));
                        found.setFoundTime(getCalendarFromISO(c.getString("found_time")).getTime());
                        found.setReportEmail(c.getString("reporter_email"));
                        found.setItemName(c.getString("name"));
                        found.setPostedTime(getCalendarFromISO(c.getString("posted_time")).getTime());
                        found.setReporterName(c.getString("reporter_name"));
                        found.setReportPhone(c.getLong("reporter_phone"));
                        found.setEncodedImage(c.getString("image"));
                        found.setCategory(c.getInt("found_category"));
                        foundItemList.add(found);
                        infoDisplayName.add(found.getItemName() + " (Found)");
                        infoDisplayLocation.add(found.getFoundLocation());
                        infoDisplayTime.add(found.getFoundTime().toString());
                        infoDisplayImage.add(found.getEncodedImage());
                        foundMap.put(lines, found);
                        lines++;
 //                       System.out.println("this is line found" + lines);
                    }
                    for (int i = 0; i < lostArray.length(); i++) {
 //                       System.out.println("this is i lost" + i);
                        JSONObject c = lostArray.getJSONObject(i);
                        LostItem lost = new LostItem();
                        lost.setLostDescription(c.getString("lost_desc"));
                        lost.setLostLocation(c.getString("lost_location"));
                        lost.setLostTime(getCalendarFromISO(c.getString("lost_time")).getTime());
                        lost.setReportEmail(c.getString("reporter_email"));
                        lost.setItemName(c.getString("name"));
                        lost.setPostedTime(getCalendarFromISO(c.getString("posted_time")).getTime());
                        lost.setReportPhone(c.getLong("reporter_phone"));
                        lost.setReporterName(c.getString("reporter_name"));
                        lost.setEncodedImage(c.getString("image"));
                        lost.setCategory(c.getInt("lost_category"));

                        lostItemList.add(lost);
                        infoDisplayName.add(lost.getItemName() + " (Lost)");
                        infoDisplayLocation.add(lost.getLostLocation());
                        infoDisplayTime.add(lost.getLostTime().toString());
                        infoDisplayImage.add(lost.getEncodedImage());
                        lostMap.put(lines, lost);
                        lines++;
 //                       System.out.println("this is line lost" + lines);
                    }
//                    System.out.println("this is final line" + lines);
//                    System.out.println("foundmap" + foundMap);
//                    System.out.println("lostdmap" + lostMap);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                // if the json response is null
                // or if the response does not match the keys, print error message
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            CustomAdapter customAdapter = new CustomAdapter();
            infoListView.setAdapter(customAdapter);
        }
        // set up a custom list view
        class CustomAdapter extends BaseAdapter{
            @Override
            public int getCount(){
                return infoDisplayName.size();
            }
            @Override
            public Objects getItem(int i){
                return null;
            }
            @Override
            public long getItemId(int i){
                return 0;
            }
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                view = getLayoutInflater().inflate(R.layout.row, null);
                TextView textView_name = (TextView)view.findViewById(R.id.name);
                TextView textView_location = (TextView)view.findViewById(R.id.location);
                TextView textView_time = (TextView)view.findViewById(R.id.time);
                ImageView imageView_preview = (ImageView)view.findViewById((R.id.preview));
                textView_name.setText(infoDisplayName.get(i));
                textView_location.setText(infoDisplayLocation.get(i));
                textView_time.setText(infoDisplayTime.get(i));
                //set up image in list view
                byte[] decodedString = Base64.decode(infoDisplayImage.get(i), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView_preview.setImageBitmap(decodedByte);
                return view;
            }

        }
    }
}
