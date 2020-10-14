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

public class PickListBinActivity extends AppCompatActivity {

    String whsCode;
    String userLogin;

    ListView listViewBinLoc;

    List<PickListBinModel> pickListBinLoc = new ArrayList<>();

    PickListBinActivity.PickListBinLocAdapter pickListBinLocAdapter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_bin);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");
        String itmCd = intent.getStringExtra("itemCode");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        listViewBinLoc = findViewById(R.id.listViewBinLoc);

        String url ="http://103.87.86.29:12950/api/getplbinlist/whscode="+whsCode+"&itemcode="+itmCd; //103.87.86.29:12950 //116.197.129.170:12950

        pDialog = new ProgressDialog(PickListBinActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() == 0) {
                        PickListBinModel listBinLoc = new PickListBinModel("DEFAULT DATA", "0.0");
                        pickListBinLoc.add(listBinLoc);
                        Toast.makeText(PickListBinActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dataBinLoc = response.getJSONObject(i);
                            PickListBinModel listBinLoc = new PickListBinModel(dataBinLoc.getString("binCode"), dataBinLoc.getString("avlQty"));
                            pickListBinLoc.add(listBinLoc);
                        }
                    }

                    pickListBinLocAdapter = new PickListBinActivity.PickListBinLocAdapter(pickListBinLoc, getApplicationContext());

                    listViewBinLoc.setAdapter(pickListBinLocAdapter);

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
                PickListBinModel listBinLoc = new PickListBinModel("DEFAULT DATA", "0.0");
                pickListBinLoc.add(listBinLoc);

                pickListBinLocAdapter = new PickListBinActivity.PickListBinLocAdapter(pickListBinLoc, getApplicationContext());

                listViewBinLoc.setAdapter(pickListBinLocAdapter);

                Toast.makeText(PickListBinActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
            }
        });

        PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_pick_list_bin_loc,menu);

        MenuItem menuItemBinLoc = menu.findItem(R.id.search_view_pick_list_bin_loc);

        SearchView searchView = (SearchView) menuItemBinLoc.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pickListBinLocAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view_pick_list_bin_loc) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PickListBinLocAdapter extends BaseAdapter implements Filterable {

        private List<PickListBinModel> pickListBinModelList;
        private List<PickListBinModel> pickListBinModelListFiltered;
        private Context context;

        public PickListBinLocAdapter(List<PickListBinModel> PickListBinModelList, Context context) {
            this.pickListBinModelList = PickListBinModelList;
            this.pickListBinModelListFiltered = PickListBinModelList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return pickListBinModelListFiltered.size();
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
            View listBinLocView = getLayoutInflater().inflate(R.layout.pick_list_bin_loc_view,null);

            TextView binLoc = listBinLocView.findViewById(R.id.binLoc);
            TextView qtyBinLoc = listBinLocView.findViewById(R.id.qtyBinLoc);

            binLoc.setText(pickListBinModelListFiltered.get(position).getBinLoc());
            qtyBinLoc.setText(pickListBinModelListFiltered.get(position).getQtyBinLoc());

            listBinLocView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPickListBinLoc = new Intent();
                    intentPickListBinLoc.putExtra("binLoc", pickListBinModelListFiltered.get(position).getBinLoc());

                    setResult(RESULT_OK, intentPickListBinLoc);
                    finish();
                }
            });

            return listBinLocView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = pickListBinModelList.size();
                        filterResults.values = pickListBinModelList;
                    } else {
                        String keyWord = constraint.toString().toUpperCase();
                        List<PickListBinModel> result = new ArrayList<>();

                        for (PickListBinModel PickListBinModel: pickListBinModelList) {
                            if (PickListBinModel.getBinLoc().contains(keyWord)) {
                                result.add(PickListBinModel);
                            }

                            filterResults.count = result.size();
                            filterResults.values = result;

                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    pickListBinModelListFiltered = (List<PickListBinModel>) results.values;

                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}