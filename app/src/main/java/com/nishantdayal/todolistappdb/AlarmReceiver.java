package com.nishantdayal.todolistappdb;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    int todo_id;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        todo_id=intent.getIntExtra(IntentConstants.TODO_ID,-1);

        ToDoOpenHelper toDoOpenHelper = new ToDoOpenHelper(context);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id);
        else
            while(cursor.moveToNext()){
                if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id)
                    break;
            }

//        Toast.makeText(context, "Alarm is Ringing !! ", Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE)))
                .setContentText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME)))
                .setSmallIcon(R.drawable.todo_icon);

        Intent resultintent = new Intent(context,ToDoItemActivity.class);
        resultintent.putExtra(IntentConstants.TODO_ID,todo_id);

        PendingIntent resultpendingintent = PendingIntent.getActivity(context,todo_id,resultintent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultpendingintent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(todo_id,builder.build());

        Intent movetoactivity = new Intent(context,AlarmActivity.class);
        movetoactivity.putExtra(IntentConstants.TODO_ID,todo_id);
        movetoactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(movetoactivity);

    }
}
