package com.example.shoppingbkd;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.barkoder.Barkoder;
import com.barkoder.BarkoderConfig;
import com.barkoder.BarkoderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    private EditText editTxt;
    private ListView list;
    private CustomAdapter adapter;
    private ArrayList<ListObject> arrayList;
    private BarkoderView barkoderView;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Button btnNewLists, btnsessions, btnInformation;
    private final int REQ_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        editTxt = (EditText) findViewById(R.id.edittext);
        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<ListObject>();
        btnNewLists = (Button) findViewById(R.id.btnNewLists);
        btnInformation = (Button) findViewById(R.id.btnInformation);
        btnsessions = (Button) findViewById(R.id.btnsessions);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        barkoderView = findViewById(R.id.bkdView);
        createBKDConfig();


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        getSupportActionBar().setTitle("Default List");

        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(this.getResources().getColor(R.color.black));


        adapter = new CustomAdapter();

        list.setAdapter(adapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        loadArray(this);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() != 0) {
            list.setAdapter(adapter);
        } else {
            Toast.makeText(ListActivity.this, "No Items in the list", Toast.LENGTH_SHORT).show();
        }

        btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, InformationActivity.class);
                startActivity(intent);
            }
        });
        // Buttons in the Drawer

        btnNewLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, NewListActivity.class);
                startActivity(intent);
            }
        });


        btnsessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, SessionsActivity.class);
                startActivity(intent);
            }
        });
        // add a name for the item in the list
        editTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        ListActivity.this.editTxt.getWindowToken(), 0);
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    ListObject object = new ListObject(findCurrentMaxListItemID());
                    object.setName(editTxt.getText().toString());

                    arrayList.add(object);
                    saveArray();
                    adapter.notifyDataSetChanged();
                    editTxt.setText("");
                    editTxt.clearFocus();


                }
                return false;
            }

        });

        // information about the item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);

                if (!arrayList.get(position).getType().equals(""))
                    builder.setMessage(arrayList.get(position).getType() + ": " + arrayList.get(position).getCode());


                builder.setCancelable(false);
                builder.setTitle(arrayList.get(position).getName());
                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                builder.setNegativeButton("SHARE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Here is the share content body";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Like to Share this barcode");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.scan, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // googltalk icon
    // scan icon

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.scan:
                barkoderView.startScanning((results, bitmap) -> addNewList(results));
                break;
            case R.id.google_talk:

                promptSpeechInput();

                break;
            default:
                return false;
        }
        return true;
    }

    private void addNewList(Barkoder.Result[] results) {
        if (results.length > 0) {
            ListObject object = new ListObject(findCurrentMaxListItemID());
            object.setName(results[0].textualData);

            arrayList.add(object);
            saveArray();
            adapter.notifyDataSetChanged();
        }
    }

    private void editListItem(Barkoder.Result[] results, int itemPosition) {
        if (results.length > 0) {
            arrayList.get(itemPosition).setType(results[0].barcodeTypeName);
            arrayList.get(itemPosition).setCode(results[0].textualData);

            saveArray();
            adapter.notifyDataSetChanged();
        }
    }

    // Adapter
    private Context context;

    public class CustomAdapter extends BaseAdapter {
        public CustomAdapter() {

            super();
        }

        Holder holder;

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return arrayList.indexOf(getItem(position));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.list_row, parent, false);
            }

            holder.txtCell = (TextView) convertView.findViewById(R.id.list_row);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.imgCode = (ImageView) convertView.findViewById(R.id.imgCode);
            holder.editname = (ImageView) convertView.findViewById(R.id.editname);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.txtCell.setText(arrayList.get(position).getName());

            holder.txtCell.setText(arrayList.get(position).getName());
            holder.txtDescription.setVisibility(arrayList.get(position).getType().equals("") ? View.GONE : View.VISIBLE);
            holder.txtDescription.setText((arrayList.get(position).getType() + ": " + arrayList.get(position).getCode()));

            final int finalPosition = position;

            // delete item from the list
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(ListActivity.this);
                    adb.setMessage("Are you sure you want to delete " + arrayList.get(finalPosition).getName());
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            arrayList.remove(finalPosition);
                            adapter.notifyDataSetChanged();
                            saveArray();
                        }
                    });
                    adb.show();


                }
            });


            // add the barcode
            holder.imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(ListActivity.this);
                    adb.setMessage("Add barcode for this item " + arrayList.get(finalPosition).getName());
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            barkoderView.startScanning((results, bitmap) -> editListItem(results, position));
                        }
                    });
                    adb.show();


                }
            });

            // editname: change the name of the item
            holder.editname.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder adb = new AlertDialog.Builder(ListActivity.this);
                    adb.setMessage("Are you sure you want to edit the name for " + arrayList.get(finalPosition).getName());
                    final EditText input = new EditText(ListActivity.this);
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
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            ListObject object = arrayList.get(finalPosition);
                            object.setName(input.getText().toString());
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
        }

    }

    // load the list
    public void loadArray(Context mContext) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        arrayList.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            try {
                JSONObject cacheJSON = new JSONObject(mSharedPreference1.getString("Status_" + i, null));
                ListObject object = new ListObject(cacheJSON.getInt("id"));
                object.setName(cacheJSON.getString("name"));
                object.setCode(cacheJSON.getString("code"));
                object.setType(cacheJSON.getString("type"));

                arrayList.add(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // SharedPreferences save the list
    public boolean saveArray() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("Status_size", arrayList.size());


        for (int i = 0; i < arrayList.size(); i++) {
            mEdit1.remove("Status_" + i);
            try {
                JSONObject cacheJSON = new JSONObject();

                cacheJSON.put("id", arrayList.get(i).getId());
                cacheJSON.put("name", arrayList.get(i).getName());
                cacheJSON.put("code", arrayList.get(i).getCode());
                cacheJSON.put("type", arrayList.get(i).getType());

                mEdit1.putString("Status_" + i, cacheJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mEdit1.commit();

    }

    @Override
    public void onBackPressed() {
        // TODO use this from latest barkoder SDK
        //if (barkoderView.getState() == BarkoderState.SCANNING)
        //     barkoderView.stopScanning();
        //else
        finish();
    }

    private int findCurrentMaxListItemID() {
        int maxID = -1;
        for (ListObject item : arrayList) {
            if (item.getId() > maxID)
                maxID = item.getId();
        }

        return maxID + 1;
    }

    // google talk
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // google talk code
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editTxt.setText(result.get(0));

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


                }
                break;
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

    //endregion Barkoder
}
