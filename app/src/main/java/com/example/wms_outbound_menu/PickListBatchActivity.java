package com.example.wms_outbound_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PickListBatchActivity extends AppCompatActivity {

    String arrDetails;
    String whsCode;
    String userLogin;
    String itemCode;
    String binLocBatch;

    JSONArray jsonArrDetails;
    JSONObject jsonObjArrDetails;

    Float batchQtyFinal;

    ListView listViewBatch;

    List<PickListBatchModel> pickListBatch = new ArrayList<>();

    PickListBatchActivity.PickListBatchAdapter pickListBatchAdapter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_batch);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");
        binLocBatch = intent.getStringExtra("binLocForBatch");
        itemCode = intent.getStringExtra("itemCodeForBatch");

        if (intent.getStringExtra("arrDetails") != null) {
            arrDetails = intent.getStringExtra("arrDetails");
        }

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        String url = "http://103.87.86.29:12950/api/getplbatchlistbyitmwhs/itemcode="+itemCode+"&whscode="+whsCode+"&bincode="+binLocBatch; //103.87.86.29:12950 //116.197.129.170:12950

        listViewBatch = findViewById(R.id.listViewBatch);

        pDialog = new ProgressDialog(PickListBatchActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() == 0) {
                        PickListBatchModel listBatch = new PickListBatchModel("DEFAULT DATA","0","YYYY-MM-DD HH:ii:ss");
                        pickListBatch.add(listBatch);
                        Toast.makeText(PickListBatchActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dataBatch = response.getJSONObject(i);
                            if (arrDetails != null) {
                                try {
                                    jsonArrDetails = new JSONArray(arrDetails);
                                    for (int j = 0; j < jsonArrDetails.length(); j++) {
                                        jsonObjArrDetails = jsonArrDetails.getJSONObject(j);
                                        JSONArray arrListBatches = new JSONArray(jsonObjArrDetails.getString("listBatches"));
                                        if (jsonObjArrDetails.getString("materialNo").equals(itemCode) && jsonObjArrDetails.getString("num").equals(binLocBatch)) {
                                            for (int k = 0; k < arrListBatches.length(); k++) {
                                                JSONObject objListBatches = arrListBatches.getJSONObject(k);
                                                Float floatBatchQty = Float.parseFloat(objListBatches.getString("batchQuantity"));
                                                if (objListBatches.getString("batchNo").equals(dataBatch.getString("batchNo"))) {
                                                    batchQtyFinal = Float.parseFloat(dataBatch.getString("avlQty")) - floatBatchQty;
                                                    break;
                                                } else {
	                                                batchQtyFinal = Float.parseFloat(dataBatch.getString("avlQty"));
	                                            }
                                            }
                                            break;
                                        } else {
                                            batchQtyFinal = Float.parseFloat(dataBatch.getString("avlQty"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                batchQtyFinal = Float.parseFloat(dataBatch.getString("avlQty"));
                            }
                            PickListBatchModel listBatch = new PickListBatchModel(dataBatch.getString("batchNo"),batchQtyFinal.toString(),dataBatch.getString("expDate"));
                            pickListBatch.add(listBatch);
                        }
                    }

                    pickListBatchAdapter = new PickListBatchActivity.PickListBatchAdapter(pickListBatch, getApplicationContext());

                    listViewBatch.setAdapter(pickListBatchAdapter);

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
                PickListBatchModel listBatch = new PickListBatchModel("DEFAULT DATA","0","YYYY-MM-DD HH:ii:ss");
                pickListBatch.add(listBatch);

                pickListBatchAdapter = new PickListBatchActivity.PickListBatchAdapter(pickListBatch, getApplicationContext());

                listViewBatch.setAdapter(pickListBatchAdapter);

                Toast.makeText(PickListBatchActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
            }
        });

        PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_pick_list_batch,menu);

        MenuItem menuItemBatch = menu.findItem(R.id.search_view_pick_list_batch);

        SearchView searchView = (SearchView) menuItemBatch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pickListBatchAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view_pick_list_batch) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PickListBatchAdapter extends BaseAdapter implements Filterable {

        private List<PickListBatchModel> pickListBatchModelList;
        private List<PickListBatchModel> pickListBatchModelListFiltered;
        private Context context;

        public PickListBatchAdapter(List<PickListBatchModel> PickListBatchModelList, Context context) {
            this.pickListBatchModelList = PickListBatchModelList;
            this.pickListBatchModelListFiltered = PickListBatchModelList;
            this.context = context;
        }


        @Override
        public int getCount() {
            return pickListBatchModelListFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View listBatchView = getLayoutInflater().inflate(R.layout.pick_list_batch_view,null);

            final TextView batch = listBatchView.findViewById(R.id.batch);
            final TextView qtyBatch = listBatchView.findViewById(R.id.qtyBatch);
            TextView expDateBatch = listBatchView.findViewById(R.id.expDateBatch);

            String expDateData[] = pickListBatchModelListFiltered.get(position).getExpDate().split("T");

            batch.setText(pickListBatchModelListFiltered.get(position).getBatchNo());
            qtyBatch.setText(pickListBatchModelListFiltered.get(position).getAvlQty());
            expDateBatch.setText(expDateData[0]);

            listBatchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Float floatQtyBatch = Float.parseFloat(qtyBatch.getText().toString());
                    if (floatQtyBatch.floatValue() == 0) {
                        Toast.makeText(PickListBatchActivity.this, "Avl Batch Penuh atau 0, Silahkan Pilih Batch Yang Lain", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intentPickListBatch = new Intent();
                        intentPickListBatch.putExtra("batchNo", pickListBatchModelListFiltered.get(position).getBatchNo());

                        setResult(RESULT_OK, intentPickListBatch);
                        finish();
                    }
                }
            });

            return listBatchView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = pickListBatchModelList.size();
                        filterResults.values = pickListBatchModelList;
                    } else {
                        String keyWord = constraint.toString().toUpperCase();
                        List<PickListBatchModel> result = new ArrayList<>();

                        for (PickListBatchModel PickListBatchModel: pickListBatchModelList) {
                            if (PickListBatchModel.getBatchNo().contains(keyWord)) {
                                result.add(PickListBatchModel);
                            }

                            filterResults.count = result.size();
                            filterResults.values = result;

                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    pickListBatchModelListFiltered = (List<PickListBatchModel>) results.values;

                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}