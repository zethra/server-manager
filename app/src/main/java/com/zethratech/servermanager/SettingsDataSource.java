package com.zethratech.servermanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SettingsDataSource {
    private Context context;

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns;

    public SettingsDataSource(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context, "settingTable", Settings.class);
        allColumns = dbHelper.fromModel.names.toArray(new String[dbHelper.fromModel.names.size()]);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Settings createSettings(Object[] fields) {
        ContentValues values = new ContentValues();
        if(dbHelper.fromModel.names.size() == fields.length) {
            for(int i = 0; i < fields.length; i++) {
                if(fields[i].getClass() == Integer.TYPE)
                    values.put(dbHelper.fromModel.names.get(i),(Integer)fields[i]);
                else if(fields[i].getClass() == Float.TYPE)
                    values.put(dbHelper.fromModel.names.get(i),(Float)fields[i]);
                else if(fields[i].getClass() == Boolean.TYPE)
                    values.put(dbHelper.fromModel.names.get(i),(Boolean)fields[i]);
                else if(fields[i].getClass() == String.class)
                    values.put(dbHelper.fromModel.names.get(i),(String)fields[i]);
            }
        } else
            Log.e(SettingsDataSource.class.getSimpleName(), "Create augments did not matches settings class");
        long insertId = database.insert(dbHelper.TABLE_NAME, null, values);
        Cursor cursor =  database.query(dbHelper.TABLE_NAME, allColumns, dbHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        return new Settings();
    }
}
