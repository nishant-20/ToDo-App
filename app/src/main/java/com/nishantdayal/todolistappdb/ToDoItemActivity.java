package com.nishantdayal.todolistappdb;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.nishantdayal.todolistappdb.ToDoOpenHelper.getToDoOpenHelperInstance;

public class ToDoItemActivity extends FragmentActivity {

    ImageButton title_voice_button,description_voice_button;
    TextView todo_idtextview;
    EditText title;
    static EditText d_date;
    static EditText t_time;
    EditText description;
    Button submitbutton;
    int todo_id;
    int REQUEST_CODE_FOR_TITLE = 1,REQUEST_CODE_FOR_DESCRIPTION = 2;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
                && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {

            int scroords[] = new int[2];
            view.getLocationOnScreen(scroords);

            float x = ev.getRawX() + view.getLeft() - scroords[0];
            float y = ev.getRawY() + view.getRight() - scroords[1];
            if (x < view.getLeft() || x > view.getRight() || y > view.getBottom() || y < view.getTop())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_item);

        todo_idtextview = (TextView) findViewById(R.id.id_secondactivity);
        title = (EditText) findViewById(R.id.title_secondactivity);
        d_date = (EditText) findViewById(R.id.date_secondactivity);
        t_time = (EditText) findViewById(R.id.time_secondactivity);
        description = (EditText) findViewById(R.id.description_secondactivity);
        submitbutton = (Button) findViewById(R.id.submitbutton);

        title_voice_button = (ImageButton) findViewById(R.id.title_voice_button);
        title_voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voicetotext("Title");
            }
        });
        description_voice_button = (ImageButton) findViewById(R.id.description_voice_button);
        description_voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voicetotext("Description");
            }
        });

        final Intent i1 = getIntent();

        //Update Operation
        todo_id = i1.getIntExtra(IntentConstants.TODO_ID, -1);

        if(todo_id==-1){
            todo_idtextview.setText("NEW TASK");
        }
        else if (todo_id != -1) {
            todo_idtextview.setText("MODIFY TASK");
            ToDoOpenHelper toDoOpenHelper = new ToDoOpenHelper(ToDoItemActivity.this);
            SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
//            ToDoOpenHelper.TODO_ID+"="+todo_id
            Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME,null,null,null,null,null,null);

            cursor.moveToFirst();
            if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id);
            else
                while(cursor.moveToNext()){
                    if(cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID))==todo_id)
                        break;
                }

            title.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE)));
            d_date.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE)));
            t_time.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME)));
            description.setText(cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION)));
        }

        d_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepickerdialog(view);
            }
        });
        t_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showtimepickerdialog(view);
            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ret_td_title = title.getText().toString();
                String ret_td_date = d_date.getText().toString();
                String ret_td_time = t_time.getText().toString();

                if (ret_td_title.trim().isEmpty()) {
                    Toast.makeText(ToDoItemActivity.this, "Title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return; }
                if(ret_td_date.isEmpty() && !ret_td_time.isEmpty()){
                    Toast.makeText(ToDoItemActivity.this, "Enter the date and try again.", Toast.LENGTH_SHORT).show();
                    return; }
                if(!ret_td_date.isEmpty() && ret_td_time.isEmpty()){
                    Toast.makeText(ToDoItemActivity.this, "Enter the time and try again.", Toast.LENGTH_SHORT).show();
                    return; }

                if(true){

                }
                updatesql();

                finish();
            }
        });

    }

    public void setalarm(){
        Long millis=0l;

        AlarmManager am = (AlarmManager) ToDoItemActivity.this.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(ToDoItemActivity.this,AlarmReceiver.class);
        i.putExtra(IntentConstants.TODO_ID,todo_id);

//        Intent i = new Intent(ToDoItemActivity.this,AlarmActivity.class);
//        i.putExtra(IntentConstants.TODO_ID,todo_id);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoItemActivity.this,todo_id,i,0);

        //Calculating epoch time from date and time
        try{
            millis = new SimpleDateFormat("dd/MM/yyyy").parse(d_date.getText().toString()).getTime();

            String[] time = t_time.getText().toString().split(":");

            millis += (Integer.parseInt(time[0])*60 + Integer.parseInt(time[1]))*60*1000;

//            Log.i("Epoch",millis+"");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        am.set(AlarmManager.RTC_WAKEUP,millis,pendingIntent);
    }

    public void updatesql(){
        String ret_td_title = title.getText().toString();
        String ret_td_date = d_date.getText().toString();
        String ret_td_time = t_time.getText().toString();
        String ret_td_description = description.getText().toString();

        ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
        SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();

        //Add
        if (todo_id == -1) {
            ContentValues cv = new ContentValues();

            cv.put(ToDoOpenHelper.TODO_TITLE, ret_td_title);
            cv.put(ToDoOpenHelper.TODO_DATE, ret_td_date);
            cv.put(ToDoOpenHelper.TODO_TIME, ret_td_time);
            cv.put(ToDoOpenHelper.TODO_DESCRIPTION, ret_td_description);

            database.insert(ToDoOpenHelper.TABLE_NAME, null, cv);

            //Id is needed for  set alarm function
            Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME,null,null,null,null,null,null);
            cursor.moveToLast();
            todo_id = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));

            if(!ret_td_date.isEmpty() && !ret_td_time.isEmpty())
                setalarm();

            setResult(MainActivity.DATA_ADDED);
        } else {
            ContentValues cv = new ContentValues();

            cv.put(ToDoOpenHelper.TODO_TITLE, ret_td_title);
            cv.put(ToDoOpenHelper.TODO_DATE, ret_td_date);
            cv.put(ToDoOpenHelper.TODO_TIME, ret_td_time);
            cv.put(ToDoOpenHelper.TODO_DESCRIPTION, ret_td_description);

            database.update(ToDoOpenHelper.TABLE_NAME, cv, ToDoOpenHelper.TODO_ID + "=" + todo_id, null);

            if(!ret_td_date.isEmpty() && !ret_td_time.isEmpty())
                setalarm();

            setResult(MainActivity.DATA_CHANGED);
        }

    }
    public void showdatepickerdialog(View v) {
        DialogFragment newfragment = new DatePickerfragment();
        newfragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerfragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar myCalendar = Calendar.getInstance();
            int year = myCalendar.get(Calendar.YEAR);
            int month = myCalendar.get(Calendar.MONTH);
            int day = myCalendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            if(day<10 && month<9)
                d_date.setText("0"+ day + "/" + "0" +(month + 1) + "/" + year);
            else if(day<10 && month>=9)
                d_date.setText("0"+ day + "/" + (month + 1) + "/" + year);
            else if(day>=10 && month<9)
                d_date.setText(day + "/" + "0" +(month + 1) + "/" + year);
            else
                d_date.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    public void showtimepickerdialog(View v) {
        DialogFragment newfragment = new TimePickerFragment();
        newfragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @TargetApi(Build.VERSION_CODES.N)
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar myCalendar = Calendar.getInstance();
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int min = myCalendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            if(hour<10 && min<10)
                t_time.setText("0" + hour + ":" + "0" + min);
            else if(hour<10 && min>=10)
                t_time.setText("0" + hour + ":" + min);
            else if(hour>=10 && min<10)
                t_time.setText(hour + ":" + "0" + min);
            else
                t_time.setText(hour + ":" + min);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    protected void voicetotext(String dialog_text){
        int REQUEST_CODE=4;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,dialog_text);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE);

        if(dialog_text.equals("Title")) {
            startActivityForResult(intent, REQUEST_CODE_FOR_TITLE);
        }
        else if(dialog_text.equals("Description")){
            startActivityForResult(intent,REQUEST_CODE_FOR_DESCRIPTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_FOR_TITLE){
                title.setText(title.getText().toString()+" "+(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0));
            }
            else if(requestCode==REQUEST_CODE_FOR_DESCRIPTION){
                description.setText(description.getText()+" "+ (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0));
            }
        }
    }
}
