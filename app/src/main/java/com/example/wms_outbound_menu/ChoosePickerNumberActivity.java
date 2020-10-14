package com.example.wms_outbound_menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePickerNumberActivity extends AppCompatActivity {

    String whsCode;
    String userLogin;

    EditText editTextChoosePickNumber;

    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picker_number);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        checkDataInSQLite();

        editTextChoosePickNumber = (EditText) findViewById(R.id.choosePickNumber);

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        editTextChoosePickNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editTextChoosePickNumber.getText().toString().trim().length() > 0) {
                    Intent intent = new Intent(ChoosePickerNumberActivity.this, PickListMainFormActivity.class);

                    intent.putExtra("pkNmbr",editTextChoosePickNumber.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_after_choose_pick_number,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        editTextChoosePickNumber = (EditText) findViewById(R.id.choosePickNumber);

        if (id == R.id.btn_after_choose_picknumber) {

            if (editTextChoosePickNumber.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Picker Number Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent intent = new Intent(ChoosePickerNumberActivity.this, PickListMainFormActivity.class);

                intent.putExtra("pkNmbr",editTextChoosePickNumber.getText().toString());
                startActivity(intent);
            }

        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClickSearchPickNumber(View v) {
        Intent intent = new Intent(ChoosePickerNumberActivity.this, PickerPickListActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        editTextChoosePickNumber = (EditText) findViewById(R.id.choosePickNumber);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String pickNmbr = data.getStringExtra("pickNmbr");
                editTextChoosePickNumber.setText(pickNmbr);
            }
            if (resultCode == RESULT_CANCELED) {
                editTextChoosePickNumber.setText("");
            }
        } else {
            editTextChoosePickNumber.setText("");
        }
    }

    protected void checkDataInSQLite() {
        DB = new DBHelper(this);
        Cursor data = DB.getPickerData();

        if (data.getCount() > 0) {
            StringBuffer buffer = new StringBuffer();
            while (data.moveToNext()) {
                buffer.append(data.getString(0)+",");
            }
            Log.d("tag", "JSON String dari SQLite: "+buffer);

            final AlertDialog.Builder info = new AlertDialog.Builder(ChoosePickerNumberActivity.this);
            info.setMessage("Data Offline ditemukan").setTitle("Info");
            info.setCancelable(false);

            info.setPositiveButton(
                    "Sinkronisasi",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //posting to online server
                            DB = new DBHelper(ChoosePickerNumberActivity.this);
                            Boolean deletePicker = DB.deletePickerData();

                            if (deletePicker) {
                                Toast.makeText(ChoosePickerNumberActivity.this, "Sinkronisasi Sukses", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                                Log.d("tag", "Status Delete: " + deletePicker);
                            } else {
                                Log.d("tag", "Status Delete: " + deletePicker);
                            }
                        }
                    }
            );

            final AlertDialog alert = info.create();
            alert.show();

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
        }
    }
}