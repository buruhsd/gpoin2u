package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class VGActivity extends AppCompatActivity {
    Spinner spnpaket, spnprov;
    EditText trxpass, idp;
    TextView poin, ketvg,txtpoin;
    Button btnbelivg;
    private BroadcastReceiver mBroadcastReceiver;
    private RequestQueue queue;
    LogConfig lg;
    /*String url_paket= "http://112.78.37.121/apig/gmember/controller_vg/paket";
    String url_prov= "http://112.78.37.121/apig/gmember/controller_vg/provider";
    String url_convert= "http://112.78.37.121/apig/gmember/controller_pulsa/convert";
    String url_cek_trx= "http://112.78.37.121/apig/gmember/controller_pulsa/cek_trxpass";
    String url_beli= "http://112.78.37.121/apig/gmember/controller_pulsa/beli";*/

    List<String> categories, categories_paket;
    String js_provider="", js_paket ="", paket="", id_imei="", pass="", trxtx="", kode="", kodep="", idpaket ="", kode_kirim="", idpelanggan="";
    double hslpoin=0;
    Boolean cek = true;
    notif n;
    PulsaActivity b;
    BPJSActivity p;
    int jumlah_akhir=0;
    SharedPreferences  sharedPreferences;
    String harga_beli="", harga_jual="", harga_rupiah ="", secret_code="";
    TextView id_username, nama;
    Dialog dialog;
    ProgressDialog loadingawal;

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

            Glide.with(VGActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_vg);
        spnpaket = (Spinner)findViewById(R.id.spinpaketppob);
        spnprov = (Spinner)findViewById(R.id.spinprovider);
        trxpass =(EditText)findViewById(R.id.trxpassvg);
        btnbelivg =(Button)findViewById(R.id.btnbelivg);
        poin = (TextView)findViewById(R.id.poinvg) ;
        ketvg = (TextView)findViewById(R.id.ketvg) ;
        idp = (EditText)findViewById(R.id.idpvg);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);
        lg = new LogConfig();
        n = new notif();
        b =  new PulsaActivity();
        p =  new BPJSActivity();
        queue = Volley.newRequestQueue(this);
       sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));
        loadingawal = new ProgressDialog(this);
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        ambildata();
        cektombol("saldo");

        btnbelivg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                    if (trxpass.getText().toString().equals("") || idp.getText().toString().equals("")) {
                        trxpass.setText("");
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VGActivity.this);
                        builder.setMessage("Your Trx Password or Id Coloumn is Empty")
                                .setCancelable(false)
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    }
                                });
                        final android.app.AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        kode_rahasia();
                    }

                }else{
                    finish();
                }
            }
        });

        spnpaket.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        paket = String.valueOf(item);
                        cek_convert(paket);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        spnprov.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);

                        ambildatapaket(String.valueOf(item));
//                        ambildatapaket();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    public void ambildata(){
    StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_prov_vg,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response);
                    js_provider =response;
                    try {
                        JSONArray jsonArr = new JSONArray(response);
                        categories  = new ArrayList<String>();
                     //   Toast.makeText(VGActivity.this, "oka" , Toast.LENGTH_SHORT).show();
//                            imgprov.setVisibility(View.VISIBLE);
                        String prov = "";
                        for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                            categories.add(jsonArr.getJSONObject(i).getString("nama"));

                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(VGActivity.this, android.R.layout.simple_spinner_item, categories);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnprov.setAdapter(spinnerAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(VGActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                    }


                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("Error.Response", error.toString() );
                    Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                }
            }
    ) {
        @Override
        protected Map<String, String> getParams()
        {         // Menambahkan parameters post
            Map<String, String>  params = new HashMap<String, String>();
            params.put("jenis", "Voucher Game");

            return params;
        }
    };
    queue.add(postRequest);
}

    public void ambildatapaket(final String prov){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_paket_vg,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        js_paket =response;
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            categories_paket  = new ArrayList<String>();

//                            imgprov.setVisibility(View.VISIBLE);
                            String prov = "";
                            for (int i = 0; i < jsonArr.length(); i++) {

//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                categories_paket.add(jsonArr.getJSONObject(i).getString("nama_paket"));

                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(VGActivity.this, android.R.layout.simple_spinner_item, categories_paket);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnpaket.setAdapter(spinnerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VGActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                    params.put("nama", prov);
                return params;
            }
        };
        queue.add(postRequest);
    }


    @Override
    protected void onResume() {
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            finish();
        }
        super.onResume();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                String k;
//                Toast.makeText(getApplicationContext(), "msuk", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
//                        if(body.toUpperCase().contains("V".toUpperCase()) ) {
                            message(body);
//                        }
                        return;

                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG,"Got a broadcast to show the main app window");
                        //Show the main app window
//                        showProgress(false);
//                        Intent i2 = new Intent(mContext,ContactListActivity.class);
//                        startActivity(i2);
//                        finish();

                        break;
                }


            }
        };
        IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
        IntentFilter filter1 = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        registerReceiver(mBroadcastReceiver, filter1);
        registerReceiver(mBroadcastReceiver, new IntentFilter("status"));


    }


    public  void cek_convert(final String nama_paket){


        double x=0;
        try {

            JSONArray jsonArr = new JSONArray(js_paket);
            for(int a =0; a< jsonArr.length(); a++){
                if( jsonArr.getJSONObject(a).getString("nama_paket").equalsIgnoreCase(nama_paket)){
                    x =jsonArr.getJSONObject(a).getDouble("harga");
                    kodep = jsonArr.getJSONObject(a).getString("kode_produk");
                    idpaket = jsonArr.getJSONObject(a).getString("id");

                    harga_beli = jsonArr.getJSONObject(a).getString("harga");
//                    final double finalX = x;
                    final double finalX1 = x;
                    StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_transaksi,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Response", response);
                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject(response);
                                    int angkaSignifikan = 8;
                                    double temp = Math.pow(10, angkaSignifikan);
                                    hslpoin= (double) Math.round(Double.valueOf(jo.getString("poin"))*temp)/temp;
                                    poin.setText("Harga, "+b.rupiah(jo.getDouble("harga"))+" = "+hslpoin+" Poin");
                                    poin.setVisibility(View.VISIBLE);
                                        harga_jual =jo.getString("harga");
                                        harga_rupiah =jo.getString("harga");
                                        Log.e("harga_beli", harga_jual+","+harga_beli);
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
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
                                    Log.d("Error.Response", error.toString() );
                                    Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {         // Menambahkan parameters post
                            Map<String, String>  params = new HashMap<String, String>();

                            params.put("poin", String.valueOf(finalX1));
                            params.put("ket", "vg");


                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

//

    }


    public void cekvg(){
//        Toast.makeText(VGActivity.this, kode , Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        Log.d("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VGActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                // ui = 0;
//imgprov.setVisibility(View.GONE);
//                                            poinpulsa.setVisibility(View.GONE);
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                }

                                if(loadingawal.isShowing()){
                                    loadingawal.dismiss();
                                }
                            }else{
//                            Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);

                                jumlah_akhir = jumlah+1;
                                kode_kirim = jumlah_akhir+"."+kodep+"."+idpelanggan+".6";




                            ceking(idpaket ,id_imei,pass,  "" , hslpoin, trxtx);
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);

                            jumlah_akhir = jumlah+1;
                            kode_kirim = jumlah_akhir+"."+kodep+"."+idpelanggan+".6";




                            ceking(idpaket ,id_imei,pass,  "" , hslpoin, trxtx);
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("trxpass", b.md5(trxtx));
                params.put("kode_rahasia", secret_code);
                params.put("kode", kode);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public void ceking(final String id_paket, final String id_imei, final String pass, final String nohps, final double harga, final  String trxpass){
//        Toast.makeText(VGActivity.this, kode+","+kode_kirim , Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VGActivity.this);
                            builder.setMessage(jo.getString("catatan"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;
//imgprov.setVisibility(View.GONE);
//                                            poinpulsa.setVisibility(View.GONE);
                                        }
                                    })
                            ;
                            final android.app.AlertDialog alert = builder.create();
                            alert.show();

                            if(jo.getString("sukses").equals("true")) {
                                Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
                                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, kode_kirim);
                                intent.putExtra(RoosterConnectionService.BUNDLE_TO, "h2h@nobita.harmonyb12.com");
                                sendBroadcast(intent);
                            }
                            saldo();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VGActivity.this, e.toString()+"ceking" , Toast.LENGTH_SHORT).show();
                        }
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
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_paket", id_paket);
                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("nohp", idpelanggan);
                params.put("kode", kode);
                params.put("kode_rahasia", secret_code);
                params.put("ket", "Pembelian Voucher Game");
                params.put("harga", String.valueOf(hslpoin));
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("trxpass", b.md5(trxtx));
                params.put("counter", String.valueOf(jumlah_akhir));

                return params;
            }
        };
        queue.add(postRequest);
    }

    public  void message(final String nama_paket){
        String ket = "";
        String tgl = "";
        String kodeproduk = "";
        String tujuan = "";
        String keterangan = "";
        String harga = "";
        final String[] data = nama_paket.split("\\-");
        if (data.length == 0) {
            ket = "okoko";
        } else {
            String tujuan1 []= data[0].split("\\.");
            tujuan = tujuan1[tujuan1.length-1].trim();

//            Toast.makeText(PPOBActivity.this, idpelangganppbob.getText().toString() + "jumlah data "+tujuan, Toast.LENGTH_SHORT).show();
                if (tujuan.equals(idp.getText().toString())) {
                    if((nama_paket.toUpperCase().contains("gagal".toUpperCase())||nama_paket.toUpperCase().contains("sukses".toUpperCase())) && !cek){
                        ketvg.setText(data[1]);
                        ketvg.setVisibility(View.VISIBLE);
                        cek = true;
                        stopService(new Intent(getBaseContext(), DataBase.class));
                    }
                   if(!cek){
                       ketvg.setText(data[1]);
                       ketvg.setVisibility(View.VISIBLE);
                   }

                 }

        }

    }

    public void onStop(){
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void saldo() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (response.equals("false")) {

                            txtpoin.setText("0 Poin");
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {


                                    txtpoin.setText(jsonArray.getJSONObject(i).getDouble("poin") + "Poin");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_imei", id_imei);

                return params;
            }
        };
        queue.add(postRequest);


    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menua ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_back:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(VGActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        final String pass =sharedPreferences.getString(LogConfig.PASS_SESSION,"0");


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_tombol,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            if (jo.getString("sukses").equals("false")) {
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VGActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                VGActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                }

                                if(loadingawal.isShowing()){
                                    loadingawal.dismiss();
                                }
                            }else{
                                if(menu.equals("saldo")){
                                    saldo();
                                }else if(menu.equals("byr")) {
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekvg();
                                }
                            }
                        } catch (JSONException e){
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
                        Toast.makeText(VGActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void kode_rahasia(){
        dialog = new Dialog(VGActivity.this);
        dialog.setContentView(R.layout.row_transaksi_kode);

        final EditText txtkode = (EditText) dialog.findViewById(R.id.txtcode);


        Button btnsend = (Button) dialog.findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtkode.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
                }else {
                    secret_code = txtkode.getText().toString();

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VGActivity.this);

                        builder.setMessage(paket+" Your Account : "+idp.getText().toString()+"?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                        cek =false;
                                        trxtx = trxpass.getText().toString();
                                        idpelanggan = idp.getText().toString();
                                        trxpass.setText("");
                                        loadingawal = ProgressDialog.show(VGActivity.this, "Cek Data", "Wait...", false, false);

//Toast.makeText(VGActivity.this, idpelanggan, Toast.LENGTH_SHORT).show();
                                        kode = kodep+"."+idpelanggan+".6";
                                        cektombol("byr");



                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                        dialog.cancel();

                                    }
                                })
                        ;
                        final android.app.AlertDialog alert = builder.create();
                        alert.show();




                }
            }
        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }
}





