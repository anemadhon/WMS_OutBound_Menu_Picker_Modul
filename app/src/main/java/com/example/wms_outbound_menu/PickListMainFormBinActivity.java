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

public class PickListMainFormBinActivity extends AppCompatActivity {

    TextView pickNumberForBin;
    TextView pickDateForBin;
    TextView cardCodeForBin;
    TextView pickMemoForBin;
    TextView itemCodeForBin;
    TextView itemNameForBin;
    TextView totalToPickForBin;
    TextView remainQtyForBin;
    TextView uomForBin;

    EditText editTextBinLoc;

    String arrDetails;
    String whsCode;
    String userLogin;

    JSONArray jsonArrDetails;
    JSONObject jsonObjArrDetails;

    Float remainQtyFinal;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_main_form_bin);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        pickNumberForBin = (TextView) findViewById(R.id.pickNumberForBinLoc);
        pickDateForBin = (TextView) findViewById(R.id.pickDateForBinLoc);
        cardCodeForBin = (TextView) findViewById(R.id.cardCodeForBinLoc);
        pickMemoForBin = (TextView) findViewById(R.id.pickMemoForBinLoc);
        itemCodeForBin = (TextView) findViewById(R.id.itemCodeForBinLoc);
        itemNameForBin = (TextView) findViewById(R.id.itemNameForBinLoc);
        totalToPickForBin = (TextView) findViewById(R.id.totalToPickForBinLoc);
        remainQtyForBin = (TextView) findViewById(R.id.remainQtyForBinLoc);
        uomForBin = (TextView) findViewById(R.id.uomForBinLoc);

        editTextBinLoc = (EditText) findViewById(R.id.binLocPicker);

        if (intent.getExtras() != null) {
            if (intent.getStringExtra("arrDetails") != null) {
                arrDetails = intent.getStringExtra("arrDetails");
            }
            String pickNmbr = intent.getStringExtra("pickNumberForBin");
            String itemCd = intent.getStringExtra("itemCodeForBin");

            String url ="http://103.87.86.29:12950/api/getplitemsbyplnoitem/docnum="+pickNmbr+"&itemcode="+itemCd; //103.87.86.29:12950 //116.197.129.170:12950

            pDialog = new ProgressDialog(PickListMainFormBinActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            pickNumberForBin.setText(pickNmbr);
            pickDateForBin.setText(intent.getStringExtra("pickDateForBin"));
            cardCodeForBin.setText(intent.getStringExtra("cardCodeForBin"));
            pickMemoForBin.setText(intent.getStringExtra("pickMemoForBin"));
            itemCodeForBin.setText(itemCd);

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response.length() == 0) {
                            itemNameForBin.setText("DEFAULT DATA");
                            totalToPickForBin.setText("0");
                            remainQtyForBin.setText("0");
                            uomForBin.setText("DEFAULT DATA");
                            Toast.makeText(PickListMainFormBinActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataPicker = response.getJSONObject(i);
                                if (arrDetails != null) {
                                    try {
                                        jsonArrDetails = new JSONArray(arrDetails);
                                        for (int j = 0; j < jsonArrDetails.length(); j++) {
                                            jsonObjArrDetails = jsonArrDetails.getJSONObject(j);
                                            if (jsonObjArrDetails.getString("materialNo").equals(dataPicker.getString("itemCode"))) {
                                                if (remainQtyFinal != null) {
                                                    remainQtyFinal -= Float.parseFloat(jsonObjArrDetails.getString("grQuantity"));
                                                } else {
                                                    remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity")) - Float.parseFloat(jsonObjArrDetails.getString("grQuantity"));
                                                }
                                                break;
                                            } else {
                                                remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity"));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity"));
                                }
                                itemNameForBin.setText(dataPicker.getString("dscription"));
                                totalToPickForBin.setText(dataPicker.getString("openQty"));
                                remainQtyForBin.setText(remainQtyFinal.toString());
                                uomForBin.setText(dataPicker.getString("unitMsr"));
                            }
                        }

                        if (pDialog.isShowing()) {
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    pDialog.dismiss();
                                }}, 300);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    itemNameForBin.setText("DEFAULT DATA");
                    totalToPickForBin.setText("0");
                    remainQtyForBin.setText("0");
                    uomForBin.setText("DEFAULT DATA");
                    Toast.makeText(PickListMainFormBinActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                }
            });

            PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

        editTextBinLoc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editTextBinLoc.getText().toString().trim().length() > 0) {
                    Intent intentToBatch = new Intent(PickListMainFormBinActivity.this, PickListMainFormBatchActivity.class);
                    intentToBatch.putExtra("pickNumberForBatch",pickNumberForBin.getText().toString());
                    intentToBatch.putExtra("pickDateForBatch",pickDateForBin.getText().toString());
                    intentToBatch.putExtra("cardCodeForBatch",cardCodeForBin.getText().toString());
                    intentToBatch.putExtra("pickMemoForBatch",pickMemoForBin.getText().toString());
                    intentToBatch.putExtra("itemCodeForBatch",itemCodeForBin.getText().toString());
                    intentToBatch.putExtra("itemNameForBatch",itemNameForBin.getText().toString());
                    intentToBatch.putExtra("totalToPickForBatch",totalToPickForBin.getText().toString());
                    intentToBatch.putExtra("remainQtyForBatch",remainQtyForBin.getText().toString());
                    intentToBatch.putExtra("uomForBatch",uomForBin.getText().toString());
                    intentToBatch.putExtra("binLocForBatch",editTextBinLoc.getText().toString());
                    intentToBatch.putExtra("arrDetails",arrDetails);
                    startActivity(intentToBatch);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_to_batch_picklist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        editTextBinLoc = (EditText) findViewById(R.id.binLocPicker);
        itemCodeForBin = (TextView) findViewById(R.id.itemCodeForBinLoc);

        if (id == R.id.btn_to_batch_activity) {

            if (editTextBinLoc.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Bin Location Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Intent intentToBatch = new Intent(PickListMainFormBinActivity.this, PickListMainFormBatchActivity.class);
                intentToBatch.putExtra("pickNumberForBatch",pickNumberForBin.getText().toString());
                intentToBatch.putExtra("pickDateForBatch",pickDateForBin.getText().toString());
                intentToBatch.putExtra("cardCodeForBatch",cardCodeForBin.getText().toString());
                intentToBatch.putExtra("pickMemoForBatch",pickMemoForBin.getText().toString());
                intentToBatch.putExtra("itemCodeForBatch",itemCodeForBin.getText().toString());
                intentToBatch.putExtra("itemNameForBatch",itemNameForBin.getText().toString());
                intentToBatch.putExtra("totalToPickForBatch",totalToPickForBin.getText().toString());
                intentToBatch.putExtra("remainQtyForBatch",remainQtyForBin.getText().toString());
                intentToBatch.putExtra("uomForBatch",uomForBin.getText().toString());
                intentToBatch.putExtra("binLocForBatch",editTextBinLoc.getText().toString());
                intentToBatch.putExtra("arrDetails",arrDetails);
                startActivity(intentToBatch);
            }
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClickSearchBinLoc(View v) {
        itemCodeForBin = (TextView) findViewById(R.id.itemCodeForBinLoc);
        Intent intentBinLoc = new Intent(PickListMainFormBinActivity.this, PickListBinActivity.class);
        intentBinLoc.putExtra("itemCode", itemCodeForBin.getText().toString());
        startActivityForResult(intentBinLoc,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        editTextBinLoc = (EditText) findViewById(R.id.binLocPicker);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String binLoc = data.getStringExtra("binLoc");
                editTextBinLoc.setText(binLoc);
            }
            if (resultCode == RESULT_CANCELED) {
                editTextBinLoc.setText("");
            }
        } else {
            editTextBinLoc.setText("");
        }
    }
}