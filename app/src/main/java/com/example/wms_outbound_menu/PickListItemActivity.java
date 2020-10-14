package com.example.wms_outbound_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class PickListItemActivity extends AppCompatActivity {

    ListView listViewItem;

    List<PickListitemModel> pickListItem = new ArrayList<>();

    PickListItemActivity.PickListItemAdapter pickListItemAdapter;

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
        setContentView(R.layout.activity_pick_list_item);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        String pickNumber = intent.getStringExtra("pickNumber");
        if (intent.getStringExtra("arrDetails") != null) {
            arrDetails = intent.getStringExtra("arrDetails");
        }

        listViewItem = findViewById(R.id.listViewItem);

        String url ="http://103.87.86.29:12950/api/getplitemsbyplno/docnum="+pickNumber; //103.87.86.29:12950 //116.197.129.170:12950

        pDialog = new ProgressDialog(PickListItemActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() == 0) {
                        PickListitemModel listPicker = new PickListitemModel("DEFAULT DATA","DEFAULT DATA","0","0","DEFAULT DATA");
                        pickListItem.add(listPicker);
                        Toast.makeText(PickListItemActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dataPicker = response.getJSONObject(i);
                            if (arrDetails != null) {
                                try {
                                    jsonArrDetails = new JSONArray(arrDetails);
                                    for (int j = 0; j < jsonArrDetails.length(); j++) {
                                        jsonObjArrDetails = jsonArrDetails.getJSONObject(j);
                                        if (jsonObjArrDetails.getString("materialNo").equals(dataPicker.getString("itemCode"))) {
                                            remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity")) - Float.parseFloat(jsonObjArrDetails.getString("grQuantity"));
                                            break;
                                        } else {
                                            remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity"));
                                        }
                                        Log.d("1.","JSON Arr: "+jsonArrDetails);
                                        Log.d("2.","JSON Arr Length: "+jsonArrDetails.length());
                                        Log.d("3.","JSON Arr Length: "+jsonObjArrDetails);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                remainQtyFinal = Float.parseFloat(dataPicker.getString("quantity"));
                            }
                            PickListitemModel listPicker = new PickListitemModel(dataPicker.getString("itemCode"),dataPicker.getString("dscription"),dataPicker.getString("openQty"),remainQtyFinal.toString(),dataPicker.getString("unitMsr"));
                            pickListItem.add(listPicker);
                        }
                    }

                    pickListItemAdapter = new PickListItemActivity.PickListItemAdapter(pickListItem, getApplicationContext());

                    listViewItem.setAdapter(pickListItemAdapter);

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
                PickListitemModel listPicker = new PickListitemModel("DEFAULT DATA","DEFAULT DATA","0","0","DEFAULT DATA");
                pickListItem.add(listPicker);

                pickListItemAdapter = new PickListItemActivity.PickListItemAdapter(pickListItem, getApplicationContext());

                listViewItem.setAdapter(pickListItemAdapter);

                Toast.makeText(PickListItemActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
            }
        });

        PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_pick_list_item,menu);

        MenuItem menuItem = menu.findItem(R.id.search_view_pick_list_item);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pickListItemAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view_pick_list_item) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PickListItemAdapter extends BaseAdapter implements Filterable {

        private List<PickListitemModel> pickListItemModelList;
        private List<PickListitemModel> pickListItemModelListFiltered;
        private Context context;

        public PickListItemAdapter(List<PickListitemModel> pickListItemModelList, Context context) {
            this.pickListItemModelList = pickListItemModelList;
            this.pickListItemModelListFiltered = pickListItemModelList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return pickListItemModelListFiltered.size();
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
            View listItemView = getLayoutInflater().inflate(R.layout.pick_list_item_view,null);

            TextView itemCode = listItemView.findViewById(R.id.itemCode);
            TextView itemName = listItemView.findViewById(R.id.itemName);
            TextView totalToPick = listItemView.findViewById(R.id.totalToPick);
            TextView remainQty = listItemView.findViewById(R.id.remainQty);
            TextView uom = listItemView.findViewById(R.id.uom);

            itemCode.setText(pickListItemModelListFiltered.get(position).getItemCode());
            itemName.setText(pickListItemModelListFiltered.get(position).getItemName());
            totalToPick.setText(pickListItemModelListFiltered.get(position).getTotalToPick());
            remainQty.setText(pickListItemModelListFiltered.get(position).getRemainQty().toString());
            uom.setText(pickListItemModelListFiltered.get(position).getUom());

            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pickListItemModelListFiltered.get(position).getRemainQty().equals("0.0")) {
                        AlertDialog.Builder doneInfo = new AlertDialog.Builder(PickListItemActivity.this);
                        doneInfo.setMessage("Pengambilan untuk Item ini Telah Selesai").setTitle("Info");
                        doneInfo.setCancelable(true);
                        AlertDialog doneInfoDialog = doneInfo.create();
                        doneInfoDialog.show();
                    } else {
                        Intent intentPickListItem = new Intent();
                        intentPickListItem.putExtra("itemCode", pickListItemModelListFiltered.get(position).getItemCode());

                        setResult(RESULT_OK, intentPickListItem);
                        finish();
                    }
                }
            });

            return listItemView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = pickListItemModelList.size();
                        filterResults.values = pickListItemModelList;
                    } else {
                        String keyWord = constraint.toString().toUpperCase();
                        List<PickListitemModel> result = new ArrayList<>();

                        for (PickListitemModel pickListItemModel: pickListItemModelList) {
                            if (pickListItemModel.getItemCode().contains(keyWord)) {
                                result.add(pickListItemModel);
                            }

                            filterResults.count = result.size();
                            filterResults.values = result;

                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    pickListItemModelListFiltered = (List<PickListitemModel>) results.values;

                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}