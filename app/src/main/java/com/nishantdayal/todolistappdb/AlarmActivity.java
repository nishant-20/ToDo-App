package com.nishantdayal.todolistappdb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        Button dismiss_buttonalarmactivity = (Button) findViewById(R.id.dismiss_buttonalarmactivity);
        TextView descriptiontextViewalarmactivity = (TextView) findViewById(R.id.descriptiontextViewalarmactivity);

        Intent i = getIntent();
        todo_id = i.getIntExtra(IntentConstants.TODO_ID,-1);

        Log.i("Tag1",todo_id+"alarm activity");
        ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
        final SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();
        final Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME,null,ToDoOpenHelper.TODO_ID+"="+todo_id,null,null,null,null);
        cursor.moveToFirst();

        descriptiontextViewalarmactivity.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION)));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        final Ringtone rt = RingtoneManager.getRingtone(this,uri);
        if(rt!=null)
            rt.play();

        done_buttonalarmactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rt!=null)
                    rt.stop();

                ContentValues cv = new ContentValues();
                cv.put(ToDoOpenHelper.TODO_ID,cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID)));
                cv.put(ToDoOpenHelper.TODO_TITLE,cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE)));
                cv.put(ToDoOpenHelper.TODO_DATE,cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE)));
                cv.put(ToDoOpenHelper.TODO_TIME,cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME)));
                cv.put(ToDoOpenHelper.TODO_DESCRIPTION,cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION)));
                cv.put(ToDoOpenHelper.TODO_ISCOMPLETED,1);

                database.update(ToDoOpenHelper.TABLE_NAME,cv,ToDoOpenHelper.TODO_ID+"="+todo_id,null);

                finishAffinity();
            }
        });

        reschedule_buttonalarmactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmActivity.this,ToDoItemActivity.class);
                i.putExtra(IntentConstants.TODO_ID,todo_id);
                if(rt!=null)
                    rt.stop();
                startActivity(i);
            }
        });

        dismiss_buttonalarmactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rt!=null)
                    rt.stop();

                finish();
            }
        });
    }
}
