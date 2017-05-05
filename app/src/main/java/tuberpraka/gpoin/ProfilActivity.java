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
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {
String id, id_imei, kodebyr, trxpass,idpelanggan,kode,ket,kode_kirim,pass;
    private RequestQueue queue;
    EditText idpelangganppbob, trxpassppob, paketppob;
    TextView txtket, poin,txtpoin;
    Button cekppob, byrppob;
    Double hslpoin;
    boolean cek=true;
    PulsaActivity p;
    int jumlah_kirim=0;
    SharedPreferences sharedPreferences;
    LogConfig lg;
    BPJSActivity b;
    String id_paket="";
    String harga_beli="", harga_jual="", harga_rupiah ="", secret_code="";
    double fee=0;
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

            Glide.with(ProfilActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_profil);
        queue = Volley.newRequestQueue(this);

        idpelangganppbob = (EditText) findViewById(R.id.idppob);
        trxpassppob =(EditText) findViewById(R.id.trxpassppob);
        paketppob =(EditText) findViewById(R.id.paket);
        byrppob =(Button)findViewById(R.id.belibtnppob);
        txtket =(TextView)findViewById(R.id.txtketppob) ;
        poin = (TextView)findViewById(R.id.poinppob);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);

        idpelangganppbob.setEnabled(false);
        paketppob.setEnabled(false);
        p= new PulsaActivity();
        b = new BPJSActivity();
        lg = new LogConfig();

        // bayar yang baru pakai cek
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));

        Intent intent = getIntent();
        id =intent.getStringExtra("id");

//        Toast.makeText(ProfilActivity.this,id +", "+id_imei , Toast.LENGTH_SHORT).show();
        hasil();
        saldo();

        byrppob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                    if(idpelangganppbob.getText().toString().equalsIgnoreCase("")||trxpassppob.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(getBaseContext(), "Your Trx Password or Id Coloumn is Empty", Toast.LENGTH_SHORT).show();

                    }else{
                        kode_rahasia();

                    }
                }else{
                    finish();
                }
            }
        });
    }

    public void hasil(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_ppob_cek,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        if(response.contains("false")){
                         finish();
                        }else {
                            try {
                                JSONArray jsonArr = new JSONArray(response);
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    String id_pelanggan = jsonArr.getJSONObject(i).getString("nohp");
                                    String paket = jsonArr.getJSONObject(i).getString("nama");
                                    idpelangganppbob.setText(id_pelanggan);
                                    paketppob.setText(paket);

                                    id_paket = jsonArr.getJSONObject(i).getString("id_paket");
                                    kodebyr = jsonArr.getJSONObject(i).getString("kode_bayar");
                                    fee = jsonArr.getJSONObject(i).getDouble("fee");
                                    cek_convert(jsonArr.getJSONObject(i).getString("alasan"));
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
                        Toast.makeText(ProfilActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id", id);
                params.put("id_imei", id_imei);
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

//            Toast.makeText(ProfilActivity.this, idpelangganppbob.getText().toString() + "jumlah data "+tujuan, Toast.LENGTH_SHORT).show();

                if(nama_paket.toUpperCase().contains("SUKSES".toUpperCase())) {

                    String[] datasukses = nama_paket.split("\\.");

                    String[] dataket = nama_paket.split("TTAG:");
                    String[] dataket1 = dataket[1].split("\\-");
                    String datakettot = dataket1[0].replace("SUKSES", " ").trim();
//

                    final String hargatot = datakettot;

                    cekharga(hargatot);
                    Toast.makeText(getApplicationContext(),hargatot,Toast.LENGTH_SHORT).show();
                    String ket3 = "";
                    String ket1[] = nama_paket.split("\\-");
                    for (int i = 0; i < ket1.length - 1; i++) {
                        ket3 = ket3 + ket1[i];
                    }

                    if (ket1.length >= 3) {
                        txtket.setText(ket3);
                        txtket.setVisibility(View.VISIBLE);
                    } else {
                        txtket.setText(nama_paket);
                        txtket.setVisibility(View.VISIBLE);
                    }
                    cek= true;
                    stopService(new Intent(getBaseContext(), DataBase.class));
                }
                String ket3 = "";

                    String ket1[] = nama_paket.split("\\-");
                    for (int i = 0; i < ket1.length - 1; i++) {
                        ket3 = ket3 + ket1[i];
                    }
//            String ket2 = ket1[3];
                    if (ket1.length >= 3) {
                        txtket.setText(ket3);
                        txtket.setVisibility(View.VISIBLE);
                    } else {
                        txtket.setText(nama_paket);
                        txtket.setVisibility(View.VISIBLE);
                    }





//        try {


//                    final double finalX = x;
//                    final double finalX1 = harga;
//                    StringRequest postRequest = new StringRequest(Request.Method.POST, url_convert,
//                            new Response.Listener<String>()
//                            {
//                                @Override
//                                public void onResponse(String response) {
//                                    Log.d("Response", response);
//
//                                    int angkaSignifikan = 2;
//                                    double temp = Math.pow(10, angkaSignifikan);
//                                    hslpoin= (double) Math.round(Double.valueOf(response)*temp)/temp;
//                                    poinbpjs.setText(harga+" Poin");
////                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
//
//                                }
//                            },
//                            new Response.ErrorListener()
//                            {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    // error
//                                    Log.d("Error.Response", "error");
////                        Toast.makeText(PulsaActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    ) {
//                        @Override
//                        protected Map<String, String> getParams()
//                        {         // Menambahkan parameters post
//                            Map<String, String>  params = new HashMap<String, String>();
//
//                            params.put("poin", String.valueOf(finalX1));
//
//
//                            return params;
//                        }
//                    };
//                    queue.add(postRequest);


//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



    }

    public void cekharga(final String hargatot){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_transaksi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        //      Toast.makeText(PPOBActivity.this, response, Toast.LENGTH_SHORT).show();
//
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            int angkaSignifikan = 3;
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
                        Toast.makeText(ProfilActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("poin", String.valueOf(hargatot));
                params.put("ket", "ppob");


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
                        Toast.makeText(ProfilActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void cekppobi(){
Log.e("Profil","cekppobi");
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
                                if(dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfilActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialoga, @SuppressWarnings("unused") final int id) {
                                                // ui = 0;
//imgprov.setVisibility(View.GONE);
//                                            poinpulsa.setVisibility(View.GONE);

                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{

                                String lengkap = "";
                                int jumlah = Integer.parseInt(response);

                                jumlah_kirim = jumlah+1;
                                kode_kirim = jumlah_kirim+"."+kode;

                                    simpan(id_imei, pass, idpelanggan, hslpoin, trxpass);

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


                                simpan( id_imei, pass, idpelanggan, hslpoin, trxpass);

                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(ProfilActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Profil", id_imei+", "+pass+", "+secret_code+", "+trxpass+", "+kode);
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

    public void simpan(final String id_imei, final String pass, final String nohps, final double harga, final  String trxpass){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli_ppob,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Profil", response);
                        try {
                            if(dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            JSONObject jo = new JSONObject(response);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfilActivity.this);
                            builder.setMessage(jo.getString("catatan"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialoga, @SuppressWarnings("unused") final int id) {

                                        }
                                    })
                            ;
                            final android.app.AlertDialog alert = builder.create();
                            alert.show();
                            if(jo.getString("sukses").equals("true")) {
                                final android.app.AlertDialog.Builder builderi = new android.app.AlertDialog.Builder(ProfilActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialoga, @SuppressWarnings("unused") final int id) {
                                                Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
                                                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, kode_kirim);
                                                intent.putExtra(RoosterConnectionService.BUNDLE_TO, "h2h@nobita.harmonyb12.com");
                                                sendBroadcast(intent);

                                                finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alerti = builderi.create();
                                alerti.show();

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
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(ProfilActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Profil simpan", id_imei+", "+pass+", "+secret_code+", "+trxpass+", "+kode);
                params.put("id_paket", id_paket);
                params.put("id_imei", id_imei);
                params.put("password", b.md5(pass));
                params.put("nohp", nohps);
                params.put("harga", String.valueOf(harga));
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("trxpass", b.md5(trxpass));
                params.put("ket", ket);
                params.put("kode",kode);
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

        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            finish();
        }
    }

    public void kode_rahasia(){
        dialog = new Dialog(ProfilActivity.this);
        dialog.setContentView(R.layout.row_transaksi_kode);

        final EditText txtkode = (EditText) dialog.findViewById(R.id.txtcode);


        Button btnsend = (Button) dialog.findViewById(R.id.btnsend);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtkode.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
                }else {
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfilActivity.this);

                    builder.setMessage(paketppob.getText().toString()+" Your Account : "+idpelangganppbob.getText().toString()+"?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                    trxpass = trxpassppob.getText().toString();
                                    idpelanggan = idpelangganppbob.getText().toString();
                                    kode = kodebyr+"."+idpelanggan+".6";
                                    ket = "BAYAR FINANCE";
                                    secret_code = txtkode.getText().toString();
                                    trxpassppob.setText("");
                                    Log.e("Profil kode", id_imei+", "+pass+", "+secret_code+", "+trxpass+", "+kode);
                                    cek=false;
                                    startService(new Intent(getBaseContext(), DataBase.class));
                                    cekppobi();


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
