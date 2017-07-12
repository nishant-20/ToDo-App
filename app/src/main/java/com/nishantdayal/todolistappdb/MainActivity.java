package com.nishantdayal.todolistappdb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.nishantdayal.todolistappdb.ToDoOpenHelper.getToDoOpenHelperInstance;

public class MainActivity extends AppCompatActivity implements onCheckBoxClickListener,NavigationView.OnNavigationItemSelectedListener{
    public final static int DATA_CHANGED = 1;
    public final static int DATA_ADDED = 2;

    ListView listview;
    ArrayList<ToDoNote> listitems;
    ToDoListAdapter todoAdapter;
    int changepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("To Do List");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ToDoItemActivity.class);
                startActivityForResult(i, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listitems = new ArrayList<>();
        listview = (ListView) findViewById(R.id.todolistview);
        todoAdapter = new ToDoListAdapter(this, listitems);
        todoAdapter.setonCheckBoxClickListener(this);
        listview.setAdapter(todoAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent i = new Intent(MainActivity.this, ToDoItemActivity.class);
                i.putExtra(IntentConstants.TODO_ID, listitems.get(position).id);
                changepos = position;
                startActivityForResult(i, 1);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("REMOVE").setCancelable(false).setMessage("Are you sure ?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ToDoNote temp = listitems.remove(position);
                        todoAdapter.notifyDataSetChanged();

                        Snackbar snackbar = Snackbar.make(view,temp.title+" deleted.",Snackbar.LENGTH_LONG);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        listitems.add(position,temp);
                                        todoAdapter.notifyDataSetChanged();
                                    }
                                });

                        snackbar.show();
                        snackbar.addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if(event==DISMISS_EVENT_CONSECUTIVE || event==DISMISS_EVENT_SWIPE || event==DISMISS_EVENT_TIMEOUT){
                                    ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(MainActivity.this);
                                    SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();

                                    database.delete(ToDoOpenHelper.TABLE_NAME,ToDoOpenHelper.TODO_ID+"="+temp.id,null);
                                    AlarmManager am_mainactivity = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);

                                    PendingIntent cancelpendingIntent =  PendingIntent.getBroadcast(MainActivity.this,temp.id,new Intent(MainActivity.this,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                                    am_mainactivity.cancel(cancelpendingIntent);
                                }
                            }

                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        updatetodolist_atbeggining();
    }

    private void updatetodolist_atbeggining() {
        ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {

            int temp_id = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            String temp_title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String temp_date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE));
            String temp_time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME));
            String temp_description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));
            int temp_isCompleted = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ISCOMPLETED));

            ToDoNote temp = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description,temp_isCompleted);
            listitems.add(temp);
        }

        todoAdapter.notifyDataSetChanged();
    }

    private void updatetodolist_on_add(int position) {
        ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();

        Cursor cursor = database.query(ToDoOpenHelper.TABLE_NAME, null, null, null, null, null, null);

        if (position == -1) {
            cursor.moveToLast();

            int temp_id = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            String temp_title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String temp_date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE));
            String temp_time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME));
            String temp_description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));
            int temp_isCompleted = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ISCOMPLETED));

            ToDoNote td = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description,temp_isCompleted);
            listitems.add(td);
        } else {
            cursor.moveToPosition(position);

            int temp_id = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            String temp_title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String temp_date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE));
            String temp_time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME));
            String temp_description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));
            int temp_isCompleted = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ISCOMPLETED));

            ToDoNote td = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description,temp_isCompleted);
            listitems.remove(position);
            listitems.add(position, td);
        }

        todoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == DATA_ADDED) {
                //UpdateDb
                updatetodolist_on_add(-1);
            } else if (resultCode == DATA_CHANGED) {
                //UpdateDb
                updatetodolist_on_add(changepos);
            } else {
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.menuremovetodoitem) {
//            if (listitems.size() == 0)
//                return true;
//
//            postoremove = -1;
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            builder.setTitle("REMOVE");
//            builder.setCancelable(false);
//            builder.setMessage("Enter the title to delete ??");
//
//            View v = getLayoutInflater().inflate(R.layout.remove_dialogview, null);
//            final EditText removeelement = (EditText) v.findViewById(R.id.removeelementtitle);
//            builder.setView(v);
//
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int position) {
//                    for (int i = 0; i < listitems.size(); i++) {
//                        if (listitems.get(i).title.equals(removeelement.getText().toString())) {
//                            postoremove = i;
//                            break;
//                        }
//                    }
//
//                    if (postoremove != -1) {
//                        updatetodolist_on_remove(postoremove);
//                    } else {
//                        Toast.makeText(MainActivity.this, "No such element with " + removeelement.getText().toString() + " title found.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        } else

        if (id == R.id.aboutsus) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.google.com");
            i.setData(uri);
            startActivity(i);
        } else if (id == R.id.contactus) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_DIAL);
            Uri uri = Uri.parse("tel:9891692696");
            i.setData(uri);
            startActivity(i);
        } else if (id == R.id.emailus) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SENDTO);
            Uri uri = Uri.parse("mailto:daya.nishant1997@gmail.com");
            i.putExtra(i.EXTRA_SUBJECT, "Feedback");
            i.setData(uri);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            }
        }

        return true;
    }


    @Override
    public void onCheckBoxClicked(View v, int pos) {
        CheckBox checkBox = (CheckBox) v;


        if(!checkBox.isChecked()){
            Log.i("Insidemain","NotChecked");

            Long millis = 0l;
            ToDoNote temp = listitems.get(pos);
            temp.isCompleted = 0;
            listitems.remove(pos);
            listitems.add(pos,temp);
            todoAdapter.notifyDataSetChanged();

            ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
            SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(ToDoOpenHelper.TODO_TITLE, temp.title);
            cv.put(ToDoOpenHelper.TODO_DATE, temp.date);
            cv.put(ToDoOpenHelper.TODO_TIME, temp.time);
            cv.put(ToDoOpenHelper.TODO_DESCRIPTION, temp.description);
            cv.put(ToDoOpenHelper.TODO_ISCOMPLETED,temp.isCompleted);

            database.update(ToDoOpenHelper.TABLE_NAME, cv, ToDoOpenHelper.TODO_ID + "=" + temp.id, null);

            AlarmManager am_mainactivity = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(MainActivity.this,AlarmReceiver.class);
            i.putExtra(IntentConstants.TODO_ID,temp.id);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(MainActivity.this,temp.id,i,PendingIntent.FLAG_UPDATE_CURRENT);
            try{
                millis = new SimpleDateFormat("dd/MM/yyyy").parse(temp.date).getTime();
                String[] time = temp.time.split(":");
                millis += (Integer.parseInt(time[0])*60 + Integer.parseInt(time[1]))*60*1000;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            am_mainactivity.set(AlarmManager.RTC_WAKEUP,millis,pendingIntent);
        }
        else{
            Log.i("Insidemain","Checked");

            ToDoNote temp = listitems.get(pos);

            temp.isCompleted = 1;
            listitems.remove(pos);
            listitems.add(pos,temp);
            todoAdapter.notifyDataSetChanged();

            ToDoOpenHelper toDoOpenHelper = getToDoOpenHelperInstance(this);
            SQLiteDatabase database = toDoOpenHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(ToDoOpenHelper.TODO_TITLE, temp.title);
            cv.put(ToDoOpenHelper.TODO_DATE, temp.date);
            cv.put(ToDoOpenHelper.TODO_TIME, temp.time);
            cv.put(ToDoOpenHelper.TODO_DESCRIPTION, temp.description);
            cv.put(ToDoOpenHelper.TODO_ISCOMPLETED,temp.isCompleted);

            database.update(ToDoOpenHelper.TABLE_NAME, cv, ToDoOpenHelper.TODO_ID + "=" + temp.id, null);

            AlarmManager am_mainactivity = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);

            PendingIntent cancelpendingIntent =  PendingIntent.getBroadcast(MainActivity.this,temp.id,new Intent(MainActivity.this,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
            am_mainactivity.cancel(cancelpendingIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.overdue_tasks){
            Toast.makeText(this, "Overdue Tasks", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.completed_tasks){
            Toast.makeText(this, "Completed Tasks", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}