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
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SSH {
    private static final String TAG = SSH.class.getSimpleName();

    private List<String> refreshCommands = new ArrayList<>(Arrays.asList("pidof apache2", "ps -u tomcat7 | grep java | awk ' { print $1 } '", "pidof vsftpd", "pgrep openvpn"));
    //private String refreshCommand = "pidof apache2 && echo : && pgrep -f tomcat7";
    private String getUpdatesCommand = "/usr/lib/update-notifier/update-motd-updates-available && /usr/lib/update-notifier/update-motd-reboot-required";
    private String updateCommand = "apt-get update && apt-get upgrade -y";

    Map<String, Integer> commands;

    public String username = "root";
    public String password = "Ra1nbowCake!";
    public String hostname = "162.243.4.46";
    public int port = 22;

    private Resources resources;

    public String[] statuses = null;

    List<TextView> stats = new ArrayList<>();
    List<Switch> switches = new ArrayList<>();
    TextView updateOutput;

    public SSH(List<TextView> _stats, List<Switch> _switches, Resources _resources) {
        stats = _stats;
        switches = _switches;
        resources = _resources;
        commands = new HashMap<>();
        commands.put("execute", 1);
        commands.put("refresh", 2);
        commands.put("getUpdates", 3);
        commands.put("update", 4);
    }

    public void execute(Context context, String command) {
        CommandWrapper commandWrapper = new CommandWrapper(commands.get("execute"), command);
        new ExecuteTask(context).execute(commandWrapper);
    }

    public void refresh(Context context) {
        String refreshCommand = "";
        for(int i = 0; i < refreshCommands.size(); i++) {
            refreshCommand += refreshCommands.get(i);
            if(i != refreshCommands.size() - 1) {
                refreshCommand += " ; echo : ; ";
            }
        }
        refreshCommand = refreshCommand + "; (" + refreshCommand + ") > out ; ps aux > pro";
        CommandWrapper commandWrapper = new CommandWrapper(commands.get("refresh"), refreshCommand);
        new ExecuteTask(context).execute(commandWrapper);
    }

    public void getUpdates(Context context, TextView updateOutput) {
        CommandWrapper commandWrapper = new CommandWrapper(commands.get("getUpdates"), getUpdatesCommand);
        this.updateOutput = updateOutput;
        new ExecuteTask(context).execute(commandWrapper);
    }

    public void update(Context context) {
        CommandWrapper commandWrapper = new CommandWrapper(commands.get("update"), updateCommand);
        new ExecuteTask(context).execute(commandWrapper);
    }

    private class ExecuteTask extends AsyncTask<CommandWrapper, String, CommandWrapper> {

        private Context context;

        public ExecuteTask(Context context) {
            this.context = context;
        }

        @Override
        protected CommandWrapper doInBackground(CommandWrapper... params) {
            try {
                params[0].setCommandOutput(executeRemoteCommand(username, password, hostname, port, params[0].getCommand()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(CommandWrapper result) {
            if(result.getCommandNumber() == commands.get("execute")) {
                showToast(context, result.getCommandOutput());
                refresh(context);
            } else if(result.getCommandNumber() == commands.get("refresh")) {
                if(result != null && result.getCommandOutput() != null) {
                    statuses = result.getCommandOutput().split(":");
                    if (statuses.length == stats.size() && statuses.length == switches.size()) {
                        for (int i = 0; i < statuses.length; i++) {
                            if (statuses[i].replaceAll("\\s+","").matches(".*\\d+.*")) {
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
                Log.i(TAG, "Refreshed");
            } else if(result.getCommandNumber() == commands.get("getUpdates")) {
                showToast(context, "Checked for Updates");
                if(updateOutput != null) {
                    if(result.getCommandOutput().equals("\n")) {
                        updateOutput.setText("No Updates Available");
                    } else {
                        updateOutput.setText(result.getCommandOutput());
                    }
                }
                else
                    showToast(context, "ERROR: Could not show update output");
            } else if(result.getCommandNumber() == commands.get("update")) {
                showToast(context, "Installed Updates");
                Log.i(TAG, result.getCommandOutput());
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
