package com.zethratech.servermanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * Created by bgoldberg on 4/27/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "server-manager.db";
    private static final int DATABASE_VERSION = 1;

    public String TABLE_NAME = "";
    private String DATABASE_CREATE;
    public String COLUMN_ID;
    public CreateFromModel fromModel;
    public List<String> names;

    public DatabaseHelper(Context context, String TABLE_NAME, Class model) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.TABLE_NAME = TABLE_NAME;
        fromModel = new CreateFromModel(TABLE_NAME, model, true);
        DATABASE_CREATE = fromModel.createString;
        COLUMN_ID = fromModel.COLUMN_ID;
        names = fromModel.names;
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
