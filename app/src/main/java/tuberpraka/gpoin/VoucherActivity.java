package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class VoucherActivity extends AppCompatActivity {

    Spinner spinpaket, spinjnspkt;
    TextView poinpulsa, txtpoin;
    TextView id_username, nama;
    SharedPreferences sharedPreferences;
    String id_imei="", jenis_voucher="1";
    LogConfig lg;
    ProgressDialog loadingawal;
    int paket =0, jns=0;
    List<String> categories, categoriesjns, categoriesjenis;
    Button btnorder, btnhistory;
    EditText txttrx,txtjml;

    String jsob="",jsoba="", harga_beli="", harga_jual="", harga_rupiah="", paketitem="", id="", secret_code="", paketitemjenis="";
    double hslpoin=0;
    double poin=0;
    private RequestQueue queue;
    PulsaActivity pa;
    BPJSActivity b;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(VoucherActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_voucher);

        spinpaket = (Spinner)findViewById(R.id.spinpaket);
        spinjnspkt = (Spinner)findViewById(R.id.spinjnspaket);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);
        poinpulsa =(TextView)findViewById(R.id.poinpulsa);
        btnorder =(Button)findViewById(R.id.btnbeli);
        txttrx =(EditText)findViewById(R.id.trxpass);
        btnhistory=(Button)findViewById(R.id.btnhistory);
        txtjml =(EditText) findViewById(R.id.txtjml);

        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));

        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        queue = Volley.newRequestQueue(this);
        loadingawal = ProgressDialog.show(this, "Cek Data", "Wait...", false, false);
        lg = new LogConfig();
        cektombol("saldo");
        pa = new PulsaActivity();
        b = new BPJSActivity();
        lg = new LogConfig();
        jnspaket();
        saldo();



        spinpaket.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        loadingawal = ProgressDialog.show(VoucherActivity.this, "Process", "Wait...", false, false);
                        Object item = parent.getItemAtPosition(pos);

                        cek_harga(item.toString());
                        paket=pos;
                        paketitem = String.valueOf(item);


                        poinpulsa.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        spinjnspkt.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        loadingawal = ProgressDialog.show(VoucherActivity.this, "Process", "Wait...", false, false);
                        Object item = parent.getItemAtPosition(pos);
//                        Toast.makeText(VoucherActivity.this,item.toString(),Toast.LENGTH_LONG ).show();
                        cek_convert(item.toString());
                        jns=pos;
                        paketitemjenis = String.valueOf(item);
if(categoriesjenis.get(pos).toString().equals("2")){
    txtjml.setVisibility(View.INVISIBLE);
    txtjml.setText("1");

}else{
    txtjml.setVisibility(View.VISIBLE);
    txtjml.setText("");

}
                        jenis_voucher =categoriesjenis.get(pos).toString();
                        poinpulsa.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {

                    if(txtjml.getText().toString().trim().equals("") || txtjml.getText().toString().trim().equals("0")){
                        Toast.makeText(VoucherActivity.this, "You must be complite the data", Toast.LENGTH_LONG).show();
                    }else {
                        kode_rahasia();
//                    loadingawal = ProgressDialog.show(VoucherActivity.this, "Process", "Wait...", false, false);




                    }
                }else{
                    finish();
                }



            }
        });
btnhistory.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(lg.session(getApplicationContext())) {

                Intent i = new Intent(VoucherActivity.this, HistoryVoucherActivity.class);
                startActivity(i);

        }else{
            finish();
        }
    }
});

        txtjml.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(txtjml.getText().toString().trim().equals("") || txtjml.getText().toString().trim().equals("0")){

                }else {
                    loadingawal = ProgressDialog.show(VoucherActivity.this, "Process", "Wait...", false, false);
                    cek_harga(paketitem);


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(VoucherActivity.this);
//        hasil = false;
//        b = new BPJSActivity();
        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_tombol,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
//                        hasil = response;
                        JSONObject jo = null;
//                        hasil = false;
                        try {
                            jo = new JSONObject(response);
                            if (jo.getString("sukses").equals("false")) {
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                VoucherActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
//                                if(menu.equals("saldo")){
//                                    saldo();
//                                }else if(menu.equals("byr")) {
//                                    transjson(jsob, paket, nohp_a,trxpass.getText().toString() );
//
//                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();

//                            }


                        }
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
//        Log.e("Response log", String.valueOf(hasil));

    }

    public void jnspaket(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_jenis_voucher,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response", response);
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                        if(response.equals("false")){
                          final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                            builder.setMessage("Invalid Connection")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;
                                            VoucherActivity.this.finish();
                                        }
                                    })
                            ;
                            final android.app.AlertDialog alert = builder.create();
                            alert.show();

                        }else {
                            jsob = response;


                            try {
                                JSONArray jsonArr = new JSONArray(jsob);
                                categoriesjns = new ArrayList<String>();
                                categoriesjenis =new ArrayList<String>();

                                for (int i = 0; i < jsonArr.length(); i++) {
//                                    if (jsonArr.getJSONObject(i).getString("nama").equalsIgnoreCase(nama_paket)) {
                                        categoriesjns.add(jsonArr.getJSONObject(i).getString("nama"));
                                    categoriesjenis.add(jsonArr.getJSONObject(i).getString("jenis"));
//                                    }

                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(VoucherActivity.this, android.R.layout.simple_spinner_item, categoriesjns           );
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinjnspkt.setAdapter(spinnerAdapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Response", e.toString());
                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public  void cek_convert(final String nama_paket) {
        try {
            JSONArray jsonArr = new JSONArray(jsob);

        for (int i = 0; i < jsonArr.length(); i++) {
//            Toast.makeText(VoucherActivity.this, jsonArr.getJSONObject(i).getString("nama"), Toast.LENGTH_LONG).show();
            if (jsonArr.getJSONObject(i).getString("nama").equalsIgnoreCase(nama_paket)) {
               final String id_jenis = jsonArr.getJSONObject(i).getString("id");
        double x = 0;


            StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_paket_voucher,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // menampilkan respone
                            Log.d("Response", response);
                            if(loadingawal.isShowing()){
                                loadingawal.dismiss();
                            }
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                            if(response.equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                builder.setMessage("Invalid Connection")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                // ui = 0;
                                                VoucherActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }else {
                                jsoba = response;


                                try {
                                    JSONArray jsonArr = new JSONArray(jsoba);
                                    categories = new ArrayList<String>();

                                    for (int i = 0; i < jsonArr.length(); i++) {
                                    categories.add(jsonArr.getJSONObject(i).getString("nama_paket"));

                                    }

                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(VoucherActivity.this, android.R.layout.simple_spinner_item, categories);
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinpaket.setAdapter(spinnerAdapter);


                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                            Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {         // Menambahkan parameters post
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("id_imei", id_imei);
                    params.put("id_jenis", id_jenis);


                    return params;
                }
            };
            queue.add(postRequest);
//            for (int a = 0; a < jsonArr.length(); a++) {
//                if (jsonArr.getJSONObject(a).getString("nama_paket").equalsIgnoreCase(nama_paket)) {
//                    x = jsonArr.getJSONObject(a).getDouble("harga");
//
//                    harga_beli = jsonArr.getJSONObject(a).getString("harga");
//                }
//            }


                }

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }
    }

    public  void cek_harga(final String nama_paket){

        double x=0;
        try {

            JSONArray jsonArr = new JSONArray(jsoba);
            for(int a =0; a< jsonArr.length(); a++){
//                Toast.makeText(VoucherActivity.this, "ifa "+jsonArr.getJSONObject(a).getString("nama_paket"), Toast.LENGTH_LONG).show();

                if( jsonArr.getJSONObject(a).getString("nama_paket").equalsIgnoreCase(nama_paket)){
//                    Toast.makeText(VoucherActivity.this, "ifb "+jsonArr.getJSONObject(a).getString("nama_paket"), Toast.LENGTH_LONG).show();

                    x =jsonArr.getJSONObject(a).getDouble("harga");
                    id = jsonArr.getJSONObject(a).getString("id");
                    harga_beli = jsonArr.getJSONObject(a).getString("harga");
                    Log.e("Response cek_harga", harga_beli);
//                    final double finalX = x;
                    int jml=0;
                    if(txtjml.getText().toString().trim().equals("") || txtjml.getText().toString().trim().equals("0")){
                        jml=0;
                    }else {
                       jml = Integer.parseInt(txtjml.getText().toString().trim());

                    }
                    final double finalX1 = x * jml;
                    StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_transaksi,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Response cek_harga", response);

                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject(response);


                                            int angkaSignifikan = 8;
                                            double temp = Math.pow(10, angkaSignifikan);
                                            hslpoin = (double) Math.round(Double.valueOf(jo.getString("poin")) * temp) / temp;
                                            poinpulsa.setText("Harga, " + pa.rupiah(jo.getDouble("harga")) + " = " + hslpoin + " Poin");
                                            poinpulsa.setVisibility(View.VISIBLE);
                                            harga_jual = jo.getString("harga");
                                            harga_rupiah = jo.getString("harga");

//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        Toast.makeText(getBaseContext(),"Invalid Connection", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                        Log.e("Response harga", e.toString());
//                                        Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();

                                    }

                                    if(loadingawal.isShowing()){
                                        loadingawal.dismiss();
                                    }

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.e("Error.Response harga", error.toString());
                                    Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                    if(loadingawal.isShowing()){
                                        loadingawal.dismiss();
                                    }
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {         // Menambahkan parameters post
                            Map<String, String>  params = new HashMap<String, String>();
                            Log.e("Response harga", String.valueOf(finalX1));
                            params.put("poin", String.valueOf(finalX1));
                            params.put("ket", "voucher "+paketitem);


                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Response harga", e.toString());
        }

//

    }

    public void cektrx(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx_voucher,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }else{

                                if(jenis_voucher.equals("1")) {
                                    Intent st = new Intent(VoucherActivity.this, KirimVoucherActivity.class);
                                    st.putExtra("id", id);
                                    st.putExtra("harga_jual", harga_jual);
                                    st.putExtra("harga_beli", harga_beli);
                                    st.putExtra("poin", hslpoin + "");
                                    st.putExtra("paketitem", paketitem);
                                    startActivity(st);
                                    txttrx.setText("");
                                    txtjml.setText("");
                                }else{
                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                    builder.setMessage("Your Data :\n"+ "Package    :"+paketitem+"("+poin+")")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                    loadingawal = ProgressDialog.show(VoucherActivity.this, "Process", "Wait...", false, false);
                                                    savedata();
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                                }
                                            })
                                    ;
                                    final android.app.AlertDialog alert = builder.create();
                                    alert.show();

                                }


                                cek_harga(paketitem);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);

                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"));
                params.put("trxpass", b.md5(txttrx.getText().toString()));
                params.put("kode_rahasia", secret_code);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void saldo(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
                        if(response.equals("false")) {

                            txtpoin.setText("0 Poin");
                        }else{
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    poin = jsonArray.getJSONObject(i).getDouble("poin");
                                    txtpoin.setText(poin+"Poin");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                        Toast.makeText(VoucherActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }
    }

    public void kode_rahasia(){
        dialog = new Dialog(VoucherActivity.this);
        dialog.setContentView(R.layout.row_transaksi_kode);

        final EditText txtkode = (EditText) dialog.findViewById(R.id.txtcode);


        Button btnsend = (Button) dialog.findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secret_code = txtkode.getText().toString();
                if(txtkode.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
                }else {

                    cektrx();


                }
            }
        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Boolean login = sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF, true);
        if (!login) {
            finish();
        }
    }

    public void savedata(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli_voucher,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }else{
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VoucherActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                // ui = 0;
                                                VoucherActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Response", e.toString());
                        }
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                        txttrx.setText("");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(VoucherActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//                Log.e("Response",sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0")+", "+txtnama+", "+txtalamat+", "+txtkodepos+", " +
//                        txttlp+", "+b.md5(txttrxpass)+", "+id_paket+", "+harga_jual+", "+harga_beli+", "+poin);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"));
                params.put("nama", "");
                params.put("alamat", "");
                params.put("kodepos", "");
                params.put("telp", "");
                params.put("trxpass", b.md5(txttrx.getText().toString()));
                params.put("id_paket", id);
                params.put("harga_jual", harga_jual);
                params.put("harga_beli", harga_beli);
                params.put("kode_rahasia", secret_code);
                params.put("poin", String.valueOf(hslpoin));

                return params;
            }
        };
        queue.add(postRequest);
    }

}
