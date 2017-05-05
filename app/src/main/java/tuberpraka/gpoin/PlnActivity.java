package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
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

public class PlnActivity extends AppCompatActivity {
    EditText idpelangganpln, trxpasspln;
    TextView poinpulsa, ketpln,txtpoin;
    Button cekpln;
    Spinner spnpln;
    boolean cek=true;
    private BroadcastReceiver mBroadcastReceiver;
    private RequestQueue queue;

    List<String> categories;
    String js="", paketitem="", trxtx="", id_imei="", pass ="", txtidp ="", kode_kirim="", secret_code="";
    int paket=0;
    String kode;
    double hslpoin=0;
    notif n;
    BPJSActivity b;
    PulsaActivity p;
    int jumlah_kirim=0;
    ProgressDialog loadingawal;
    LogConfig lg;
    SharedPreferences sharedPreferences;
    String harga_beli="", harga_jual="", harga_rupiah ="";
    TextView id_username, nama;
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

            Glide.with(PlnActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_pln);

        idpelangganpln = (EditText) findViewById(R.id.idpln);
        trxpasspln =(EditText) findViewById(R.id.trxpasspln);
        cekpln =(Button)findViewById(R.id.btncekpln);
        spnpln =(Spinner)findViewById(R.id.spnpln);
        poinpulsa = (TextView)findViewById(R.id.poinpln);
        ketpln = (TextView)findViewById(R.id.txtketpln);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);
        n = new notif();
        lg = new LogConfig();
        n = new notif();
        b = new BPJSActivity();
        p= new PulsaActivity();
        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));

        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        ambildata();
       cektombol("saldo");
        cekpln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                if( trxpasspln.getText().toString().trim().equals("")|| idpelangganpln.getText().toString().trim().equals("") ){
                    Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();
                    trxpasspln.setText("");
                } else {
                    kode_rahasia();
                }


            }else{
                finish();
            }

            }
        });

        spnpln.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        loadingawal = ProgressDialog.show(PlnActivity.this, "Process", "Wait...", false, false);
                        Object item = parent.getItemAtPosition(pos);
                        paket=pos;
                        paketitem = String.valueOf(item);
                        cek_convert(paketitem);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    public void ambildata(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_paket_pln,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        js =response;
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            categories  = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                            String prov = "";
                            for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                categories.add(jsonArr.getJSONObject(i).getString("nama_paket"));

                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PlnActivity.this, android.R.layout.simple_spinner_item, categories);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnpln.setAdapter(spinnerAdapter);

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
                        Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("jenis", "pln");
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
                        if(body.toUpperCase().contains("PLN".toUpperCase())) {
                            message(body);
                        }
                        return;

                    case RoosterConnectionService.UI_AUTHENTICATED:


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

    public void transjson(final String json, final int posisi, final String nohp){
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(json);
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlnActivity.this);
            final JSONArray finalJsonArr = jsonArr;
            builder.setMessage(""+paketitem+" Your Account"+txtidp+"?")
                    .setCancelable(false)
                    .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            // ui = 0;

                            String body= null;
                            try {

                                body = finalJsonArr.getJSONObject(posisi).getString("kode_produk")+"."+nohp+".6";
                                kode = body;

                                SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);

                                cek_trx(sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"),
                                        sharedPreferences.getString(LogConfig.PASS_SESSION,"0"),trxtx, finalJsonArr.getJSONObject(posisi).getString("id"));





                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                            dialog.cancel();

                        }
                    })
            ;
            final android.app.AlertDialog alert = builder.create();
            alert.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void cek_trx(final String id_imei, final String pass, final  String trxpass, final String id){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlnActivity.this);
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
                            String lengkap = "";
                            int jumlah = Integer.parseInt(response);
//                            if(jumlah==0){
//                                kode_kirim = kode;
//                            }else{
                                jumlah_kirim = jumlah+1;
                                kode_kirim = jumlah_kirim+"."+kode;
//                            }


                            simpan(id ,id_imei,pass, txtidp , hslpoin, trxtx);
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


                            simpan(id ,id_imei,pass, txtidp , hslpoin, trxtx);
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
Log.e("Response", secret_code+", "+trxpass);
                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("trxpass", b.md5(trxpass));
                params.put("kode_rahasia", secret_code);
                params.put("kode", kode);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void simpan(final String id_paket, final String id_imei, final String pass, final String nohps, final double harga, final  String trxpass){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlnActivity.this);
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
                        Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Response", secret_code+", "+trxpass);
                params.put("id_paket", id_paket);
                params.put("kode", kode);
                params.put("ket", "Pembelian Pulsa Listrik");
                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("nohp", nohps);
                params.put("harga", String.valueOf(harga));
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("trxpass", b.md5(trxpass));
                params.put("kode_rahasia", secret_code);
                params.put("counter", String.valueOf(jumlah_kirim));

                return params;
            }
        };
        queue.add(postRequest);
    }

    public  void cek_convert(final String nama_paket){


        double x=0;
        try {

            JSONArray jsonArr = new JSONArray(js);
            for(int a =0; a< jsonArr.length(); a++){
                if( jsonArr.getJSONObject(a).getString("nama_paket").equalsIgnoreCase(nama_paket)){
                    x =jsonArr.getJSONObject(a).getDouble("harga");



//                    final double finalX = x;
                    final double finalX1 = x;
                    harga_beli = jsonArr.getJSONObject(a).getString("harga");
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
                                        poinpulsa.setText("Harga, "+p.rupiah(jo.getDouble("harga"))+" = "+hslpoin+" Poin");
                                        poinpulsa.setVisibility(View.VISIBLE);
                                        harga_jual =jo.getString("harga");
                                        harga_rupiah =jo.getString("harga");
                                        Log.e("harga_beli", harga_beli+","+harga_jual+","+harga_rupiah);
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if(loadingawal.isShowing()){
                                        loadingawal.dismiss();
                                    }
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    onBackPressed();
                                    Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {         // Menambahkan parameters post
                            Map<String, String>  params = new HashMap<String, String>();

                            params.put("poin", String.valueOf(finalX1));
                            params.put("ket", "pln");


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

    public void message(String body){

        String ket ="";
        String[] data = body.split("\\-");
        String data1[] = data[0].split("\\.");




        String tujuan1 []= data[0].split("\\.");
        String tujuan = tujuan1[tujuan1.length-1].trim();
        if (idpelangganpln.getText().toString().trim().equals("")) {

        } else {
//            Toast.makeText(PPOBActivity.this, idpelangganppbob.getText().toString() + "jumlah data "+tujuan, Toast.LENGTH_SHORT).show();
            if (tujuan.equals(idpelangganpln.getText().toString())) {
                String ket1[] = body.split("\\-");
                String ket3 = "";
                if(ket1.length>=3) {
                    for (int i = 0; i < ket1.length - 1; i++) {
                        ket3 = ket3 + ket1[i];
                    }
                }else{
                    ket3=body;
                }
                if ((body.contains("GAGAL")||body.contains("SUKSES"))&& !cek) {
                    ketpln.setText(ket3);
                    ketpln.setVisibility(View.VISIBLE);
                    cek = true;
                    stopService(new Intent(getBaseContext(), DataBase.class));
                }
                if(!cek) {
                    ketpln.setText(ket3);
                    ketpln.setVisibility(View.VISIBLE);
                }

            }
        }
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
                        Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(PlnActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PlnActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                PlnActivity.this.finish();
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
                                }else if(menu.equals("cek")) {
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    transjson(js, paket, idpelangganpln.getText().toString() );

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
                        Toast.makeText(PlnActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void onStop(){
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void kode_rahasia(){
        dialog = new Dialog(PlnActivity.this);
        dialog.setContentView(R.layout.row_transaksi_kode);

        final EditText txtkode = (EditText) dialog.findViewById(R.id.txtcode);


        Button btnsend = (Button) dialog.findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtkode.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Data must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    secret_code=txtkode.getText().toString();
                    loadingawal = ProgressDialog.show(PlnActivity.this, "Cek Data", "Wait...", false, false);
                    trxtx = trxpasspln.getText().toString();
                    txtidp = idpelangganpln.getText().toString();
                    cektombol("cek");

                    cek = false;
                    trxpasspln.setText("");
                }
            }



        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }
}
