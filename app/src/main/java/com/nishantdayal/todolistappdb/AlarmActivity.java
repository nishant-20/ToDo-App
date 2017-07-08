package com.nishantdayal.todolistappdb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.nishantdayal.todolistappdb.ToDoOpenHelper.getToDoOpenHelperInstance;

public class AlarmActivity extends AppCompatActivity {
    int todo_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button done_buttonalarmactivity = (Button) findViewById(R.id.done_buttonalarmactivity);
        Button reschedule_buttonalarmactivity = (Button) findViewById(R.id.reschedule_buttonalarmactivity);
        TextView descriptiontextViewalarmactivity = (TextView) findViewById(R.id.descriptiontextViewalarmactivity);

        Intent i = getIntent();
        todo_id = i.getIntExtra(IntentConstants.TODO_ID,-1);

        ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id);
        else
            while(cursor.moveToNext()){
                if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id)
                    break;
            }

        descriptiontextViewalarmactivity.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION)));

        done_buttonalarmactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        reschedule_buttonalarmactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmActivity.this,ToDoItemActivity.class);
                startActivity(i);
            }
        });
    }
}
