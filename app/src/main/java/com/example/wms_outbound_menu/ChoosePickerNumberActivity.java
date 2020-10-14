package com.example.wms_outbound_menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ChoosePickerNumberActivity extends AppCompatActivity {

    String whsCode;
    String userLogin;

    EditText editTextChoosePickNumber;

    DBHelper DB;

    ProgressDialog pDialog;

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
        final Cursor data = DB.getPickerData();
        String stringJson = "";

        if (data.getCount() > 0) {
            if (data.getCount() == 1) {
                while (data.moveToNext()) {
                    stringJson = data.getString(0);
                }
            }
            final StringBuffer buffer = new StringBuffer();
            while (data.moveToNext()) {
                buffer.append(data.getString(0)+",");
            }
            Log.d("tag", "JSON String dari SQLite: "+buffer);

            final AlertDialog.Builder info = new AlertDialog.Builder(ChoosePickerNumberActivity.this);
            info.setMessage(data.getCount()+" Data Offline ditemukan").setTitle("Info");
            info.setCancelable(false);

            final String finalStringJson = stringJson;
            info.setPositiveButton(
                    "Sinkronisasi Sekarang",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            postingToServer(data.getCount() >= 2 ? buffer.toString() : finalStringJson);
                            dialogInterface.dismiss();
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

    protected void postingToServer(final String json){

        pDialog = new ProgressDialog(ChoosePickerNumberActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        String urlPost = "http://103.87.86.29:12950/tgrpo/tgrpo/api/listgrpodlvs"; //103.87.86.29:12950 //116.197.129.170:12950

        StringRequest strReq = new StringRequest(Request.Method.POST, urlPost, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject statusMsg = new JSONObject(response);
                    if (statusMsg.getString("status").toString().equals("1")) {
                        DB = new DBHelper(ChoosePickerNumberActivity.this);
                        Boolean deletePicker = DB.deletePickerData();

                        if (deletePicker) {
                            Toast.makeText(ChoosePickerNumberActivity.this, "Sinkronisasi Sukses", Toast.LENGTH_SHORT).show();
                            Log.d("tag", "Status Delete: " + deletePicker);
                        } else {
                            Log.d("tag", "Status Delete: " + deletePicker);
                        }
                    } else {
                        AlertDialog.Builder errorInfo = new AlertDialog.Builder(ChoosePickerNumberActivity.this);
                        errorInfo.setMessage("Error, Sinkronisasi Data Gagal").setTitle("Info");
                        errorInfo.setCancelable(true);
                        AlertDialog errorInfoDialog = errorInfo.create();
                        errorInfoDialog.show();
                    }
                    Log.d("tag", "response post: "+statusMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pDialog.isShowing()) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        public void run() {
                            pDialog.dismiss();
                        }}, 300);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder errorInfo = new AlertDialog.Builder(ChoosePickerNumberActivity.this);
                errorInfo.setMessage("Error, Sinkronisasi Data Gagal (Status Code:"+error.networkResponse.statusCode+")").setTitle("Error");
                errorInfo.setCancelable(false);

                errorInfo.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent toMain = new Intent(ChoosePickerNumberActivity.this, MainActivity.class);
                                startActivity(toMain);
                            }
                        }
                );

                AlertDialog errorInfoDialog = errorInfo.create();
                errorInfoDialog.show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8;";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return json == null ? null : json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        PickerPickSingleton.getInstance(this).addToRequestQueue(strReq);
    }
}