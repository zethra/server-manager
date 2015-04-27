package com.zethratech.servermanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bgoldberg on 4/27/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "server-manager.db";
    private static final int DATABASE_VERSION = 1;

    private static String DATABASE_CREATE = "";
    public CreateFromModel settingModel = new CreateFromModel();

    public DatabaseHelper(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.DATABASE_NAME = DATABASE_NAME;
        DATABASE_CREATE = settingModel.create("settingsTable", Settings.class, true);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + settingModel.tableName);
        onCreate(db);
    }
}
