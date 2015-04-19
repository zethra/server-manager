package com.zethratech.servermanager;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SSH {
    private static final String TAG = SSH.class.getSimpleName();

    private String refreshCommand = "service apache2 status;service tomcat7 status";

    public String username = "root";
    public String password = "Ra1nbowCake!";
    public String hostname = "162.243.4.46";
    public int port = 22;

    private Resources resources;

    public String[] statuses = null;

    List<TextView> stats = new ArrayList<>();
    List<Switch> switches = new ArrayList<>();

    public SSH(List<TextView> _stats, List<Switch> _switches, Resources _resources) {
        stats = _stats;
        switches = _switches;
        resources = _resources;
    }

    public boolean refreshIsFinished() {
        if (statuses != null)
            return true;
        else
            return false;
    }

    public void execute(Context context, String command) {
        new ExecuteTask(context).execute(command);
    }

    private class ExecuteTask extends AsyncTask<String, String, String> {

        private Context context;

        public ExecuteTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String output = null;
            try {
                output = executeRemoteCommand(username, password, hostname, port, params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            showToast(context, result);
            refresh(context);
            super.onPostExecute(result);
        }

    }

    public void refresh(Context context) {
        new RefreshTask(context).execute(refreshCommand);
    }

    private class RefreshTask extends AsyncTask<String, String, String> {

        private Context context;

        public RefreshTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            statuses = null;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String output = null;
            try {
                output = executeRemoteCommand(username, password, hostname, port, params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                statuses = result.split("\n");
                if (statuses.length == stats.size() && statuses.length == switches.size()) {
                    for (int i = 0; i < statuses.length; i++) {
                        if (!statuses[i].contains("not")) {
                            stats.get(i).setText("Running");
                            stats.get(i).setTextColor(resources.getColor(R.color.running));
                            switches.get(i).setChecked(true);
                        } else {
                            stats.get(i).setText("Not Running");
                            stats.get(i).setTextColor(resources.getColor(R.color.notRunning));
                            switches.get(i).setChecked(false);
                        }
                    }
                } else {
                    showToast(context, "Refresh Failed!");
                }
            } else {
                showToast(context, "Refresh Failed!");
            }
            super.onPostExecute(result);
        }
    }

    public static String executeRemoteCommand(String username, String password, String hostname, int port, String command) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
        session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        channelssh.setCommand(command);
        channelssh.connect();
        while (!channelssh.isClosed()){}
        channelssh.disconnect();

        return baos.toString();
    }

    private void showToast(final Context context, final String message) {
        new Handler(context.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
