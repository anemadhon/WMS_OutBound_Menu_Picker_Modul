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

public class PickerPickListActivity extends AppCompatActivity {

    String whsCode;
    String userLogin;

    ListView listView;

    List<PickerPickListModel> pickerList = new ArrayList<>();

    PickerPickListAdapter pickerPickListAdapter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_pick_list);

        Intent intent = getIntent();
        whsCode = "WMSISTPR";//intent.getStringExtra("whsCode");
        userLogin = "WMS";//intent.getStringExtra("userLogin");

        getSupportActionBar().setTitle("Picker - "+userLogin+" - "+whsCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        listView = findViewById(R.id.listViewPicker);

        String url ="http://103.87.86.29:12950/api/getplbywhs/whscode="+whsCode; //103.87.86.29:12950 //116.197.129.170:12950

        pDialog = new ProgressDialog(PickerPickListActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() == 0) {
                        PickerPickListModel listPicker = new PickerPickListModel("DEFAULT DATA","YYYY-MM-DD HH:ii:ss","DEFAULT DATA","DEFAULT DATA","DEFAULT DATA");
                        pickerList.add(listPicker);
                        Toast.makeText(PickerPickListActivity.this, "Data Tidak Ditemukan", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dataPicker = response.getJSONObject(i);
                            PickerPickListModel listPicker = new PickerPickListModel(dataPicker.getString("uDocNum"),dataPicker.getString("docDate"),dataPicker.getString("pickRemark"),dataPicker.getString("cardCode"),dataPicker.getString("cardName"));
                            pickerList.add(listPicker);
                        }
                    }

                    pickerPickListAdapter = new PickerPickListAdapter(pickerList, getApplicationContext());

                    listView.setAdapter(pickerPickListAdapter);

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
                PickerPickListModel listPicker = new PickerPickListModel("DEFAULT DATA","YYYY-MM-DD HH:ii:ss","DEFAULT DATA","DEFAULT DATA","DEFAULT DATA");
                pickerList.add(listPicker);
                pickerPickListAdapter = new PickerPickListAdapter(pickerList, getApplicationContext());

                listView.setAdapter(pickerPickListAdapter);
                Toast.makeText(PickerPickListActivity.this, "Error, Gagal Mengambil Data", Toast.LENGTH_LONG).show();
            }
        });

        PickerPickSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_picker_pick_list,menu);

        MenuItem menuItem = menu.findItem(R.id.search_view_picker_pick_list);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pickerPickListAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view_picker_pick_list) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PickerPickListAdapter extends BaseAdapter implements Filterable {

        private List<PickerPickListModel> pickerPickListModelList;
        private List<PickerPickListModel> pickerPickListModelListFiltered;
        private Context context;

        public PickerPickListAdapter(List<PickerPickListModel> pickerPickListModelList, Context context) {
            this.pickerPickListModelList = pickerPickListModelList;
            this.pickerPickListModelListFiltered = pickerPickListModelList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return pickerPickListModelListFiltered.size();
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
            View pickerView = getLayoutInflater().inflate(R.layout.picker_pick_list_view,null);

            TextView pickNumber = pickerView.findViewById(R.id.pickNumber);
            TextView pickDate = pickerView.findViewById(R.id.pickDate);
            TextView pickerName = pickerView.findViewById(R.id.pickerName);
            TextView pickMemo = pickerView.findViewById(R.id.pickMemo);
            TextView pickCardCode = pickerView.findViewById(R.id.pickerCardCode);

            String postingDate[] = pickerPickListModelListFiltered.get(position).getPickDate().split("T");

            pickNumber.setText(pickerPickListModelListFiltered.get(position).getPickNumber());
            pickDate.setText(postingDate[0]);
            pickerName.setText(pickerPickListModelListFiltered.get(position).getPickCardName());
            pickMemo.setText(pickerPickListModelListFiltered.get(position).getPickMemo());
            pickCardCode.setText(pickerPickListModelListFiltered.get(position).getPickCardCode());

            pickerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentPickNmbr = new Intent();
                    intentPickNmbr.putExtra("pickNmbr", pickerPickListModelListFiltered.get(position).getPickNumber());
                    setResult(RESULT_OK, intentPickNmbr);
                    finish();
                }
            });

            return pickerView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = pickerPickListModelList.size();
                        filterResults.values = pickerPickListModelList;
                    } else {
                        String keyWord = constraint.toString().toUpperCase();
                        List<PickerPickListModel> result = new ArrayList<>();

                        for (PickerPickListModel pickerPickListModel:pickerPickListModelList) {
                            if (pickerPickListModel.getPickNumber().contains(keyWord)) {
                                result.add(pickerPickListModel);
                            }

                            filterResults.count = result.size();
                            filterResults.values = result;

                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    pickerPickListModelListFiltered = (List<PickerPickListModel>) results.values;

                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}