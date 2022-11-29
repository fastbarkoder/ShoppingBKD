package com.example.shoppingbkd;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.barkoder.Barkoder;
import com.barkoder.BarkoderConfig;
import com.barkoder.BarkoderView;
import com.barkoder.interfaces.BarkoderResultCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ManageListActivity extends AppCompatActivity {
    ListView manage_list;
    EditText new_items;
    FloatingActionButton button_done;
    private ManageListAdapter adapter;
    private ArrayList<ManageListObject> arraymanagelist;
    private final int CODE_SHOPPING = 1;
    private final int CODE_SCANNER = 2;
    private boolean click;
    RelativeLayout activity_manage_list;
    int list_id;
    String data;
    private BarkoderView barkoderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("List");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));


        activity_manage_list = (RelativeLayout) findViewById(R.id.activity_manage_list);
        manage_list = (ListView) findViewById(R.id.manage_list);
        new_items = (EditText) findViewById(R.id.new_items);
        button_done = (FloatingActionButton) findViewById(R.id.button_done);
        barkoderView = findViewById(R.id.bkdView1);
        createBKDConfig();

        arraymanagelist = new ArrayList<ManageListObject>();

        adapter = new ManageListAdapter();

        manage_list.setAdapter(adapter);
        data = getIntent().getExtras().getString("name");
        getSupportActionBar().setTitle(data);

        list_id = getIntent().getIntExtra("id", 0);

        loadArray(this);
        adapter.notifyDataSetChanged();

        new_items.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ManageListActivity.this.new_items.getWindowToken(), 0);
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    ManageListObject object_manage_list = new ManageListObject();
                    object_manage_list.name = new_items.getText().toString();

                    arraymanagelist.add(object_manage_list);
                    saveArray();
                    adapter.notifyDataSetChanged();
                    new_items.setText("");
                    new_items.clearFocus();

                }
                return false;
            }
        });

        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Snackbar snackbar = Snackbar
                        .make(activity_manage_list, "Your shopping list is saved !", Snackbar.LENGTH_LONG);
                snackbar.show();
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ManageListActivity.this, SessionsActivity.class);

                        intent.putExtra("name", data);
                        intent.putExtra("list_id", list_id);
                        startActivity(intent);

                        finish();


                    }
                },  1000);


            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.scun_manage_list:
                barkoderView.startScanning((results, bitmap) -> addNewList(results));
                break;
            case R.id.shopping:

                if (click) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.list)));

                        Window w = getWindow();
                        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        w.setStatusBarColor(this.getResources().getColor(R.color.list));
                        getSupportActionBar().setTitle("List");
                        new_items.setVisibility(View.VISIBLE);
                        button_done.setVisibility(View.GONE);


                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.accentcolor)));

                        Window w = getWindow();
                        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        w.setStatusBarColor(this.getResources().getColor(R.color.accentcolor));
                        new_items.setVisibility(GONE);
                        button_done.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle("Shopping");


                }
                for (ManageListObject mso : arraymanagelist) {
                    mso.checked = false;
                }
                adapter.notifyDataSetChanged();
                click = !click;

                break;
            default:
                return false;
        }
        return true;
    }

    private void addNewList(Barkoder.Result[] results) {
        if (results.length > 0) {
            ManageListObject object = new ManageListObject();
            object.type = results[0].barcodeTypeName;
            object.code = results[0].textualData;
            arraymanagelist.add(object);
            saveArray();
            adapter.notifyDataSetChanged();
        }
    }

    private void editListItem(Barkoder.Result[] results, int itemPosition) {
        if (results.length > 0) {
            arraymanagelist.get(itemPosition).type = results[0].barcodeTypeName;
            arraymanagelist.get(itemPosition).code = results[0].textualData;
            saveArray();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO use this from latest barkoder SDK
        //if (barkoderView.getState() == BarkoderState.SCANNING)
        //     barkoderView.stopScanning();
        //else
        finish();
    }

    public class ManageListAdapter extends BaseAdapter {
        public ManageListAdapter() {

            super();
        }

        Holder holder;


        @Override
        public int getCount() {
            return arraymanagelist.size();
        }

        @Override
        public Object getItem(int position) {
            return arraymanagelist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return arraymanagelist.indexOf(getItem(position));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.list_row, parent, false);
            }

            holder.rlRow = (RelativeLayout) convertView.findViewById(R.id.rlRow);
            holder.txtCell = (TextView) convertView.findViewById(R.id.list_row);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.imgCode = (ImageView) convertView.findViewById(R.id.imgCode);
            holder.editname = (ImageView) convertView.findViewById(R.id.editname);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.txtCell.setText(arraymanagelist.get(position).name);

            holder.txtDescription.setVisibility(arraymanagelist.get(position).type.equals("") ? GONE : View.VISIBLE);
            holder.txtDescription.setText((arraymanagelist.get(position).type + ": " + arraymanagelist.get(position).code));

            final int finalPosition = position;

            holder.rlRow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (click) {

                        if (arraymanagelist.get(position).getChecked()) {
                            arraymanagelist.get(position).checked = false;


                        } else {
                            arraymanagelist.get(position).checked = true;


                        }
                        adapter.notifyDataSetChanged();

                    }
                }
            });

            if (arraymanagelist.get(position).getChecked()) {
                holder.txtCell.setPaintFlags(holder.txtCell.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtCell.setTextColor(getResources()
                        .getColor(R.color.accentcolor));

            } else {
                holder.txtCell.setPaintFlags(holder.txtCell.getPaintFlags()
                        & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.txtCell.setTextColor(getResources().getColor(
                        R.color.shop_off_text_color));
            }

            if (click) {

                holder.imgCode.setVisibility(GONE);
                holder.delete.setVisibility(GONE);
                holder.editname.setVisibility(GONE);

            } else {

                holder.imgCode.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.editname.setVisibility(View.VISIBLE);

            }


            // delete item from the list
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(ManageListActivity.this);
                    adb.setMessage("Are you sure you want to delete " + arraymanagelist.get(finalPosition).name);
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("YES", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            arraymanagelist.remove(finalPosition);
                            adapter.notifyDataSetChanged();
                            saveArray();
                        }
                    });
                    adb.show();


                }
            });

            // click on image in left ( barcode icon ) opens the barkoder
            holder.imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(ManageListActivity.this);
                    adb.setMessage("Add barcode for this item " + arraymanagelist.get(finalPosition).name);
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Add", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            barkoderView.startScanning((results, bitmap) -> editListItem(results, position));
                        }
                    });
                    adb.show();


                }
            });

            // editname - change the name of the item
            holder.editname.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder adb = new AlertDialog.Builder(ManageListActivity.this);
                    adb.setTitle("Edit name: " + arraymanagelist.get(finalPosition).name);
                    final EditText input = new EditText(ManageListActivity.this);
                    input.setText(arraymanagelist.get(finalPosition).name);
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

                            ManageListObject object_manage_list = arraymanagelist.get(finalPosition);
                            object_manage_list.name = input.getText().toString();
                            saveArray();
                            adapter.notifyDataSetChanged();
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
            TextView txtCell, txtDescription;
            ImageView delete, imgCode, editname;
            RelativeLayout rlRow;
        }

    }
    // save the list in the SharedPreferences
    public boolean saveArray() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("list_" + list_id + "_itemcount", arraymanagelist.size());

        for (int i = 0; i < arraymanagelist.size(); i++) {
            mEdit1.remove("list_" + list_id + "_item_" + i);
            try {
                JSONObject cacheJSON = new JSONObject();
                cacheJSON.put("name", arraymanagelist.get(i).name);
                cacheJSON.put("code", arraymanagelist.get(i).code);
                cacheJSON.put("type", arraymanagelist.get(i).type);
                cacheJSON.put("list_id", list_id);


                mEdit1.putString("list_" + list_id + "_item_" + i, cacheJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mEdit1.commit();

    }

    // load the list with items
    public void loadArray(Context mContext) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        arraymanagelist.clear();
        int size = mSharedPreference1.getInt("list_" + list_id + "_itemcount", 0);

        for (int i = 0; i < size; i++) {
            try {
                JSONObject cacheJSON = new JSONObject(mSharedPreference1.getString("list_" + list_id + "_item_" + i, ""));
                ManageListObject object_manage_list = new ManageListObject();
                object_manage_list.name = cacheJSON.getString("name");
                object_manage_list.code = cacheJSON.getString("code");
                object_manage_list.type = cacheJSON.getString("type");
                object_manage_list.list_id = cacheJSON.getInt("list_id");

                arraymanagelist.add(object_manage_list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    //region Barkoder

    private void createBKDConfig() {
        barkoderView.config = new BarkoderConfig(getApplicationContext(), "PEmBIohr9EZXgCkySoetbwP4gvOfMcGzgxKPL2X6uqNVHcuY6VqMuygBIANbBCVEvF7iIBvTOEJRU1oIziR_dEkkn7sGjDpbGXRDl7Aa1uyRCFVV_Abuzc-razZa22B4cKra8yWanC8FSeJA-pkgQnGF3q_vwIgqfvJ1wsbCA6kDZYxPwrQkYuhSeCz1fkR8Kevy-_BzIzJd-XHebyS-OCDhfLNxYta3xW6OsXmOTgZPcYo4Hnu_2UqUI1CkNnjlbiUPw8VqEN-PhO5m-YzG8Bs3ilPiQ2qSuGscGolOE_Y.",
                new Barkoder.LicenseCheckListener() {
                    @Override
                    public void onLicenseCheck(Barkoder.LicenseCheckResult licenseCheckResult) {
                        Log.i("License info", licenseCheckResult.message);
                    }
                });

        barkoderView.config.getDecoderConfig().SetEnabledDecoders(new Barkoder.DecoderType[]{
                Barkoder.DecoderType.Aztec,
                Barkoder.DecoderType.AztecCompact,
                Barkoder.DecoderType.QR,
                Barkoder.DecoderType.QRMicro,
                Barkoder.DecoderType.Code128,
                Barkoder.DecoderType.Code93,
                Barkoder.DecoderType.Code39,
                Barkoder.DecoderType.Codabar,
                Barkoder.DecoderType.Code11,
                Barkoder.DecoderType.Msi,
                Barkoder.DecoderType.UpcA,
                Barkoder.DecoderType.UpcE,
                Barkoder.DecoderType.UpcE1,
                Barkoder.DecoderType.Ean13,
                Barkoder.DecoderType.Ean8,
                Barkoder.DecoderType.PDF417,
                Barkoder.DecoderType.PDF417Micro,
                Barkoder.DecoderType.Datamatrix
        });
    }

    private int barcodeExistsInProdict(String code) {
        for (int i = 0; i < arraymanagelist.size(); i++) {
            ManageListObject object = arraymanagelist.get(i);
            if (object.code.equals(code)) {
                return i;
            }
        }

        return -1;
    }
}
