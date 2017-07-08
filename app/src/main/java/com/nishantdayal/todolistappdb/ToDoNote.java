package com.nishantdayal.todolistappdb;

import java.io.Serializable;

/**
 * Created by NishantDayal on 23/06/17.
 */

public class ToDoNote implements Serializable{
    int id;
    String title;
    String date;
    String time;
    String description;

    ToDoNote(int id,String title,String date,String time,String description){
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
    }
}
