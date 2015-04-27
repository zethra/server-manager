package com.zethratech.servermanager;

/**
 * Created by BGOLDBERG on 4/27/2015.
 */
public class DatabaseWrapper {
    public String DATABASE_NAME;
    public int DATABASE_VERSION;
    public String DATABASE_CREATE;
    public CreateFromModel fromModel;

    public DatabaseWrapper(String DATABASE_NAME, int DATABASE_VERSION, Class model) {
        this.DATABASE_NAME = DATABASE_NAME;
        this.DATABASE_VERSION = DATABASE_VERSION;

    }
}
