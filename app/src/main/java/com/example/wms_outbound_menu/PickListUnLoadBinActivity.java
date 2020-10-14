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

public class PickListUnLoadBinActivity extends AppCompatActivity {

    String whsCode;
    String userLogin;

    ListView listViewUnLoadBin;

    List<PickListUnLoadBinModel> pickListUnLoadBin = new ArrayList<>();

    PickListUnLoadBinActivity.PickListUnLoadBinAdapter pickListUnLoadBinAdapter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list_un_load_bin);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        listViewUnLoadBin = findViewById(R.id.listViewUnLoadBin);

        String url ="http://103.87.86.29:12950/api/getbinnotunload/"+whsCode; //103.87.86.29:12950 //116.197.129.170:12950

        pDialog = new ProgressDialog(PickListUnLoadBinActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() == 0) {
                        PickListUnLoadBinModel listUnLoadBin = new PickListUnLoadBinModel("DEFAULT DATA");
                        pickListUnLoadBin.add(listUnLoadBin);
                        Toast.makeText(PickListUnLoadBinActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dataUnLoadBin = response.getJSONObject(i);
                            PickListUnLoadBinModel listUnLoadBin = new PickListUnLoadBinModel(dataUnLoadBin.getString("binCode"));
                            pickListUnLoadBin.add(listUnLoadBin);
                        }
                    }

                    pickListUnLoadBinAdapter = new PickListUnLoadBinActivity.PickListUnLoadBinAdapter(pickListUnLoadBin, getApplicationContext());

                    listViewUnLoadBin.setAdapter(pickListUnLoadBinAdapter);

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
                PickListUnLoadBinModel listUnLoadBin = new PickListUnLoadBinModel("DEFAULT DATA");
                pickListUnLoadBin.add(listUnLoadBin);

                pickListUnLoadBinAdapter = new PickListUnLoadBinActivity.PickListUnLoadBinAdapter(pickListUnLoadBin, getApplicationContext());

                listViewUnLoadBin.setAdapter(pickListUnLoadBinAdapter);

                Toast.makeText(PickListUnLoadBinActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
            }
        });

        PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_pick_list_un_load_bin,menu);

        MenuItem menuItemUnLoadBin = menu.findItem(R.id.search_view_pick_list_un_load_bin);

        SearchView searchView = (SearchView) menuItemUnLoadBin.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pickListUnLoadBinAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view_pick_list_un_load_bin) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PickListUnLoadBinAdapter extends BaseAdapter implements Filterable {

        private List<PickListUnLoadBinModel> pickListUnLoadBinModelList;
        private List<PickListUnLoadBinModel> pickListUnLoadBinModelListFiltered;
        private Context context;

        public PickListUnLoadBinAdapter(List<PickListUnLoadBinModel> PickListUnLoadBinModelList, Context context) {
            this.pickListUnLoadBinModelList = PickListUnLoadBinModelList;
            this.pickListUnLoadBinModelListFiltered = PickListUnLoadBinModelList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return pickListUnLoadBinModelListFiltered.size();
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
            View listUnLoadBinView = getLayoutInflater().inflate(R.layout.pick_list_un_load_bin,null);

            TextView unLoadBin = listUnLoadBinView.findViewById(R.id.unLoadBinLoc);

            unLoadBin.setText(pickListUnLoadBinModelListFiltered.get(position).getUnLoadBin());

            listUnLoadBinView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPickListBinLoc = new Intent();
                    intentPickListBinLoc.putExtra("unLoadBin", pickListUnLoadBinModelListFiltered.get(position).getUnLoadBin());

                    setResult(RESULT_OK, intentPickListBinLoc);
                    finish();
                }
            });

            return listUnLoadBinView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = pickListUnLoadBinModelList.size();
                        filterResults.values = pickListUnLoadBinModelList;
                    } else {
                        String keyWord = constraint.toString().toUpperCase();
                        List<PickListUnLoadBinModel> result = new ArrayList<>();

                        for (PickListUnLoadBinModel PickListUnLoadBinModel: pickListUnLoadBinModelList) {
                            if (PickListUnLoadBinModel.getUnLoadBin().contains(keyWord)) {
                                result.add(PickListUnLoadBinModel);
                            }

                            filterResults.count = result.size();
                            filterResults.values = result;

                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    pickListUnLoadBinModelListFiltered = (List<PickListUnLoadBinModel>) results.values;

                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}