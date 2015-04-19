package com.zethratech.servermanager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

    SSH ssh = null;
    Timer timer;

    TextView apacheStatus;
    TextView tomcatStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apacheStatus = (TextView) findViewById(R.id.apacheStatus);
        tomcatStatus = (TextView) findViewById(R.id.tomcatStatus);

        ssh = new SSH(new ArrayList<TextView>(Arrays.asList(apacheStatus, tomcatStatus)), getResources());
        ssh.refresh(getApplicationContext());

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ssh.refresh(getApplicationContext());
            }
        }, 0, 20000);
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                ssh.refresh(getApplicationContext());
                break;
            case R.id.apacheStart:
                ssh.execute(getApplicationContext(), "service apache2 start");
                break;
            case R.id.apacheStop:
                ssh.execute(getApplicationContext(), "service apache2 stop");
                break;
            case R.id.tomcatStart:
                ssh.execute(getApplicationContext(), "service tomcat7 start");
                break;
            case R.id.tomcatStop:
                ssh.execute(getApplicationContext(), "service tomcat7 stop");
                break;
        }
    }

    public void onClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                ssh.refresh(getApplicationContext());
                break;
        }
    }


}
