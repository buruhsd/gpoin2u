package tuberpraka.gpoin;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryVoucherActivity extends AppCompatActivity {


    private RequestQueue queue;
    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;

    String username ="", id_imei="";
    VoucherAdapter adapter;
    ArrayList<Menu_voucher> data;
    PulsaActivity p;
    TextView txtDateto, txtDatefrom;
    private BroadcastReceiver mBroadcastReceiver;
    int hour, minute, mYear,mMonth, mDay;
    String datefrom="", dateto="", jo="";
    LogConfig lg;
    MainActivity m;
    private String[] arrMonth = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    static final int DATE_DIALOG_ID = 1;
    boolean cek=false;
    ProgressDialog loadingawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m = new MainActivity();


        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(HistoryVoucherActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_history_voucher);

        recyclerView = (RecyclerView) findViewById(R.id.recycTupup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        username =sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0");
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        queue = Volley.newRequestQueue(this);
        loadingawal = ProgressDialog.show(HistoryVoucherActivity.this, "Proses...", "Wait...", false, false);
        hasil();
    }

    public void hasil(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_history_voucher,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response History", response);
                        try {
                            JSONObject js = new JSONObject(response);
                            if(js.getString("sukses").equals("false")){
                                Toast.makeText(HistoryVoucherActivity.this, js.getString("catatan") , Toast.LENGTH_SHORT).show();
                                if(loadingawal.isShowing()){
                                    loadingawal.dismiss();
                                }
                            }
                            else{

                                jo = js.getString("catatan");
                                hasil_desk(jo);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(HistoryVoucherActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void hasil_desk(String jo){
        try {

            JSONArray jsonArray = new JSONArray(jo);
            int count = 0;
            Log.e("Response hasil_desk", jo);

            data = new ArrayList<>();
           for (int i = 0; i < jsonArray.length(); i++) {
                String paket =jsonArray.getJSONObject(i).getString("nama_paket");
                String status = jsonArray.getJSONObject(i).getString("stat");
                String harga = jsonArray.getJSONObject(i).getString("harga")+" Poin";
                String tgl = jsonArray.getJSONObject(i).getString("tgl");
                String harga_jual = jsonArray.getJSONObject(i).getString("harga_jual");
                String kode_pos = jsonArray.getJSONObject(i).getString("kodepos");
                String alamat =  jsonArray.getJSONObject(i).getString("alamat");
                String tlp = jsonArray.getJSONObject(i).getString("tlp");
               String tgl_kirim = jsonArray.getJSONObject(i).getString("tgl_kirim");
               String ket = jsonArray.getJSONObject(i).getString("ket");
               String ekspedisi = jsonArray.getJSONObject(i).getString("ekspedisi");
               String resi = jsonArray.getJSONObject(i).getString("resi");
               String id_kode_voucher = jsonArray.getJSONObject(i).getString("kode");
               String nama_paket = jsonArray.getJSONObject(i).getString("nama");

                Menu_voucher topup = new Menu_voucher(paket, status, harga, tgl, harga_jual, kode_pos, alamat, tlp, tgl_kirim,
                        ket, ekspedisi, resi, id_kode_voucher,nama_paket);
                data.add(topup);
                count++;
            }

            adapter = new VoucherAdapter(data, HistoryVoucherActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Response hasil_error", e.toString());
            Toast.makeText(HistoryVoucherActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
        }
        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }
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
