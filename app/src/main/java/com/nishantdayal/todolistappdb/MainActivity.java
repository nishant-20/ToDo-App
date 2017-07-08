package com.nishantdayal.todolistappdb;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.nishantdayal.todolistappdb.ToDoOpenHelper.getToDoOpenHelperInstance;

public class MainActivity extends AppCompatActivity {
    public final static int DATA_CHANGED = 1;
    public final static int DATA_ADDED = 2;

    ListView listview;
    ArrayList<ToDoNote> listitems;
    ToDoListAdapter todoAdapter;
    int changepos, postoremove;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        listitems = new ArrayList<>();

        listview = (ListView) findViewById(R.id.todolistview);
        todoAdapter = new ToDoListAdapter(this, listitems);
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

            ToDoNote temp = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description);
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

            ToDoNote td = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description);
            listitems.add(td);
        } else {
            cursor.moveToPosition(position);

            int temp_id = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            String temp_title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String temp_date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DATE));
            String temp_time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TIME));
            String temp_description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));

            ToDoNote td = new ToDoNote(temp_id, temp_title, temp_date, temp_time, temp_description);
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
}