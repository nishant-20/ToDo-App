package com.nishantdayal.todolistappdb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NishantDayal on 23/06/17.
 */

public class ToDoListAdapter extends ArrayAdapter<ToDoNote> {
    ArrayList<ToDoNote> listitems;
    Context context;

    public ToDoListAdapter(Context context, ArrayList<ToDoNote> listitems){
        super(context,0);
        this.context = context;
        this.listitems = listitems;
    }

    @Override
    public int getCount() {
        return this.listitems.size();
    }

    static class ToDoViewHolder{
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;

        ToDoViewHolder(TextView titleTextView,TextView dateTextView,TextView timeTextView){
            this.titleTextView = titleTextView;
            this.dateTextView = dateTextView;
            this.timeTextView = timeTextView;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.todo_listitem, null);

            LinearLayout todoitemlayout = (LinearLayout) convertView.findViewById(R.id.todo_item_layout);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.titleview);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.dateview);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.timeview);

            todoitemlayout.setBackgroundResource(R.drawable.todoitemmodify);

            ToDoViewHolder toDoViewHolder = new ToDoViewHolder(titleTextView,dateTextView,timeTextView);
            convertView.setTag(toDoViewHolder);
        }

        ToDoNote td = listitems.get(position);
        ToDoViewHolder toDoViewHolder = (ToDoViewHolder) convertView.getTag();

        toDoViewHolder.titleTextView.setText(td.title);
        toDoViewHolder.dateTextView.setText(td.date);
        toDoViewHolder.timeTextView.setText(td.time);

        return convertView;
    }
}
