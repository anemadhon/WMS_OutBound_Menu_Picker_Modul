package com.example.wms_outbound_menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClickPickerPickList(View v) {
        Intent intent = new Intent(MainActivity.this, ChoosePickerNumberActivity.class);
        startActivity(intent);
    }

    public void btnClickInventoryDispatch(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Menuju Activity Inventory Dispatch").setTitle("Info");
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void btnClickReturnToVendor(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Menuju Activity Return to Vendor").setTitle("Info");
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void btnClickBackToWhs(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Kembali Ke Activity Whs").setTitle("Info");
        AlertDialog alert = builder.create();
        alert.show();
    }
}