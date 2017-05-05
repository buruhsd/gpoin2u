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

public class PDAMActivity extends AppCompatActivity {
    EditText idpelangganpdam, trxpasspdam;
    TextView txtket, poin, txtpoin;
    Button cekpdam, belipdam;
    Spinner spnpdam;
    boolean cek = true;
    private BroadcastReceiver mBroadcastReceiver;
    private RequestQueue queue;
    /*String url_ambi = "http://112.78.37.121/apig/gmember/controller_pdam/paket";
    String url_beli = "http://112.78.37.121/apig/gmember/controller_pdam/beli";
    String url_cek_trx= "http://112.78.37.121/apig/gmember/controller_pulsa/cek_trxpass";
    String url_cek= "http://112.78.37.121/apig/gmember/controller_pdam/insert_cek";
    String url_convert= "http://112.78.37.121/apig/gmember/controller_pulsa/convert";*/
    List<String> categories;
    String js = "", id,paket = "", kode_byr="",kode_cek="", kode="", id_imei="", pass="", passtrx="", ket="", kode_kirim="", idpelanggan="";
    notif n ;
    BPJSActivity b;
    PulsaActivity p;
    double hslpoin=0;
    SharedPreferences sharedPreferences;
    int jumlah_kirim =0;
    LogConfig lg;
    String harga_beli="", harga_jual="", harga_rupiah ="", secret_code="";
    double fee=0;
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

            Glide.with(PDAMActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_pdam);
        idpelangganpdam = (EditText) findViewById(R.id.idpdam);
        trxpasspdam = (EditText) findViewById(R.id.trxpasspdam);
        cekpdam = (Button) findViewById(R.id.btncekpdam);
        belipdam = (Button) findViewById(R.id.belibtnpdam);
        spnpdam = (Spinner) findViewById(R.id.spnpdam);
        txtket =(TextView)findViewById(R.id.txtketpdam);
        poin =(TextView)findViewById(R.id.poinpdam);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);
        lg = new LogConfig();
        n = new notif();
        b = new BPJSActivity();
        p = new PulsaActivity();

        queue = Volley.newRequestQueue(this);
        ambildata();
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));
        loadingawal = new ProgressDialog(this);
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        cektombol("saldo");

        cekpdam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
               kode_rahasia("cek");
                }else{
                    finish();
                }
            }
        });


belipdam.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(lg.session(getApplicationContext())) {
       kode_rahasia("bayar");
    }else{
        finish();
    }
    }
});
        spnpdam.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        Object item = parent.getItemAtPosition(pos);
                        paket = String.valueOf(item);
                        transjson(pos);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }



    public void ambildata() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_paket_pdam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        js = response;
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            categories = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                            String prov = "";
                            for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                categories.add(jsonArr.getJSONObject(i).getString("nama"));

                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PDAMActivity.this, android.R.layout.simple_spinner_item, categories);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnpdam.setAdapter(spinnerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();
                params.put("jenis", String.valueOf(2));
//                params.put("username", username);
//                params.put("password", md5(pass));
//                params.put("nohp", nohps);
//                params.put("harga", String.valueOf(harga));
//                params.put("trxpass", md5(trxpass));

                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
//                        txtketbpjs.setText(txtketbpjs.getText().toString()+body);
//                        txtketbpjs.setVisibility(View.VISIBLE);
                        if(body.toUpperCase().contains("PDAM".toUpperCase())){
//
////                        if(StringcontainsIgnoreCase(container, content))
                            cek_convert(body);
//
                        }


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



    public void transjson( final int posisi){
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(js);
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);
            final JSONArray finalJsonArr = jsonArr;

            String body= null;
            try {
                kode_byr = finalJsonArr.getJSONObject(posisi).getString("kode_bayar");
                kode_cek = finalJsonArr.getJSONObject(posisi).getString("kode_cek");
                id = finalJsonArr.getJSONObject(posisi).getString("id");
                fee = finalJsonArr.getJSONObject(posisi).getDouble("fee");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void cekpdam(){
       // Toast.makeText(getBaseContext(), "cekcekcek", Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);
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
                        //    Toast.makeText(getBaseContext(), kode, Toast.LENGTH_SHORT).show();
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);
//                            if(jumlah==0){
//                                kode_kirim = kode;
//                            }else{
                                jumlah_kirim = jumlah+1;
                                kode_kirim = jumlah_kirim+"."+kode;
//                            }


                            if(kode.toUpperCase().contains("CEK".toUpperCase())) {
                                ceking(id, id_imei, pass, idpelanggan, hslpoin, passtrx);
                            }else{
                                simpan(id, id_imei, pass, idpelanggan, hslpoin, passtrx);
                            }
//                            ceking("1" ,username,pass,  idpelanggan , hslpoin, trxpass);
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);
//                            if(jumlah==0){
//                                kode_kirim = kode;
//                            }else{
                                jumlah_kirim = jumlah+1;
                                kode_kirim = jumlah_kirim+"."+kode;
//                            }


                            if(kode.toUpperCase().contains("CEK".toUpperCase())) {
                                ceking(id, id_imei, pass, idpelanggan, hslpoin, passtrx);
                            }else{
                                simpan(id, id_imei, pass, idpelanggan, hslpoin, passtrx);
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
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("trxpass", b.md5(passtrx));
                params.put("kode_rahasia", secret_code);
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
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);
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
                            cektombol("saldo");

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                params.put("nohp", nohps);
                params.put("kode", kode);
                params.put("kode_rahasia", secret_code);
                params.put("ket", ket);
                params.put("harga", String.valueOf(harga));
                params.put("trxpass", b.md5(passtrx));
                params.put("counter", String.valueOf(jumlah_kirim));

                return params;
            }
        };
        queue.add(postRequest);
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
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);
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
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                params.put("nohp", nohps);
                params.put("harga", String.valueOf(harga));
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("trxpass", b.md5(passtrx));
                params.put("ket", ket);
                params.put("kode",kode);
                params.put("kode_rahasia", secret_code);
                params.put("counter", String.valueOf(jumlah_kirim));

                return params;
            }
        };
        queue.add(postRequest);
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

//            tgl = data[0];
//            kodeproduk = data[1];
//            tujuan = data[2];
//            keterangan = data[3];
//            harga = data[4];
                ket = data[i] + " " + ket;
            }
        }

        String tujuan1 []= data[0].split("\\.");
        tujuan = tujuan1[tujuan1.length-1].trim();
        if (idpelangganpdam.getText().toString().trim().equals("")) {

        } else {
//            Toast.makeText(PPOBActivity.this, idpelangganppbob.getText().toString() + "jumlah data "+tujuan, Toast.LENGTH_SHORT).show();
            if (tujuan.equals(idpelangganpdam.getText().toString())) {

//        Toast.makeText(BPJSActivity.this, nama_paket + "jumlah data", Toast.LENGTH_SHORT).show();
                if (nama_paket.toUpperCase().contains("SUKSES".toUpperCase()) && !cek) {
//            Toast.makeText(BPJSActivity.this, "SUKSES", Toast.LENGTH_SHORT).show();
                    if (nama_paket.toUpperCase().contains("CEK".toUpperCase())) {

                        belipdam.setVisibility(View.VISIBLE);
                    }
                    String[] dataket = nama_paket.split("TTAG:");
                    String[] dataket1 = dataket[1].split("\\-");
                    String datakettot = dataket1[0].replace("SUKSES", " ").trim();
                    final String hargatot = datakettot;

                    StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_transaksi,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Response", response);
                                    //    Toast.makeText(PDAMActivity.this, response, Toast.LENGTH_SHORT).show();
                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject(response);
                                        int angkaSignifikan = 8;
                                        double temp = Math.pow(10, angkaSignifikan);
                                        hslpoin = (double) Math.round(Double.valueOf(jo.getString("poin")) * temp) / temp;
                                        poin.setText("Harga, " + p.rupiah(jo.getDouble("harga")) + " = " + hslpoin + " Poin");
                                        poin.setVisibility(View.VISIBLE);
                                        harga_jual = hargatot;
                                        harga_rupiah =hargatot;
                                        double a = Double.valueOf(harga_rupiah)-fee;
                                        harga_beli = String.valueOf(a);
                                        Log.e("harga_jual", harga_jual+","+harga_beli+","+harga_rupiah);
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {         // Menambahkan parameters post
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("poin", String.valueOf(hargatot));
                            params.put("ket", "pdam");


                            return params;
                        }
                    };
                    queue.add(postRequest);
                    txtket.setText(data[1]);
                    txtket.setVisibility(View.VISIBLE);
                    stopService(new Intent(getBaseContext(), DataBase.class));
                    cek =true;


                }
                if(!cek) {
                    txtket.setText(data[1]);
                    txtket.setVisibility(View.VISIBLE);
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
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PDAMActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(PDAMActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                PDAMActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("saldo")){
                                    saldo();
                                }else if(menu.equals("cek")) {
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekpdam();
//                                    search();
                                }else if(menu.equals("byr")){
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekpdam();
//                                    hasil();
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
                        Toast.makeText(PDAMActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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


    public void kode_rahasia(final String ceking){
        dialog = new Dialog(PDAMActivity.this);
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
                    if (ceking.equals("cek")) {
                        if (idpelangganpdam.getText().toString().equalsIgnoreCase("") || trxpasspdam.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();
                        } else {

                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);

                            builder.setMessage("Your Account PDAM : "+idpelangganpdam.getText().toString()+"?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;
                                            belipdam.setVisibility(View.GONE);
                                            passtrx = trxpasspdam.getText().toString();
//Toast.makeText(getApplictrxpasspdam.setText("");ationContext(), passtrx,Toast.LENGTH_SHORT).show();
                                            cek = false;
                                            ket = "CEK PDAM";
                                            idpelanggan = idpelangganpdam.getText().toString();
                                            kode = kode_cek + "." + idpelangganpdam.getText().toString() + ".6";
                                            loadingawal = ProgressDialog.show(PDAMActivity.this, "Cek Data", "Wait...", false, false);

                                            cektombol("cek");

                                            trxpasspdam.setText("");

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
                        if (idpelangganpdam.getText().toString().equalsIgnoreCase("") || trxpasspdam.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();
                        } else {

                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PDAMActivity.this);

                            builder.setMessage("Your Account PDAM : " + idpelangganpdam.getText().toString() + "?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;
                                            belipdam.setVisibility(View.GONE);
                                            passtrx = trxpasspdam.getText().toString();
                                            cek = false;
                                            ket = "BAYAR PDAM";
                                            idpelanggan = idpelangganpdam.getText().toString();
                                            kode = kode_byr + "." + idpelangganpdam.getText().toString() + ".6";
                                            loadingawal = ProgressDialog.show(PDAMActivity.this, "Cek Data", "Wait...", false, false);
                                            cektombol("byr");
                                            trxpasspdam.setText("");

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

                }
            }
        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }
}

