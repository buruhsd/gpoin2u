package tuberpraka.gpoin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerCareActivity extends AppCompatActivity {
    TableLayout recyclerView;
    ListCsAdapter adapter;
    ArrayList<csClass> data;
    private RequestQueue queue;
    String username ="", id_imei="";
    SharedPreferences sharedPreferences;
    TableRow tr_headtopup;
    TableLayout tltopup;
    TextView kltanggal,kltopup, txtcs, klstatus, klprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_care);
        txtcs = (TextView)findViewById(R.id.txtcs);
//        tltopup = (TableLayout) findViewById(R.id.rcs);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        username =sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0");
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        queue = Volley.newRequestQueue(this);

hasil();
    }

    public void hasil(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_list_cs,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){

                            Toast.makeText(CustomerCareActivity.this, "Sorry, no data found" , Toast.LENGTH_SHORT).show();
                        }else{
                            JSONObject jo = null;
                            try {
//                                    jo = new JSONObject(response);

                                JSONArray jsonArray = new JSONArray(response);

                                data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    if((i+1) %2 ==0){
//
//                                        Toast.makeText(CustomerCareActivity.this, "2"+jsonArray.getJSONObject(i).getString("jenis")+" : \n"+jsonArray.getJSONObject(i).getString("nomor") , Toast.LENGTH_SHORT).show();
//                                        kltanggal = new TextView(CustomerCareActivity.this);
//                                        kltanggal.setText(jsonArray.getJSONObject(i).getString("jenis")+": \n"+jsonArray.getJSONObject(i).getString("nomor"));
//                                        kltanggal.setTextColor(Color.BLACK);
//                                        kltanggal.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//                                        kltanggal.setPadding(10, 10, 10, 10);
//                                        kltanggal.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
//                                        tr_headtopup.addView(kltanggal); // Adding textView to tablerow.
//
//                                        tltopup.addView(tr_headtopup, new TableRow.LayoutParams(
//                                                TableRow.LayoutParams.MATCH_PARENT,
//                                                TableRow.LayoutParams.WRAP_CONTENT));
//                                    }else{
//                                        Toast.makeText(CustomerCareActivity.this, "1"+jsonArray.getJSONObject(i).getString("jenis")+": \n"+jsonArray.getJSONObject(i).getString("nomor") , Toast.LENGTH_SHORT).show();
//                                        tr_headtopup = new TableRow(CustomerCareActivity.this);
//                                        tr_headtopup.setLayoutParams(new TableRow.LayoutParams(
//                                                TableRow.LayoutParams.WRAP_CONTENT,
//                                                TableRow.LayoutParams.WRAP_CONTENT));
//
//                                        kltopup = new TextView(CustomerCareActivity.this);
//                                        kltopup.setText(jsonArray.getJSONObject(i).getString("jenis")+": \n"+jsonArray.getJSONObject(i).getString("nomor"));
//                                        kltopup.setTextColor(Color.BLACK);
//                                        kltopup.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//                                        kltopup.setPadding(10, 10, 10, 10);
//                                        kltopup.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
//                                        tr_headtopup.addView(kltopup); // Adding textView to tablerow.
//if(i==jsonArray.length()-1){
//    tltopup.addView(tr_headtopup, new TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT));
//}
txtcs.setText(txtcs.getText().toString()+jsonArray.getJSONObject(i).getString("jenis")+": "+jsonArray.getJSONObject(i).getString("nomor")+"\n\n");

                                        /** Creating another textview **/


//                                    }
//                                prov = jsonArr.getJSONObject(i).getString("nama");
//                                        categories.add();

//                                    csClass csi = new csClass(jsonArray.getJSONObject(i).getString("cek"),
//                                            jsonArray.getJSONObject(i).getString("nomor"),jsonArray.getJSONObject(i).getString("jenis"));
//                                    data.add(csi);
//                                    count++;





                                }

//                                adapter = new ListCsAdapter(data, CustomerCareActivity.this);
////                                recyclerView.setAdapter(adapter);
//                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(CustomerCareActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(CustomerCareActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                return params;
            }
        };
        queue.add(postRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean login = sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF, true);
        if (!login) {
            finish();
        }
    }

}
