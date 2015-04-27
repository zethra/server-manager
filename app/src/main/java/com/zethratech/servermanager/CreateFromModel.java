package com.zethratech.servermanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bgoldberg on 4/27/2015.
 */
public class CreateFromModel {
    String tableName;
    String createString;
    List<String> names = new ArrayList<>();
    public String create(String tableName, Class toConvert, boolean notNull) {
        this.tableName = tableName;
        createString = "create table " + tableName + "(";
        int i = 1;
        for(Field field : toConvert.getDeclaredFields()) {
            createString += field.getName();
            if(field.getType() == Integer.TYPE || field.getType() == Long.TYPE || field.getType() == Boolean.TYPE) {
                createString += " integer";
            } else if(field.getType() == String.class) {
                createString += " text";
            }

            if(field.getName().equals("id")) {
                createString += " primary key autoincrement";
            } else if(notNull) {
                createString += " not null";
            }

            if(i < toConvert.getDeclaredFields().length) {
                createString += ", ";
            } else {
                createString += ");";
            }
            names.add(field.getName());
            i++;
        }

        return createString;
    }
}
