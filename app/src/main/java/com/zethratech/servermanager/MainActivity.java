package com.zethratech.servermanager;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

    SSH ssh = null;
    Timer timer;

    ViewPager viewPager;

    TextView apacheStatus;
    TextView tomcatStatus;
    Switch apacheSwitch;
    Switch tomcatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter());
    }

    @Override
    public void onPause() {
        timer.cancel();
        timer = null;
        Log.i(MainActivity.class.getSimpleName() ,"Pause");
        super.onPause();
    }

    @Override
    public void onResume() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //ssh.refresh(getApplicationContext());
            }
        }, 0, 20000);
        Log.i(MainActivity.class.getSimpleName() ,"Resume");
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                ssh.refresh(getApplicationContext());
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                ssh.refresh(getApplicationContext());
                break;
            case R.id.apacheSwitch:
                if(apacheSwitch.isChecked())
                    ssh.execute(getApplicationContext(), "service apache2 start");
                else
                    ssh.execute(getApplicationContext(), "service apache2 stop");
                break;
            case R.id.tomcatSwitch:
                if(tomcatSwitch.isChecked())
                    ssh.execute(getApplicationContext(), "service tomcat7 start");
                else
                    ssh.execute(getApplicationContext(), "service tomcat7 stop");
                break;
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Servers";
            } else {
                return "Settings";
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position == 0) {
                View servers = getLayoutInflater().inflate(R.layout.fragment_servers, container, false);
                container.addView(servers);
                apacheStatus = (TextView) findViewById(R.id.apacheStatus);
                tomcatStatus = (TextView) findViewById(R.id.tomcatStatus);
                apacheSwitch = (Switch) findViewById(R.id.apacheSwitch);
                tomcatSwitch = (Switch) findViewById(R.id.tomcatSwitch);

                ssh = new SSH(new ArrayList<TextView>(Arrays.asList(apacheStatus, tomcatStatus)),new ArrayList<Switch>(Arrays.asList(apacheSwitch, tomcatSwitch)) , getResources());
                ssh.refresh(getApplicationContext());

                /*timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ssh.refresh(getApplicationContext());
                    }
                }, 0, 20000);*/
                return servers;
            } else {
                View settings = getLayoutInflater().inflate(R.layout.activity_settings, container, false);
                container.addView(settings);
                return settings;
            }
        }
    }

}
