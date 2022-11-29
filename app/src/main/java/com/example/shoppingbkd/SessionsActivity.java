package com.example.shoppingbkd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SessionsActivity extends AppCompatActivity {
     ListView invoices_listview;

        private ArrayList<SessionsObject> arraysessions;
        private CustomAdapter adapter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sessions);

            invoices_listview = (ListView) findViewById(R.id.invoices_listview);
            arraysessions = new ArrayList<SessionsObject>();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sessions");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.material_actionbar)));

            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(this.getResources().getColor(R.color.material_actionbar));

            loadArray(this);
            adapter = new CustomAdapter();
            invoices_listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            if (getIntent().hasExtra("name")) {
                String name = getIntent().getStringExtra("name");
                int list_id = getIntent().getIntExtra("id", 0);

                SessionsObject object = new SessionsObject();
                object.name = name;
                object.list_id = list_id;

                arraysessions.add(object);
                saveArray();
                adapter.notifyDataSetChanged();
            }
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        // this is the adapter

        public class CustomAdapter extends BaseAdapter {
            public CustomAdapter() {

                super();
            }

            Holder holder;

            @Override
            public int getCount() {
                return arraysessions.size();
            }

            @Override
            public Object getItem(int position) {
                return arraysessions.get(position);
            }

            @Override
            public long getItemId(int position) {
                return arraysessions.indexOf(getItem(position));
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    holder = new Holder();
                    convertView = getLayoutInflater().inflate(R.layout.session_row, parent, false);
                }

                holder.txt_invoiceList = (TextView) convertView.findViewById(R.id.txt_invoiceList);
                holder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
                holder.txt_invoice_delete = (ImageView) convertView.findViewById(R.id.txt_invoice_delete);

                holder.txt_invoice_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(SessionsActivity.this);
                        adb.setMessage("Are you sure you want to delete " + arraysessions.get(position).name);
                        adb.setNegativeButton("Cancel", null);
                        adb.setPositiveButton("YES", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                arraysessions.remove(position);
                                adapter.notifyDataSetChanged();
                                saveArray();
                            }
                        });
                        adb.show();
                    }
                });

                holder.txt_invoiceList.setText(arraysessions.get(position).name);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                String formattedDate = df.format(c.getTime());
                holder.txt_date.setText("Date : " + formattedDate);


                return convertView;
            }

            public class Holder {
                TextView txt_invoiceList, txt_date;
                ImageView txt_invoice_delete;

            }

        }

        // save the list in the SharedPreferences
        public boolean saveArray() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor mEdit1 = sp.edit();

            mEdit1.putInt("Status_size_sessions", arraysessions.size());

            for (int i = 0; i < arraysessions.size(); i++) {
                mEdit1.remove("Status_sessions" + i);
                try {
                    JSONObject cacheJSON = new JSONObject();

                    cacheJSON.put("name", arraysessions.get(i).name);
                    cacheJSON.put("id", arraysessions.get(i).list_id);

                    mEdit1.putString("Status_sessions" + i, cacheJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return mEdit1.commit();

        }

        // load the list
        public void loadArray(Context mContext) {
            SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
            arraysessions.clear();
            int size = mSharedPreference1.getInt("Status_size_sessions", 0);
            for (int i = 0; i < size; i++) {
                try {
                    JSONObject cacheJSON = new JSONObject(mSharedPreference1.getString("Status_sessions" + i, null));
                    SessionsObject object = new SessionsObject();
                    object.name = cacheJSON.getString("name");
                    object.list_id = cacheJSON.getInt("id");


                    arraysessions.add(object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }
