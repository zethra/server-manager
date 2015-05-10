package com.zethratech.servermanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
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
    private static final String TAG = MainActivity.class.getSimpleName();
    int refreshTime = 5000;

    boolean firstRun = true;
    boolean refresh = true;
    boolean autoRefresh = false;

    SSH ssh = null;
    Timer timer;

    ViewPager viewPager;

    TextView apacheStatus;
    TextView tomcatStatus;
    TextView vsftpdStatus;
    TextView openvpnStatus;
    TextView mysqlStatus;

    Switch apacheSwitch;
    Switch tomcatSwitch;
    Switch vsftpdSwitch;
    Switch openvpnSwitch;
    Switch mysqlSwitch;

    TextView updateOutput;

    SharedPreferences settings;
    SharedPreferences.OnSharedPreferenceChangeListener settingChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter());
        timer = new Timer();
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        settingChange = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                autoRefresh = settings.getBoolean("autoRefresh", true);
            }
        };
    }

    @Override
    public void onPause() {
        refresh = false;
        Log.i(MainActivity.class.getSimpleName() ,"Pause");
        super.onPause();
    }

    @Override
    public void onResume() {
        refresh = true;
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
            case R.id.mysqlSwitch:
                if(mysqlSwitch.isChecked())
                    ssh.execute(getApplicationContext(), "service mysql start");
                else
                    ssh.execute(getApplicationContext(), "service mysql stop");
                break;
            case R.id.vsftpdSwitch:
                if(vsftpdSwitch.isChecked())
                    ssh.execute(getApplicationContext(), "service vsftpd start");
                else
                    ssh.execute(getApplicationContext(), "service vsftpd stop");
                break;
            case R.id.openvpnSwitch:
                if(openvpnSwitch.isChecked())
                    ssh.execute(getApplicationContext(), "service openvpnas start");
                else
                    ssh.execute(getApplicationContext(), "service openvpnas stop");
                break;
            case R.id.getUpdates:
                ssh.getUpdates(getApplicationContext(), updateOutput);
                break;
            case R.id.update:
                ssh.update(getApplicationContext());
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
                return "Updates";
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position == 0) {
                View servers = getLayoutInflater().inflate(R.layout.fragment_servers, container, false);
                container.addView(servers);

                apacheStatus = (TextView) findViewById(R.id.apacheStatus);
                tomcatStatus = (TextView) findViewById(R.id.tomcatStatus);
                vsftpdStatus = (TextView) findViewById(R.id.vsftpdStatus);
                openvpnStatus = (TextView) findViewById(R.id.openvpnStatus);
                mysqlStatus = (TextView) findViewById(R.id.mysqlStatus);

                apacheSwitch = (Switch) findViewById(R.id.apacheSwitch);
                tomcatSwitch = (Switch) findViewById(R.id.tomcatSwitch);
                vsftpdSwitch = (Switch) findViewById(R.id.vsftpdSwitch);
                openvpnSwitch = (Switch) findViewById(R.id.openvpnSwitch);
                mysqlSwitch = (Switch) findViewById(R.id.mysqlSwitch);

                ssh = new SSH(new ArrayList<TextView>(Arrays.asList(apacheStatus, tomcatStatus,mysqlStatus, vsftpdStatus, openvpnStatus)),
                        new ArrayList<Switch>(Arrays.asList(apacheSwitch, tomcatSwitch, mysqlSwitch, vsftpdSwitch, openvpnSwitch)) , getResources());

                autoRefresh = settings.getBoolean("autoRefresh", false);
                if(!autoRefresh) {
                    ssh.refresh(getApplicationContext());
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (refresh && ssh != null && autoRefresh) {
                            ssh.refresh(getApplicationContext());
                        }
                    }
                }, 0, refreshTime);

                //CreateFromModel createFromModel = new CreateFromModel(Settings.class);
                return servers;
            } else {
                View settings = getLayoutInflater().inflate(R.layout.fragment_updates, container, false);
                container.addView(settings);
                updateOutput = (TextView) findViewById(R.id.updateOutput);
                return settings;
            }
        }
    }

}
