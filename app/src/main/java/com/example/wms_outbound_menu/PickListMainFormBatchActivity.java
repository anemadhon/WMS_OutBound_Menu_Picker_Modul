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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PickListMainFormBatchActivity extends AppCompatActivity {

    TextView pickNumberForBatch;
    TextView pickDateForBatch;
    TextView cardCodeForBatch;
    TextView pickMemoForBatch;
    TextView itemCodeForBatch;
    TextView itemNameForBatch;
    TextView totalToPickForBatch;
    TextView remainQtyForBatch;
    TextView uomForBatch;
    TextView binLocForBatch;
    TextView qtyBinLocForBatch;
    TextView qtyAvlBatch;
    TextView expDateBatch;

    EditText editTextBatch;
    EditText editTextQty;
    EditText editTextUnLoadBin;

    List<SetItemDetailListModel> listDetailItemModel = new ArrayList<>();
    List<SetBatchListModel> listBatchModel = new ArrayList<>();

    Gson gson = new Gson();

    String arrDetails;
    String whsCode;
    String userLogin;
    String jsonDetails;

    JSONArray jsonArrDetails;
    JSONObject jsonObjArrDetails;

    Float remainQtyFinal;
    Float batchQtyFinal;

    Integer responseLength;
    Integer totalItemOnJSON;

    Float floatGrQty;
    Float floatBatchQty;
    Integer idxI;
    Integer idxJ;
    Integer statusDetail;
    Integer statusBatch;

    Intent intentFromBin;

    DBHelper DB;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_main_form_batch);

        intentFromBin = getIntent();
        whsCode = "WMSISTPR";//intentFromBin.getStringExtra("whsCode");
        userLogin = "WMS";//intentFromBin.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        pickNumberForBatch = (TextView) findViewById(R.id.pickNumberForBatch);
        pickDateForBatch = (TextView) findViewById(R.id.pickDateForBatch);
        cardCodeForBatch = (TextView) findViewById(R.id.cardCodeForBatch);
        pickMemoForBatch = (TextView) findViewById(R.id.pickMemoForBatch);
        itemCodeForBatch = (TextView) findViewById(R.id.itemCodeForBatch);
        itemNameForBatch = (TextView) findViewById(R.id.itemNameForBatch);
        totalToPickForBatch = (TextView) findViewById(R.id.totalToPickForBatch);
        remainQtyForBatch = (TextView) findViewById(R.id.remainQtyForBatch);
        uomForBatch = (TextView) findViewById(R.id.uomForBatch);
        binLocForBatch = (TextView) findViewById(R.id.binLocForBatch);
        qtyBinLocForBatch = (TextView) findViewById(R.id.qtyBinLocForBatch);
        qtyAvlBatch = (TextView) findViewById(R.id.qtyBatchPicker);
        expDateBatch = (TextView) findViewById(R.id.expDateBatchPicker);

        editTextBatch = (EditText) findViewById(R.id.batchPicker);
        editTextUnLoadBin = (EditText) findViewById(R.id.unLoadBin);

        if (intentFromBin.getExtras() != null) {
            if (intentFromBin.getStringExtra("arrDetails") != null) {
                arrDetails = intentFromBin.getStringExtra("arrDetails");
                Type listType = new TypeToken<List<SetItemDetailListModel>>(){}.getType();
                listDetailItemModel = gson.fromJson(arrDetails, listType);
                Log.d("tag gson.from", "gson.from: "+gson.toJson(listDetailItemModel));
            }
            String pickNmbr = intentFromBin.getStringExtra("pickNumberForBatch");
            final String itemCd = intentFromBin.getStringExtra("itemCodeForBatch");

            String url ="http://103.87.86.29:12950/api/getplitemsbyplnoitem/docnum="+pickNmbr+"&itemcode="+itemCd; //103.87.86.29:12950 //116.197.129.170:12950
            String urlBin ="http://103.87.86.29:12950/api/getplbinlist/whscode="+whsCode+"&itemcode="+itemCd; //103.87.86.29:12950 //116.197.129.170:12950

            pDialog = new ProgressDialog(PickListMainFormBatchActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response.length() == 0) {
                            totalToPickForBatch.setText("0");
                            remainQtyForBatch.setText("0");
                            Toast.makeText(PickListMainFormBatchActivity.this, "Data TIdak Ditemukan", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataPicker = response.getJSONObject(i);
                                if (arrDetails != null) {
                                    try {
                                        jsonArrDetails = new JSONArray(arrDetails);
                                        for (int j = 0; j < jsonArrDetails.length(); j++) {
                                            jsonObjArrDetails = jsonArrDetails.getJSONObject(j);
                                            if (jsonObjArrDetails.getString("materialNo").equals(itemCd)) {
                                                remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity")) - Float.parseFloat(jsonObjArrDetails.getString("grQuantity"));
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
                                totalToPickForBatch.setText(dataPicker.getString("openQty"));
                                remainQtyForBatch.setText(remainQtyFinal.toString());
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
                    totalToPickForBatch.setText("0");
                    remainQtyForBatch.setText("0");
                    Toast.makeText(PickListMainFormBatchActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                }
            });

            PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);

            JsonArrayRequest stringRequestBin = new JsonArrayRequest(Request.Method.GET, urlBin, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        if (response.length() == 0) {
                            qtyBinLocForBatch.setText("0");
                            Toast.makeText(PickListMainFormBatchActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dataBinLoc = response.getJSONObject(i);
                                qtyBinLocForBatch.setText(dataBinLoc.getString("avlQty"));
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
                    qtyBinLocForBatch.setText("0");
                    Toast.makeText(PickListMainFormBatchActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                }
            });

            PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequestBin);

            pickNumberForBatch.setText(pickNmbr);
            pickDateForBatch.setText(intentFromBin.getStringExtra("pickDateForBatch"));
            cardCodeForBatch.setText(intentFromBin.getStringExtra("cardCodeForBatch"));
            pickMemoForBatch.setText(intentFromBin.getStringExtra("pickMemoForBatch"));
            itemCodeForBatch.setText(itemCd);
            itemNameForBatch.setText(intentFromBin.getStringExtra("itemNameForBatch"));
            uomForBatch.setText(intentFromBin.getStringExtra("uomForBatch"));
            binLocForBatch.setText(intentFromBin.getStringExtra("binLocForBatch"));
        }

        TextWatcher setBatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String batchNo = editTextBatch.getText().toString().trim();
                if (batchNo.length() >= 4) {
                    final String itmCode = itemCodeForBatch.getText().toString();
                    final String binLc = binLocForBatch.getText().toString();
                    String url = "http://103.87.86.29:12950/api/getplbatchlistbybatch/itemcode="+itmCode+"&whscode="+whsCode+"&batchno="+batchNo+"&bincode="+binLc; //103.87.86.29:12950 //116.197.129.170:12950

                    qtyAvlBatch.setText("Loading...");
                    expDateBatch.setText("Loading...");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    final String formatted = df.format(new Date());

                    JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() == 0) {
                                    qtyAvlBatch.setText("0");
                                    expDateBatch.setText(formatted);
                                } else {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject dataPicker = response.getJSONObject(i);
                                        String expDateData[] = dataPicker.getString("expDate").split("T");
                                        if (arrDetails != null) {
                                            try {
                                                jsonArrDetails = new JSONArray(arrDetails);
                                                for (int j = 0; j < jsonArrDetails.length(); j++) {
                                                	jsonObjArrDetails = jsonArrDetails.getJSONObject(j);
                                                    JSONArray arrListBatches = new JSONArray(jsonObjArrDetails.getString("listBatches"));
                                                	if (jsonObjArrDetails.getString("materialNo").equals(itmCode) && jsonObjArrDetails.getString("num").equals(binLc)) {
                                                		for (int k = 0; k < arrListBatches.length(); k++) {
	                                                        JSONObject objListBatches = arrListBatches.getJSONObject(k);
	                                                        Float floatBatchQty = Float.parseFloat(objListBatches.getString("batchQuantity"));
	                                                        if (objListBatches.getString("batchNo").equals(batchNo)) {
	                                                            batchQtyFinal = Float.parseFloat(dataPicker.getString("avlQty")) - floatBatchQty;
	                                                            break;
	                                                        } else {
	                                                            batchQtyFinal = Float.parseFloat(dataPicker.getString("avlQty"));
	                                                        }
	                                                    }	
	                                                    break;
                                                	} else {
                                                        batchQtyFinal = Float.parseFloat(dataPicker.getString("avlQty"));
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            batchQtyFinal = Float.parseFloat(dataPicker.getString("avlQty"));
                                        }
                                        qtyAvlBatch.setText(batchQtyFinal.toString());
                                        expDateBatch.setText(expDateData[0]);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            qtyAvlBatch.setText("0");
                            expDateBatch.setText("YYYY-MM-DD HH:ii:ss");
                            Toast.makeText(PickListMainFormBatchActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                        }
                    });

                    PickerPickSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        editTextBatch.addTextChangedListener(setBatch);

        editTextBatch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editTextBatch.getText().toString().trim().length() > 3) {
                    qtyAvlBatch.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_to_main_form_picklist,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        editTextBatch = (EditText) findViewById(R.id.batchPicker);
        editTextQty = (EditText) findViewById(R.id.qtyPicker);
        editTextUnLoadBin = (EditText) findViewById(R.id.unLoadBin);

        pickNumberForBatch = (TextView) findViewById(R.id.pickNumberForBatch);
        pickDateForBatch = (TextView) findViewById(R.id.pickDateForBatch);
        cardCodeForBatch = (TextView) findViewById(R.id.cardCodeForBatch);
        pickMemoForBatch = (TextView) findViewById(R.id.pickMemoForBatch);
        itemCodeForBatch = (TextView) findViewById(R.id.itemCodeForBatch);
        binLocForBatch = (TextView) findViewById(R.id.binLocForBatch);
        uomForBatch = (TextView) findViewById(R.id.uomForBatch);
        remainQtyForBatch = (TextView) findViewById(R.id.remainQtyForBatch);
        qtyAvlBatch = (TextView) findViewById(R.id.qtyBatchPicker);
        expDateBatch = (TextView) findViewById(R.id.expDateBatchPicker);

        Float floatTotalToPick = Float.parseFloat(remainQtyForBatch.getText().toString());
        Float floatEditTextQty = editTextQty.getText().toString().equals("") ? Float.parseFloat("0") : Float.parseFloat(editTextQty.getText().toString());
        Float floatQtyAvlBatch = qtyAvlBatch.getText().toString().equals("") ? Float.parseFloat("0") : Float.parseFloat(qtyAvlBatch.getText().toString());

        String[] cardCodeWhs = cardCodeForBatch.getText().toString().split(" - ");

        if (id == R.id.btn_to_main_form_activity) {

            if (editTextBatch.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Batch Number Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (editTextQty.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (editTextUnLoadBin.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Unloading Bin Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (floatEditTextQty.floatValue() > floatTotalToPick.floatValue()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Lebih Besar dari Remaining Qty").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (floatEditTextQty.floatValue() > floatQtyAvlBatch.floatValue()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Lebih Besar dari Qty Avl Batch").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            setPostDataToModel(pickNumberForBatch.getText().toString(), pickDateForBatch.getText().toString(), cardCodeWhs[0], whsCode, pickMemoForBatch.getText().toString(), editTextBatch.getText().toString(), expDateBatch.getText().toString(), floatQtyAvlBatch, itemCodeForBatch.getText().toString(), uomForBatch.getText().toString(), binLocForBatch.getText().toString(), floatTotalToPick, floatEditTextQty, "", editTextUnLoadBin.getText().toString());
        }

        if (id == R.id.btn_to_finish) {

            if (editTextBatch.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Batch Number Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (editTextQty.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (editTextUnLoadBin.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Unloading Bin Tidak Boleh Kosong").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (floatEditTextQty.floatValue() > floatTotalToPick.floatValue()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Lebih Besar dari Remaining Qty").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            if (floatEditTextQty.floatValue() > floatQtyAvlBatch.floatValue()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Quantity Tidak Boleh Lebih Besar dari Qty Avl Batch").setTitle("Info");
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
            setPostDataToModel(pickNumberForBatch.getText().toString(), pickDateForBatch.getText().toString(), cardCodeWhs[0], whsCode, pickMemoForBatch.getText().toString(), editTextBatch.getText().toString(), expDateBatch.getText().toString(), floatQtyAvlBatch, itemCodeForBatch.getText().toString(), uomForBatch.getText().toString(), binLocForBatch.getText().toString(), floatTotalToPick, floatEditTextQty, "finish", editTextUnLoadBin.getText().toString());
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClickSearchBatch(View v) {

        itemCodeForBatch = (TextView) findViewById(R.id.itemCodeForBatch);
        binLocForBatch = (TextView) findViewById(R.id.binLocForBatch);

        Intent intentToBatchList = new Intent(PickListMainFormBatchActivity.this, PickListBatchActivity.class);
        intentToBatchList.putExtra("itemCodeForBatch",itemCodeForBatch.getText().toString());
        intentToBatchList.putExtra("binLocForBatch",binLocForBatch.getText().toString());
        intentToBatchList.putExtra("arrDetails",arrDetails);
        startActivityForResult(intentToBatchList,0);
    }

    public void btnClickSearchUnLoadBin(View v) {

        Intent intentToUnLoadBin = new Intent(PickListMainFormBatchActivity.this, PickListUnLoadBinActivity.class);
        startActivityForResult(intentToUnLoadBin,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        editTextBatch = (EditText) findViewById(R.id.batchPicker);
        editTextUnLoadBin = (EditText) findViewById(R.id.unLoadBin);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String batchNo = data.getStringExtra("batchNo");
                editTextBatch.setText(batchNo);
            }
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String unLoadBin = data.getStringExtra("unLoadBin");
                editTextUnLoadBin.setText(unLoadBin);
            }
        } 
    }

    protected void setPostDataToModel(final String pkNmbr, final String pkDt, final String crdCd, final String whCd, final String pkRmk, final String btchNmr, final String expDate, final Float qtyBatch, final String itmCd, final String uom, final String binLc, final float ttlToPick,  final Float qtyInput, String finish, final String unLdBn){
        editTextQty = (EditText) findViewById(R.id.qtyPicker);
        editTextBatch = (EditText) findViewById(R.id.batchPicker);
        editTextUnLoadBin = (EditText) findViewById(R.id.unLoadBin);

        remainQtyForBatch = (TextView) findViewById(R.id.remainQtyForBatch);

        String pickNum = pickNumberForBatch.getText().toString();
        String urlGetCountItem ="http://103.87.86.29:12950/api/getplitemsbyplno/docnum="+pickNum; //103.87.86.29:12950 //116.197.129.170:12950

        final Float floatTotalPick = Float.parseFloat(remainQtyForBatch.getText().toString());
        final Float floatEditQty = Float.parseFloat(editTextQty.getText().toString());

        if (intentFromBin.getStringExtra("arrDetails") != null) {
            Log.d("tag jsonArrDetail", "isi jsonArrDetail length: "+listDetailItemModel.size());
            Log.d("tag jsonArrDetail", "isi jsonArrDetail init: "+gson.toJson(listDetailItemModel));
            for (int i = 0; i < listDetailItemModel.size(); i++) {
                floatGrQty = listDetailItemModel.get(i).getGrQuantity();
                floatGrQty = listDetailItemModel.get(i).getGrQuantity();
                int arrBatch = listDetailItemModel.get(i).getListBatches().size();
                Log.d("tag length","length batch tiap index: "+arrBatch);
                if (listDetailItemModel.get(i).getMaterialNo().equals(itmCd) && listDetailItemModel.get(i).getNum().equals(binLc)) {
                    for (int j = 0; j < arrBatch; j++) {
                        floatBatchQty = listDetailItemModel.get(i).getListBatches().get(j).getBatchQuantity();
                        if (listDetailItemModel.get(i).getListBatches().get(j).getBatchNo().equals(editTextBatch.getText().toString())) {
                        	statusDetail = 1;
                        	statusBatch = 1;
                        	idxI = i;
                        	idxJ = j;
                            break;
                        } else {
                        	statusDetail = 1;
                            statusBatch = 0;
                        	idxI = i;
                        	idxJ = j+1;
                        }
                    }
                    break;
                } else {
                    statusDetail = 0;
                    statusBatch = 0;
                    idxI = i+1;
                }
            }
            if (statusDetail == 1) {
            	if (statusBatch == 1) {
            		listDetailItemModel.get(idxI).getListBatches().get(idxJ).setBatchQuantity((floatBatchQty + qtyInput));
                    listDetailItemModel.get(idxI).setGrQuantity((floatGrQty + qtyInput));
            	} else {
                    listDetailItemModel.get(idxI).getListBatches().add(idxJ, new SetBatchListModel(btchNmr, expDate, qtyInput));
                    listDetailItemModel.get(idxI).setGrQuantity((floatGrQty + qtyInput));
            	}
            } else {
            	listDetailItemModel.add(idxI, new SetItemDetailListModel(itmCd, uom, binLc, ttlToPick, qtyInput, Collections.singletonList(new SetBatchListModel(btchNmr, expDate, qtyInput))));
            }
            jsonDetails = gson.toJson(listDetailItemModel);

        } else {
            listBatchModel.add(0, new SetBatchListModel(btchNmr, expDate, qtyInput));
            listDetailItemModel.add(0, new SetItemDetailListModel(itmCd, uom, binLc, ttlToPick, qtyInput, listBatchModel));
            jsonDetails = gson.toJson(listDetailItemModel);
            Log.d("tag", "Hasil JSON untuk detail dan batch: "+jsonDetails);
        }

        if (finish.isEmpty()) {
            pDialog = new ProgressDialog(PickListMainFormBatchActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            totalItemOnJSON = 0;
            JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, urlGetCountItem, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    responseLength = response.length();
                    try {
                        for (int i = 0; i < responseLength; i++) {
                            JSONObject dataItem = response.getJSONObject(i);
                            for (int j = 0; j < listDetailItemModel.size(); j++) {
                                floatGrQty = listDetailItemModel.get(j).getGrQuantity();
                                Float floatTtlToPck = Float.parseFloat(dataItem.getString("openQty"));
                                if (dataItem.getString("itemCode").equals(listDetailItemModel.get(j).getMaterialNo()) || dataItem.getString("itemCode").equals(itmCd)) {
                                    if (floatTtlToPck.floatValue() == (listDetailItemModel.get(j).getGrQuantity() + qtyInput) || floatTtlToPck.floatValue() == qtyInput || floatTtlToPck.floatValue() == listDetailItemModel.get(j).getGrQuantity()) {
                                        totalItemOnJSON += 1;
                                    }
                                }
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

                    if (responseLength == 1) {
                        if (floatTotalPick.floatValue() == floatEditQty.floatValue()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PickListMainFormBatchActivity.this);
                            builder.setMessage("Semua Item Telah Selesai di Pick, Silahkan Selesaikan Transaksi dengan Menekan ikon -SAVE- disamping ikon ini. Terima Kasih").setTitle("Info");
                            AlertDialog alert = builder.create();
                            alert.show();
                            return;
                        }
                    }

                    Log.d("tag totalItemOnJSON", "Total totalItemOnJSON: "+totalItemOnJSON);

                    if ((totalItemOnJSON == responseLength) && (floatTotalPick.floatValue() == floatEditQty.floatValue())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PickListMainFormBatchActivity.this);
                        builder.setMessage("Semua Item Telah Selesai di Pick, Silahkan Selesaikan Transaksi dengan Menekan ikon -SAVE- disamping ikon ini. Terima Kasih").setTitle("Info");
                        AlertDialog alert = builder.create();
                        alert.show();
                        return;
                    } else {
                        Intent intent = new Intent(PickListMainFormBatchActivity.this, PickListMainFormActivity.class);
                        intent.putExtra("doNo", pkNmbr);
                        intent.putExtra("arrDetails", jsonDetails);
                        Log.d("tag", "jsonDetails to Activity Item: "+jsonDetails);
                        startActivity(intent);
                    }
                }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(PickListMainFormBatchActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
                    }
            });

            PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apakah Anda Yakin Ingin Menyelesaikan Transaksi Ini ?").setTitle("Info");
            builder.setCancelable(false);

            builder.setPositiveButton(
                    "Ya",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SetPostModel headerItemModel = new SetPostModel(pkNmbr, pkDt, crdCd, whCd, pkRmk, unLdBn, listDetailItemModel);
                            String json = gson.toJson(headerItemModel);
                            //checkConnection(json);
                            postingToServer(json);
                            postingToSQLite(json);
                            Log.d("tag", "headerItemModel: "+json);
                        }
                    });

            builder.setNegativeButton(
                    "Tidak",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert11 = builder.create();
            alert11.show();
        }
    }

    protected void checkConnection(final String json) {
        //check connection
        //if "online"
        // // postingToServer(json);
        //else
        // // postingToSQLite(json);
    }

    protected void postingToSQLite(final String json) {
        String jsonString = json;
        DB = new DBHelper(this);
        Boolean insertPicker = DB.insertPickerData(jsonString);
        if (insertPicker)
            Toast.makeText(PickListMainFormBatchActivity.this, "GOOD!! inserted into SQLite", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(PickListMainFormBatchActivity.this, "BAD!! no data inserted into SQLite", Toast.LENGTH_SHORT).show();
    }

    protected void postingToServer(final String json){

        pDialog = new ProgressDialog(PickListMainFormBatchActivity.this);
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
                        AlertDialog.Builder successInfo = new AlertDialog.Builder(PickListMainFormBatchActivity.this);
                        successInfo.setMessage("Berhasil Posting Data").setTitle("Info");
                        successInfo.setCancelable(false);

                        successInfo.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent toMain = new Intent(PickListMainFormBatchActivity.this, ChoosePickerNumberActivity.class);
                                        startActivity(toMain);
                                    }
                                }
                        );

                        AlertDialog successInfoDialog = successInfo.create();
                        successInfoDialog.show();
                    } else {
                        AlertDialog.Builder errorInfo = new AlertDialog.Builder(PickListMainFormBatchActivity.this);
                        errorInfo.setMessage("Error, Posting Data Gagal").setTitle("Info");
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
                Toast.makeText(PickListMainFormBatchActivity.this, "Error, Posting Data Gagal (Status Code:"+error.networkResponse.statusCode+")", Toast.LENGTH_SHORT).show();
                Log.d("tag", "response post error: "+error);
                Log.d("tag", "response post code: "+error.networkResponse.statusCode);
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
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