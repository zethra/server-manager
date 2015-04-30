package com.zethratech.servermanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        if(dbHelper.names.size() == fields.length) {
            for(int i = 0; i < fields.length; i++) {
                if(fields[i].getClass() == Integer.TYPE)
                    values.put(dbHelper.names.get(i),(Integer)fields[i]);
                else if(fields[i].getClass() == Float.TYPE)
                    values.put(dbHelper.names.get(i),(Float)fields[i]);
                else if(fields[i].getClass() == Boolean.TYPE)
                    values.put(dbHelper.names.get(i),(Boolean)fields[i]);
                else if(fields[i].getClass() == String.class)
                    values.put(dbHelper.names.get(i),(String)fields[i]);
            }
        } else
            Log.e(SettingsDataSource.class.getSimpleName(), "Create augments did not matches settings class");
        long insertId = database.insert(dbHelper.TABLE_NAME, null, values);
        Cursor cursor =  database.query(dbHelper.TABLE_NAME, allColumns, dbHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Settings settings = cursorToSettings(cursor);
        cursor.close();
        return settings;
    }

    public Settings editSettings(long id, Object[] fields) {
        ContentValues values = new ContentValues();
        if(dbHelper.names.size() == fields.length) {
            for(int i = 0; i < fields.length; i++) {
                if(fields[i].getClass() == Integer.TYPE)
                    values.put(dbHelper.names.get(i),(Integer)fields[i]);
                else if(fields[i].getClass() == Float.TYPE)
                    values.put(dbHelper.names.get(i),(Float)fields[i]);
                else if(fields[i].getClass() == Boolean.TYPE)
                    values.put(dbHelper.names.get(i),(Boolean)fields[i]);
                else if(fields[i].getClass() == String.class)
                    values.put(dbHelper.names.get(i),(String)fields[i]);
            }
        } else
            Log.e(SettingsDataSource.class.getSimpleName(), "Create augments did not matches settings class");
        long insertId = database.update(dbHelper.TABLE_NAME, values, dbHelper.COLUMN_ID + " = " + id, null);
        Cursor cursor =  database.query(dbHelper.TABLE_NAME, allColumns, dbHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Settings settings = cursorToSettings(cursor);
        cursor.close();
        return settings;
    }

    public void deleteSettings(Settings settings) {
        long id = settings.id;
        database.delete(dbHelper.TABLE_NAME, dbHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Settings> getAllComments() {
        List<Settings> settingsList = new ArrayList<>();
        Cursor cursor = database.query(dbHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Settings settings = cursorToSettings(cursor);
            settingsList.add(settings);
            cursor.moveToNext();
        }
        cursor.close();
        return settingsList;
    }

    private Settings cursorToSettings(Cursor cursor) {
        Settings settings = new Settings();
        if(cursor.getCount() > 0) {
            for (int i = 0; i < dbHelper.names.size(); i++) {
                try {
                    Field field = settings.getClass().getDeclaredFields()[i];
                    field.setAccessible(true);
                    Object value = field.get(settings);
                    if (field.getType() == Boolean.TYPE)
                        value = (cursor.getInt(i) == 0);
                    else if (field.getType() == Integer.TYPE)
                        value = cursor.getInt(i);
                    else if (field.getType() == Float.TYPE)
                        value = cursor.getFloat(i);
                    else if (field.getType() == String.class)
                        value = cursor.getString(i);
                    field.set(settings, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return settings;
    }
}
