package com.nishantdayal.todolistappdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NishantDayal on 29/06/17.
 */

public class ToDoOpenHelper extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "ToDoList";
    public final static String TODO_ID = "_id";
    public final static String TODO_TITLE = "Title";
    public final static String TODO_DATE = "Date";
    public final static String TODO_TIME = "Time";
    public final static String TODO_DESCRIPTION = "Description";
    public static ToDoOpenHelper toDoOpenHelper;

    public static ToDoOpenHelper getToDoOpenHelperInstance(Context context){
        if(toDoOpenHelper==null) {
            toDoOpenHelper = new ToDoOpenHelper(context);
        }

        return toDoOpenHelper;
    }

    public ToDoOpenHelper(Context context) {
        super(context, "ToDoList.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table " + TABLE_NAME + " ( " + TODO_ID + " integer primary key autoincrement, " + TODO_TITLE +
                " text, " + TODO_DATE + " text, " + TODO_TIME + " text, " + TODO_DESCRIPTION + " text);";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
