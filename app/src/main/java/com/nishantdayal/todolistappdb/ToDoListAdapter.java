package com.nishantdayal.todolistappdb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NishantDayal on 23/06/17.
 */

public class ToDoListAdapter extends ArrayAdapter<ToDoNote> {
    ArrayList<ToDoNote> listitems;
    Context context;
    onCheckBoxClickListener listener;

    void setonCheckBoxClickListener(onCheckBoxClickListener listener){
        this.listener = listener;
    }

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
        CheckBox istaskCompleted;
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;

        ToDoViewHolder(CheckBox istaskCompleted,TextView titleTextView,TextView dateTextView,TextView timeTextView){
            this.istaskCompleted = istaskCompleted;
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
            CheckBox istaskCompleted = (CheckBox) convertView.findViewById(R.id.istaskCompleted);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.titleview);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.dateview);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.timeview);

            todoitemlayout.setBackgroundResource(R.drawable.todoitemmodify);

            ToDoViewHolder toDoViewHolder = new ToDoViewHolder(istaskCompleted,titleTextView,dateTextView,timeTextView);
            convertView.setTag(toDoViewHolder);
        }

        final int pos = position;
        ToDoNote td = listitems.get(position);
        ToDoViewHolder toDoViewHolder = (ToDoViewHolder) convertView.getTag();
        if(td.isCompleted == 1)
            toDoViewHolder.istaskCompleted.setChecked(true);
        else {
            toDoViewHolder.istaskCompleted.setChecked(false);
//            Log.i("Tag",td.isCompleted+"");
        }


        toDoViewHolder.titleTextView.setText(td.title);
        toDoViewHolder.dateTextView.setText(td.date);
        toDoViewHolder.timeTextView.setText(td.time);
        toDoViewHolder.istaskCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                    listener.onCheckBoxClicked(view,pos);
            }
        });

        return convertView;
    }

}


interface onCheckBoxClickListener{
    void onCheckBoxClicked(View v,int pos);
}
