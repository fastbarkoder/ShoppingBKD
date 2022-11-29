package com.example.shoppingbkd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewListActivity extends AppCompatActivity {
    private EditText addnewlist;
    private ListView new_list;

    private ArrayList<NewListObject> array_newList;
    private CustomAdapterList adapter_list;
    private int maxID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));


            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));


        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional
        getSupportActionBar().setTitle("My Lists");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        addnewlist = (EditText) findViewById(R.id.addnewlist);
        new_list = (ListView) findViewById(R.id.new_list);
        array_newList = new ArrayList<NewListObject>();
        adapter_list = new CustomAdapterList();
        new_list.setAdapter(adapter_list);


        loadArray(this);
        adapter_list.notifyDataSetChanged();


        if (adapter_list.getCount() != 0) {
            new_list.setAdapter(adapter_list);
        } else {

            Toast.makeText(NewListActivity.this, "You have no list created", Toast.LENGTH_SHORT).show();
        }


        addnewlist.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        NewListActivity.this.addnewlist.getWindowToken(), 0);
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    NewListObject object = new NewListObject();
                    object.name_list = addnewlist.getText().toString();
                    object.id = maxID + 1;
                    maxID++;
                    array_newList.add(object);
                    saveArray();
                    adapter_list.notifyDataSetChanged();
                    addnewlist.setText("");
                    addnewlist.clearFocus();


                }
                return false;
            }

        });

//        array_newList.get(position).name_list
        new_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                Intent intent = new Intent(NewListActivity.this, ManageListActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", array_newList.get(position).id);
                intent.putExtra("name", array_newList.get(position).name_list);
                startActivity(intent);

            }

        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // save the list in the SharedPreferences
    public boolean saveArray() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("Status_size_list", array_newList.size());
        for (int i = 0; i < array_newList.size(); i++) {
            mEdit1.remove("Status_list" + i);
            try {
                JSONObject cacheJSON = new JSONObject();
                cacheJSON.put("name_list", array_newList.get(i).name_list);
                cacheJSON.put("id", array_newList.get(i).id);

                mEdit1.putString("Status_list" + i, cacheJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mEdit1.commit();

    }

    // load the list with items whe you open the application
    public void loadArray(Context mContext) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        array_newList.clear();
        int size = mSharedPreference1.getInt("Status_size_list", 0);

        for (int i = 0; i < size; i++) {
            try {
                JSONObject cacheJSON = new JSONObject(mSharedPreference1.getString("Status_list" + i, null));
                NewListObject object = new NewListObject();
                object.name_list = cacheJSON.getString("name_list");
                object.id = cacheJSON.getInt("id");

                if (object.id > maxID)
                    maxID = object.id;

                array_newList.add(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public class CustomAdapterList extends BaseAdapter {
        public CustomAdapterList() {

            super();
        }

        Holder holder;

        @Override
        public int getCount() {
            return array_newList.size();
        }

        @Override
        public Object getItem(int position) {
            return array_newList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return array_newList.indexOf(getItem(position));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.newlist_row, parent, false);
            }

            holder.list_name = (TextView) convertView.findViewById(R.id.list_name);
            holder.deletelist = (ImageView) convertView.findViewById(R.id.deletelist);
            holder.editlist = (ImageView) convertView.findViewById(R.id.editlist);
            holder.list_name.setText(array_newList.get(position).name_list);


            final int finalPosition = position;

            // delete items from the list
            holder.deletelist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(NewListActivity.this);
                    adb.setMessage("Are you sure you want to delete " + array_newList.get(finalPosition).name_list);
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("YES", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            array_newList.remove(finalPosition);
                            adapter_list.notifyDataSetChanged();
                            saveArray();
                        }
                    });
                    adb.show();


                }
            });


            // editname change the name of the item
            holder.editlist.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder adb = new AlertDialog.Builder(NewListActivity.this);
                    adb.setTitle("Edit name: " + array_newList.get(finalPosition).name_list);
                    final EditText input = new EditText(NewListActivity.this);
                    input.setText(array_newList.get(finalPosition).name_list);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    input.requestFocus();

                    adb.setView(input, 100, 50, 100, 0);
                    adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        }
                    });

                    InputMethodManager immm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    immm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    adb.setPositiveButton("DONE", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            NewListObject object = array_newList.get(finalPosition);
                            object.name_list = input.getText().toString();
                            saveArray();
                            adapter_list.notifyDataSetChanged();
                            input.setText("");


                            Toast.makeText(getApplicationContext(), "The name is changed", Toast.LENGTH_SHORT).show();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);


                        }
                    });
                    adb.show();


                }
            });


            return convertView;
        }

        public class Holder {
            TextView list_name;
            ImageView deletelist, editlist;
        }

    }


}
