package com.zethratech.servermanager;

/**
 * Created by Zethra on 4/23/2015.
 */
public class CommandWrapper {
    private int commandNumber = 0;
    private String command = null;
    private String commandOutput = null;

    public CommandWrapper(int commandNumber, String command) {
        this.commandNumber = commandNumber;
        this.command = command;
    }

    public boolean isCommand(int commandNumber) {
        if(this.commandNumber == commandNumber)
            return true;
        return false;
    }

    public int getCommandNumber() {
        return commandNumber;
    }

    public String getCommand() {
        return command;
    }

    public void setCommandOutput(String commandOutput) {
        this.commandOutput = commandOutput;
    }

    public String getCommandOutput() {
        return commandOutput;
    }
}
