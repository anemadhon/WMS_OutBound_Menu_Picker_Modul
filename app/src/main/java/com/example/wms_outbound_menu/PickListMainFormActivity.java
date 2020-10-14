package com.example.wms_outbound_menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PickListMainFormActivity extends AppCompatActivity {

    TextView pickNumber;
    TextView pickDate;
    TextView pickMemo;
    TextView pickCardCode;

    EditText editTextItemCode;

    String arrDetails;
    String whsCode;
    String userLogin;
    String currentDate;

    Intent intent;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_main_form);

        intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        pickNumber = (TextView) findViewById(R.id.pickNumber);
        pickDate = (TextView) findViewById(R.id.pickDate);
        pickMemo = (TextView) findViewById(R.id.pickMemo);
        pickCardCode =  (TextView) findViewById(R.id.pickCardCode);

        editTextItemCode = (EditText) findViewById(R.id.itemBarcodePicker);

        Date dtNow = new Date();
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(dtNow);

        if (intent.getStringExtra("pkNmbr") != null || (intent.getStringExtra("doNo") != null && intent.getStringExtra("arrDetails") != null)) {
            String doNo = intent.getStringExtra("doNo") == null ? intent.getStringExtra("pkNmbr") : intent.getStringExtra("doNo");
            arrDetails = intent.getStringExtra("arrDetails");

            String urlHeader ="http://103.87.86.29:12950/api/getplbydocnum/docnum="+doNo; //103.87.86.29:12950 //116.197.129.170:12950

            if (intent.getStringExtra("pkNmbr") != null) {
                pDialog = new ProgressDialog(PickListMainFormActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, urlHeader, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response.length() == 0) {
                            pickNumber.setText("DEFAULT DATA");
                            pickDate.setText("YYYY-MM-DD HH:ii:ss");
                            pickCardCode.setText("DEFAULT DATA");
                            pickMemo.setText("DEFAULT DATA");
                            Toast.makeText(PickListMainFormActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataPicker = response.getJSONObject(i);
                                String postingDate[] = dataPicker.getString("docDate").split("T");
                                pickNumber.setText(dataPicker.getString("uDocNum"));
                                pickDate.setText(postingDate[0]);
                                pickCardCode.setText(dataPicker.getString("cardCode")+" - "+dataPicker.getString("cardName"));
                                pickMemo.setText(dataPicker.getString("pickRemark"));
                            }
                        }

                        if (intent.getStringExtra("pkNmbr") != null) {
                            if (pDialog.isShowing()) {
                                new android.os.Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        pDialog.dismiss();
                                    }}, 300);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    pickNumber.setText("DEFAULT DATA");
                    pickDate.setText("YYYY-MM-DD HH:ii:ss");
                    pickCardCode.setText("DEFAULT DATA");
                    pickMemo.setText("DEFAULT DATA");
                    Toast.makeText(PickListMainFormActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                }
            });

            PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

        editTextItemCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editTextItemCode.getText().toString().trim().length() > 0) {
                    Intent intent = new Intent(PickListMainFormActivity.this, PickListMainFormBinActivity.class);

                    intent.putExtra("pickNumberForBin",pickNumber.getText().toString());
                    intent.putExtra("pickDateForBin",pickDate.getText().toString());
                    intent.putExtra("cardCodeForBin",pickCardCode.getText().toString());
                    intent.putExtra("pickMemoForBin",pickMemo.getText().toString());
                    intent.putExtra("itemCodeForBin",editTextItemCode.getText().toString());
                    intent.putExtra("arrDetails",arrDetails);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_to_bin_picklist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        pickNumber = (TextView) findViewById(R.id.pickNumber);
        pickDate = (TextView) findViewById(R.id.pickDate);
        pickMemo = (TextView) findViewById(R.id.pickMemo);
        pickCardCode =  (TextView) findViewById(R.id.pickCardCode);

        editTextItemCode = (EditText) findViewById(R.id.itemBarcodePicker);

        if (id == R.id.btn_to_bin_activity) {

            if (editTextItemCode.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Item Code Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent intent = new Intent(PickListMainFormActivity.this, PickListMainFormBinActivity.class);

                intent.putExtra("pickNumberForBin",pickNumber.getText().toString());
                intent.putExtra("pickDateForBin",pickDate.getText().toString());
                intent.putExtra("cardCodeForBin",pickCardCode.getText().toString());
                intent.putExtra("pickMemoForBin",pickMemo.getText().toString());
                intent.putExtra("itemCodeForBin",editTextItemCode.getText().toString());
                intent.putExtra("arrDetails",arrDetails);
                startActivity(intent);
            }
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClickSearchItem(View v) {
        pickNumber = (TextView) findViewById(R.id.pickNumber);
        Intent intentPickListItem = new Intent(PickListMainFormActivity.this, PickListItemActivity.class);
        intentPickListItem.putExtra("pickNumber",pickNumber.getText().toString());
        intentPickListItem.putExtra("arrDetails",arrDetails);
        startActivityForResult(intentPickListItem,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        editTextItemCode = (EditText) findViewById(R.id.itemBarcodePicker);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String itemCode = data.getStringExtra("itemCode");
                editTextItemCode.setText(itemCode);
            }
            if (resultCode == RESULT_CANCELED) {
                editTextItemCode.setText("");
            }
        } else {
            editTextItemCode.setText("");
        }
    }
}