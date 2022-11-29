package com.example.shoppingbkd;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_200)));

        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(this.getResources().getColor(R.color.red_200));

        getSupportActionBar().setTitle("Information");
        TextView tv = (TextView)findViewById(R.id.information);
        tv.setText("- Simple Usage\n" +
                "\n" +
                "Our decoder app is simple and easy to use!\n" +
                "\n" +
                "Just point to your barcode and lift-off!\n" +
                "\n" +
                "- High Performance\n" +
                "\n" +
                "Scan poorly printed, damaged or partially obscured barcodes in any environment with very high accuracy, superior quality and unprecedented reliability.\n" +
                "\n" +
                "- Time And Cost Reduction\n" +
                "\n" +
                "Increase efficiency, cost-saving, and improve customer satisfaction with BarKoder.\n" +
                "\n" +
                "There is no need to spend money on expensive external readers." +

                "\n" +
                "https://barkoder.com/");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}