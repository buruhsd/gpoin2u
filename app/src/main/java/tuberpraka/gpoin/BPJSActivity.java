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
import android.widget.Button;
import android.widget.EditText;
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

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BPJSActivity extends AppCompatActivity {

    EditText idbpjs, trxpassbpjs;
    Button btncekbpjs, btnbyrbpjs;
    TextView txtketbpjs, poinbpjs, txtpoin, nama, id_username;
    boolean cek=true;
    private BroadcastReceiver mBroadcastReceiver;
    private RequestQueue queue;
    double hslpoin;
    String id_pa="83";
    String harga_beli="", harga_jual="", harga_rupiah ="", secret_code="";
    /*String url_beli= "http://112.78.37.121/apig/gmember/controller_ppob/beli";
    String url_convert= "http://112.78.37.121/apig/gmember/controller_pulsa/convert";
    String url_cek_trx= "http://112.78.37.121/apig/gmember/controller_pulsa/cek_trxpass";
    String url_cek= "http://112.78.37.121/apig/gmember/controller_ppob/insert_cek";*/
    notif n;
    PulsaActivity p;
    String ket ="";
    ProgressDialog loadingawal;
    String kode="", id_imei="",pass="", trxpass="", idpelanggan="", kode_lengkap="", id_user="";
    int jumlah_kirim =0;
    Dialog dialog;


    SharedPreferences sharedPreferences;
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

            Glide.with(BPJSActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_bpjs);

        idbpjs = (EditText) findViewById(R.id.idbpjs);
        trxpassbpjs =(EditText) findViewById(R.id.trxpassbpjs);
        btncekbpjs =(Button)findViewById(R.id.btncekbpjs);
        btnbyrbpjs =(Button)findViewById(R.id.belibtnbpjs);
        txtketbpjs =(TextView)findViewById(R.id.txtketbpjs);
        poinbpjs =(TextView)findViewById(R.id.poinbpjs);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        nama =(TextView)findViewById(R.id.nama);
        id_username=(TextView)findViewById(R.id.id_username);

        queue = Volley.newRequestQueue(this);
        n = new notif();
        p= new PulsaActivity();
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        cektombol("saldo");
        loadingawal = new ProgressDialog(this);
        btncekbpjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogConfig lg = new LogConfig();
                if (lg.session(getApplicationContext())) {
                    if (idbpjs.getText().toString().equalsIgnoreCase("") || trxpassbpjs.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();
                    } else {

                        kode_rahasia("cek");

                    }
                }else{
                finish();
            }


            }
        });

        btnbyrbpjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogConfig lg = new LogConfig();
                if (lg.session(getApplicationContext())) {
                    if (idbpjs.getText().toString().equalsIgnoreCase("") || trxpassbpjs.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();
                    } else {


                        kode_rahasia("bayar");

                    }
                }else{
                    finish();
                }
            }
        });
    }

    public void cekbpjs(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                           final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);
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

                                jumlah_kirim = jumlah+1;
                                kode_lengkap = jumlah_kirim+"."+kode;
//                            }

//                            Toast.makeText(BPJSActivity.this, "ok" , Toast.LENGTH_SHORT).show();
                            if(kode.toUpperCase().contains("CEK".toUpperCase())) {
                                ceking(id_pa, id_imei, pass, idpelanggan, hslpoin, trxpass);
                            }else{
                                simpan(id_pa, id_imei, pass, idpelanggan, hslpoin, trxpass);
                            }
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(BPJSActivity.this, e.toString()+"cekbpjs" , Toast.LENGTH_SHORT).show();
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);
//                            if(jumlah==0){
//                                kode_lengkap = kode;
//                            }else{
                            jumlah_kirim = jumlah+1;
                                kode_lengkap = jumlah_kirim+"."+kode;
//                            }

//                            Toast.makeText(BPJSActivity.this, "ok" , Toast.LENGTH_SHORT).show();
                            if(kode.toUpperCase().contains("CEK".toUpperCase())) {
                                ceking(id_pa, id_imei, pass, idpelanggan, hslpoin, trxpass);
                            }else{
                                simpan(id_pa, id_imei, pass, idpelanggan, hslpoin, trxpass);
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
                        Toast.makeText(BPJSActivity.this, "Connection Invalid" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
Log.e("BPJS", id_imei+", "+pass+", "+secret_code+", "+trxpass+", "+kode);
                params.put("id_imei", id_imei);
                params.put("password", md5(pass));
                params.put("kode_rahasia", secret_code);
                params.put("trxpass", md5(trxpass));
                params.put("kode", kode);

                return params;
            }
        };
        queue.add(postRequest);

    }

    public void ceking(final String id_paket, final String id_imei, final String pass, final String nohps, final double harga, final  String trxpass){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);
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
                                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, kode_lengkap);
                                intent.putExtra(RoosterConnectionService.BUNDLE_TO, "h2h@nobita.harmonyb12.com");
                                sendBroadcast(intent);


                            }
                            saldo();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BPJSActivity.this, e.toString()+"ceking" , Toast.LENGTH_SHORT).show();
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
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_paket", id_paket);
                params.put("id_imei", id_imei);
                params.put("password", md5(pass));
                params.put("nohp", nohps);
                params.put("kode", kode);
                params.put("ket", ket);
                params.put("harga", String.valueOf(harga));
                params.put("trxpass", md5(trxpass));
                params.put("counter", String.valueOf(jumlah_kirim));
                params.put("kode_rahasia", secret_code);

                return params;
            }
        };
        queue.add(postRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
saldo();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            finish();
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                String k;
//                Toast.makeText(getApplicationContext(), "msuk", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                switch (action)
                {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);

                        Log.e("BPJS", body);
                        if(body.toUpperCase().contains("BPJS".toUpperCase())) {

                            cek_convert(body);
                        }

                         return;

                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();


                        break;
                }


            }
        };
        IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver,filter);
        IntentFilter filter1 = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        registerReceiver(mBroadcastReceiver,filter1);
        registerReceiver(mBroadcastReceiver, new IntentFilter("status"));



    }


    public final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            //Toast.makeText(this, hexString.toString() , Toast.LENGTH_SHORT).show();
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
//            bahaya(e.toString());
        }
        return "";
    }

    public  void cek_convert(final String nama_paket){
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
            for (int i = 0; i < data.length; i++) {


                ket = data[i] + " " + ket;
            }
        }

        String tujuan1 []= data[0].split("\\.");
        tujuan = tujuan1[tujuan1.length-1].trim();
        if (idbpjs.getText().toString().trim().equals("")) {

        } else {
//            Toast.makeText(PPOBActivity.this, idpelangganppbob.getText().toString() + "jumlah data "+tujuan, Toast.LENGTH_SHORT).show();
            if (tujuan.equals(idbpjs.getText().toString())) {


//        Toast.makeText(BPJSActivity.this, nama_paket + "jumlah data", Toast.LENGTH_SHORT).show();
                if (nama_paket.toUpperCase().contains("SUKSES".toUpperCase())) {
//            Toast.makeText(BPJSActivity.this, "SUKSES", Toast.LENGTH_SHORT).show();
                    if (nama_paket.toUpperCase().contains("CEK".toUpperCase())) {
                        btnbyrbpjs.setVisibility(View.VISIBLE);
                    }
                    String[] dataket = nama_paket.split("TTAG:");
                    String[] dataket1 = dataket[1].split("\\-");
                    String datakettot = dataket1[0].replace("SUKSES", "").trim();
//

                    final String hargatot = datakettot;



                    StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_transaksi,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Response", response);
//                    Toast.makeText(BPJSActivity.this, response, Toast.LENGTH_SHORT).show();
//
                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject(response);
                                        int angkaSignifikan = 8;
                                        double temp = Math.pow(10, angkaSignifikan);
                                        hslpoin = (double) Math.round(Double.valueOf(jo.getString("poin")) * temp) / temp;
                                        poinbpjs.setText("Harga, " + p.rupiah(jo.getDouble("harga")) + " = " + hslpoin + " Poin");
                                       harga_jual = jo.getString("harga");
//                                        double a = Double.valueOf(hargatot)+jo.getDouble("fee");
                                        harga_rupiah =hargatot;
//                                        harga_beli = String.valueOf(a);
                                        cek_fee();
                                        poinbpjs.setVisibility(View.VISIBLE);
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {         // Menambahkan parameters post
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("poin", String.valueOf(hargatot));
                            params.put("ket", "bpjs");


                            return params;
                        }
                    };
                    queue.add(postRequest);

                    String ket3 = "";

                    String ket1[] = nama_paket.split("\\-");
                    if (ket1.length >= 3) {
                        for (int i = 0; i < ket1.length - 1; i++) {
                            ket3 = ket3 + ket1[i];
                        }
                    } else {
                        ket3 = nama_paket;
                    }

                    txtketbpjs.setText(ket3);
                    txtketbpjs.setVisibility(View.VISIBLE);

                    cek = true;
                    stopService(new Intent(getBaseContext(), DataBase.class));
                }

                if(!cek) {
                    String ket3 = "";

                    String ket1[] = nama_paket.split("\\-");
                    if (ket1.length >= 3) {
                        for (int i = 0; i < ket1.length - 1; i++) {
                            ket3 = ket3 + ket1[i];
                        }
                    } else {
                        ket3 = nama_paket;
                    }

                    txtketbpjs.setText(ket3);
                    txtketbpjs.setVisibility(View.VISIBLE);

                }
            }

//        if(loadingawal.isShowing()){
//            loadingawal.dismiss();
//        }

        }


    }

    public void onStop() {
        super.onStop();

//        if(mBroadcastReceiver.isOrderedBroadcast()) {
        unregisterReceiver(mBroadcastReceiver);
//        }
    }

    public void simpan(final String id_paket, final String id_imei, final String pass, final String nohps, final double harga, final  String trxpass){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli_ppob,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);
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
                                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, kode_lengkap);
                                intent.putExtra(RoosterConnectionService.BUNDLE_TO, "h2h@nobita.harmonyb12.com");
                                sendBroadcast(intent);
                            }
                            saldo();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(dialog.isShowing()) {
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
                        Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_paket", id_paket);
                params.put("id_imei", id_imei);
                params.put("password", md5(pass));
                params.put("nohp", nohps);
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("harga", String.valueOf(harga));
                params.put("trxpass", md5(trxpass));
                params.put("ket", ket);
                params.put("kode", kode);
                params.put("kode_rahasia", secret_code);

                return params;
            }
        };
        queue.add(postRequest);
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
                                    txtpoin.setText(jsonArray.getJSONObject(i).getDouble("poin") + " Poin");
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
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(BPJSActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                BPJSActivity.this.finish();
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
                                if(menu.equals("cek")){
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekbpjs();

                                }else if(menu.equals("byr")){
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekbpjs();
                                }else{
                                    saldo();
                                }
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
                        Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void cek_fee(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_fee,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);

                        try {
                            JSONArray jsonArr = new JSONArray(response);

                            for (int i = 0; i < jsonArr.length(); i++) {
                                double a = Double.valueOf(harga_rupiah)-jsonArr.getJSONObject(i).getDouble("fee");
                                harga_beli = String.valueOf(a);
                                Log.e("harga_beli", harga_beli);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Log.e("error",e.toString());

                        }
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(BPJSActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id", id_pa);

                return params;
            }
        };
        queue.add(postRequest);
//        Log.e("Response log", String.valueOf(hasil));

    }

    public void kode_rahasia(final String ceking){
        dialog = new Dialog(BPJSActivity.this);
        dialog.setContentView(R.layout.row_transaksi_kode);

       final EditText txtkode = (EditText) dialog.findViewById(R.id.txtcode);


       Button btnsend = (Button) dialog.findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtkode.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
                }else {
                    if (ceking.equals("cek")) {
                        if (idbpjs.getText().toString().equalsIgnoreCase("") || trxpassbpjs.getText().toString().equalsIgnoreCase("")||txtkode.getText()
                                .toString().equals("")) {
                            Toast.makeText(getBaseContext(), "Data must be filled", Toast.LENGTH_SHORT).show();
                        } else {
                            kode = "CEKBPJS." + idbpjs.getText().toString() + ".6";
                            ket = "CEK BPJS";
                            trxpass = trxpassbpjs.getText().toString();
                            idpelanggan = idbpjs.getText().toString();
                            cek = false;
                            secret_code = txtkode.getText().toString();

                            trxpassbpjs.setText("");
//                        loadingawal = ProgressDialog.show(BPJSActivity.this, "Cek Data", "Wait...", false, false);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);

                            builder.setMessage("Your BPJS Account : " + idpelanggan + "?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;
                                            loadingawal = ProgressDialog.show(BPJSActivity.this, "Cek Data", "Wait...", false, false);
                                            cektombol("cek");
                                            dialog.cancel();

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

                    } else if (ceking.equals("bayar")) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BPJSActivity.this);

                        builder.setMessage("Your BPJS Account : " + idpelanggan + "?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        // ui = 0;
                                        ket = "BAYAR BPJS";
                                        trxpass = trxpassbpjs.getText().toString();
                                        idpelanggan = idbpjs.getText().toString();
                                        kode = "BYRBPJS." + idbpjs.getText().toString() + ".6";
                                        cek = false;
                                        LogConfig lg = new LogConfig();
                                        loadingawal = ProgressDialog.show(BPJSActivity.this, "Cek Data", "Wait...", false, false);
                                        secret_code = txtkode.getText().toString();
                                        cektombol("byr");

                                        btnbyrbpjs.setVisibility(View.GONE);
                                        dialog.cancel();

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
//                    simpan("id_paket",);
                    }

                }
            }
        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }

}

