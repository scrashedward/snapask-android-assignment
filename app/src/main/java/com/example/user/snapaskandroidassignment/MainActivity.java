package com.example.user.snapaskandroidassignment;
import android.app.Activity;
import android.graphics.ColorFilter;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class MainActivity extends FragmentActivity {
    public Vector<Integer> icon = new Vector<>();
    public Vector<String> data = new Vector<>();
    public Vector<String> des = new Vector<>();
    String email = new String();
    String phone = new String();
    String username = new String();
    String picUrl = new String();
    String school = new String();
    String subject = new String();
    Drawable roundPic;
    String newEmail = null;
    String oldEmail = null;
    String oldPhone = null;
    String newPhone = null;
    View v;


    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Hide the actionbar **/

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        /** Get Json Object and parse it in a Thread since http request could not be called in main thread **/

        if(!isOnline())Log.d("scrash","not online");

        Thread jsonInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonString = getJSON("https://api.myjson.com/bins/4zujh");
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    email = dataObject.getString("email");
                    username = dataObject.getString("username");
                    picUrl = dataObject.getString("profile_pic_file_name");
                    phone = dataObject.getString("phone");
                    school = dataObject.getString("school_name");
                    JSONArray subjectArray = dataObject.getJSONArray("subjects");
                    subject = subjectArray.getJSONObject(0).getString("abbr");
                    URL url = new URL(picUrl);
                    Bitmap profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    roundPic = new RoundImage(profilePic);
                }
                catch(JSONException|IOException e){
                    Log.d("scrash", "error parsing Json Object");
                }
            }
        });

        /** Start the thread then wait till it terminated to continue Main thread **/

        jsonInfo.start();
        try {
            jsonInfo.join();
        }catch(InterruptedException e){
            Log.d("scrash","error joining jsonInfo");
        }
        /** Setting Profile Image **/

        ImageView profilePicView = (ImageView) findViewById(R.id.profilePic);
        profilePicView.setImageDrawable(roundPic);


        /** Setting Background Image **/

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ImageView backgroundPicView = (ImageView) findViewById(R.id.background);
        backgroundPicView.setBackgroundResource(R.drawable.background);
        backgroundPicView.getLayoutParams().height = (int)(size.y/2.3);
        backgroundPicView.getLayoutParams().width = size.x;



        /** Tab Setup **/

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.getTabWidget().setDividerDrawable(null);

        /** Tab one, loading list and make it editable through popup window **/

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("info");
        tabSpec.setContent(R.id.infoTab);
        tabSpec.setIndicator("Info");
        tabHost.addTab(tabSpec);

        des.add("email");
        des.add("phone");
        des.add("university");
        des.add("subjects");
        icon.add(R.drawable.mail);
        icon.add(R.drawable.phone);
        icon.add(R.drawable.university);
        icon.add(R.drawable.heart);
        data.add(email);
        data.add(phone);
        data.add(school);
        data.add(subject);

        for(int i = 0; i < des.size(); i++ ){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("icon", Integer.toString(icon.get(i)));
            hm.put("data", data.get(i));
            hm.put("des", des.get(i) );
            aList.add(hm);
        };

        String[] from = {"icon","data","des" };
        int[] to = {R.id.icon, R.id.data,R.id.des};
        SimpleAdapter adapter = new SimpleAdapter(this, aList, R.layout.listlayout, from, to);

        ListView listView = (ListView) findViewById(R.id.infoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                v = view;
                TextView tmpDes = (TextView) view.findViewById(R.id.des);
                TextView tmpData = (TextView) view.findViewById(R.id.data);
                if(tmpDes.getText().toString() == "email"){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Edit email");

                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newEmail = input.getText().toString();
                            TextView tmpData = (TextView) v.findViewById(R.id.data);
                            if(oldEmail == null )oldEmail = tmpData.getText().toString();
                            tmpData.setText(input.getText());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                else if(tmpDes.getText().toString() == "phone"){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Edit phone");

                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newPhone = input.getText().toString();
                            TextView tmpData = (TextView) v.findViewById(R.id.data);
                            if(oldPhone == null)oldPhone = tmpData.getText().toString();
                            tmpData.setText(input.getText());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            }
        });

        /** Tab two, set spinners **/

        tabSpec = tabHost.newTabSpec("Payment");
        tabSpec.setContent(R.id.paymentTab);
        tabSpec.setIndicator("Payment");
        tabHost.addTab(tabSpec);


        final Spinner cardTypeSpinner = (Spinner) findViewById(R.id.cardTypeSpinner);
        String[] cardType = {"MasterCard","VISA","JCB","American Express","Union Pay"};
        MySpinnerAdapter spinnerAdapter =  new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, cardType);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardTypeSpinner.setAdapter(spinnerAdapter);

        final Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        String[] month = {"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
        MySpinnerAdapter monthAdapter = new MySpinnerAdapter(this,android.R.layout.simple_spinner_item, month);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        final Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        String[] year = {"2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030","2031","2032"};
        MySpinnerAdapter yearAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, year);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        Button payButton = (Button) findViewById(R.id.checkButton);
        payButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] expire ={(String)monthSpinner.getSelectedItem(),(String)yearSpinner.getSelectedItem()};
                        EditText cardNumber = (EditText) findViewById(R.id.cardNumber);
                        EditText cvv = (EditText) findViewById(R.id.cvv);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Payment Check")
                                .setMessage("Card Number : "+ cardNumber.getText().toString()+"\n Do you really want to pay this?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this,"Paid successful",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
        );


        /** Tab three: **/


        tabSpec = tabHost.newTabSpec("Setting");
        tabSpec.setContent(R.id.settingTab);
        tabSpec.setIndicator("Setting");
        tabHost.addTab(tabSpec);

        /** Change Tab appearance and Tab text color **/

        for(int i=0 ; i< tabHost.getTabWidget().getTabCount();i++) {
            tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.apptheme_tab_indicator_holo);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            if(tv == null) Log.d("scrash","it's a null pointer");
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            tv.setTextSize(15);
            //tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.indicator);
        }





    }

    /** This function tests if user have access to the internet **/

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /** This function gets JSON object from certain URL **/

    public String getJSON(String address){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(),"Failedet JSON object");
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }


    /** Record if there's a change of the phone or email**/

    @Override
    protected void onPause(){
        super.onPause();
        if(oldPhone!=null){
            Log.d("datachanged","Phone has been changed from "+oldPhone+" to "+newPhone);
            oldPhone = null;
        }
        if(oldEmail!=null){
            Log.d("datachanged","Email has been changed from "+oldEmail+" to "+newEmail);
            oldEmail = null;
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
