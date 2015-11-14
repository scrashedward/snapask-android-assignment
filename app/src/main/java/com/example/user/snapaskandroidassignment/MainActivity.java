package com.example.user.snapaskandroidassignment;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class MainActivity extends FragmentActivity {
    public Vector<Integer> icon = new Vector<>();
    public Vector<String> data = new Vector<>();
    public Vector<String> des = new Vector<>();
    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Bitmap profilePic = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
        Drawable roundPic = new RoundImage(profilePic);
        ImageView profilePicView = (ImageView) findViewById(R.id.profilePic);
        profilePicView.setImageDrawable(roundPic);
        //profilePicView.getLayoutParams().height = 150;
        //profilePicView.getLayoutParams().width = 150;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        /* Background Image part*/

        ImageView backgroundPicView = (ImageView) findViewById(R.id.background);
        backgroundPicView.setBackgroundResource(R.drawable.background);
        backgroundPicView.getLayoutParams().height = (int)(size.y/2.5);
        backgroundPicView.getLayoutParams().width = size.x;



        /* Tab part */

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();


        /* Tab one:*/

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
        data.add("brain@appedu.co");
        data.add("6900 6900");
        data.add("HKU");
        data.add("Math,Phy,Chem");

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


        /* Tab two: */

        tabSpec = tabHost.newTabSpec("Payment");
        tabSpec.setContent(R.id.paymentTab);
        tabSpec.setIndicator("Payment");
        tabHost.addTab(tabSpec);


        /* Tab three: */
        tabSpec = tabHost.newTabSpec("Setting");
        tabSpec.setContent(R.id.settingTab);
        tabSpec.setIndicator("Setting");
        tabHost.addTab(tabSpec);





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
