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
    int isCompleted;                  // 0 for false 1 for true

    ToDoNote(int id,String title,String date,String time,String description,int isCompleted){
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
