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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PulsaActivity extends AppCompatActivity {
    LogConfig lg;
Spinner spinpaket;
    EditText txtnohp, trxpass;
    Button btnbeli, btncek;
    private BroadcastReceiver mBroadcastReceiver;
    private RequestQueue queue;
    String provider="", paketitem="", nohp_a="", id_a="";
    String jsob=null, kode="", kode_kirim ="", id_imei="";
    LinearLayout linpkt;
    ImageView imgprov;
    TextView poinpulsa, txtpoin;
    TextView id_username, nama;
    notif n;

    int paket=0, jumlah_kirim=0;
    List<String> categories;
//    String url = "http://112.78.37.121/apig/gmember/controller_pulsa/paket";
//    String url_beli= "http://112.78.37.121/apig/gmember/controller_pulsa/beli";
//    String url_cek_trx= "http://112.78.37.121/apig/gmember/controller_pulsa/cek_trxpass";
//    String url_convert= "http://112.78.37.121/apig/gmember/controller_pulsa/convert";
    ProgressDialog loadingawal;
    double hslpoin=0;
    SharedPreferences sharedPreferences;
    String harga_beli="", harga_jual="", harga_rupiah ="", secret_code="";
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

            Glide.with(PulsaActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_pulsa);

        txtnohp = (EditText)findViewById(R.id.nohp);
        btnbeli = (Button)findViewById(R.id.btnbeli);
        spinpaket = (Spinner) findViewById(R.id.spinpaket);
        imgprov = (ImageView) findViewById(R.id.imgprov);
        linpkt = (LinearLayout)findViewById(R.id.linpkt);
        trxpass = (EditText) findViewById(R.id.trxpass);
        poinpulsa =(TextView)findViewById(R.id.poinpulsa);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);

        lg = new LogConfig();

        n = new notif();
        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));

        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));

cektombol("saldo");
//        btnbeli.setVisibility(View.INVISIBLE);
//        linpkt.setVisibility(View.INVISIBLE);
//        spinpaket.setVisibility(View.INVISIBLE);
//        trxpass.setVisibility(View.INVISIBLE);


//        ArrayAdapter<CharSequence> adapterpaket = ArrayAdapter.createFromResource(this,
//                allSpinner, android.R.layout.simple_spinner_item);

//       if(categories.size()!= 0) {
//
//       }
txtnohp.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        Toast.makeText(PulsaActivity.this, "start"+start+", before"+before+",count"+count, Toast.LENGTH_SHORT).show();
        if(start==3 && count ==1 ){
            loadingawal = ProgressDialog.show(PulsaActivity.this, "Process", "Wait...", false, false);
            provider(txtnohp.getText().toString());
        }
//        Toast.makeText(getBaseContext(), start+" "+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
});

btnbeli.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(lg.session(getApplicationContext())) {
kode_rahasia();
        }else{
            finish();
        }
    }
});


        spinpaket.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        loadingawal = ProgressDialog.show(PulsaActivity.this, "Process", "Wait...", false, false);
                        Object item = parent.getItemAtPosition(pos);
                        cek_convert(item.toString());
                        paket=pos;
                        paketitem = String.valueOf(item);


                        poinpulsa.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });



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

                Boolean status = intent.getExtras().getBoolean("status_login");

                      //  Toast.makeText(getApplicationContext(), status+"a", Toast.LENGTH_SHORT).show();

//                String k;
//                Toast.makeText(getApplicationContext(), "msuk", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                switch (action)
                {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
                        if(body.toUpperCase().contains("BPJS".toUpperCase()) || body.toUpperCase().contains("PLN".toUpperCase())
                                || body.toUpperCase().contains("FNC".toUpperCase())
                                ||body.toUpperCase().contains("PDAM".toUpperCase())||
                        body.toUpperCase().contains("TEL".toUpperCase()) || body.toUpperCase().contains("TV".toUpperCase())) {

                        }else {

                            message(body);
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

    public void provider(final String nohp){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_paket_pulsa,
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
//                            btnbeli.setVisibility(View.INVISIBLE);
//                            linpkt.setVisibility(View.INVISIBLE);
//                            spinpaket.setVisibility(View.INVISIBLE);
//                            trxpass.setVisibility(View.INVISIBLE);
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
                            builder.setMessage("Your phone number is not detected")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            // ui = 0;

                                        }
                                    })
                            ;
                            final android.app.AlertDialog alert = builder.create();
                            alert.show();

                        }else {
                            jsob = response;


                            try {

                                JSONArray jsonArr = new JSONArray(jsob);
                                categories  = new ArrayList<String>();
                                imgprov.setVisibility(View.VISIBLE);
                                String prov = "";
                                for (int i = 0; i < jsonArr.length(); i++) {
//                                    Toast.makeText(PulsaActivity.this, jsonArr.getJSONObject(i).getString("nama") , Toast.LENGTH_SHORT).show();
//                                    jsonArr.getJSONObject(i).getDouble("lat_from");
                                        prov = jsonArr.getJSONObject(i).getString("nama");
                                    categories.add(jsonArr.getJSONObject(i).getString("nama_paket"));

                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PulsaActivity.this, android.R.layout.simple_spinner_item, categories);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinpaket.setAdapter(spinnerAdapter);
                                    if(prov.equalsIgnoreCase("telkomsel")){
                                        imgprov.setImageResource(R.drawable.telkomsel);
                                    }else if(prov.equalsIgnoreCase("IM3/INDOSAT")){
                                        imgprov.setImageResource(R.drawable.indosat);
                                    }else if(prov.equalsIgnoreCase("esia")){
                                        imgprov.setImageResource(R.drawable.esia);
                                    }else if(prov.equalsIgnoreCase("flexi")){
                                        imgprov.setImageResource(R.drawable.flexi);
                                    }else if(prov.equalsIgnoreCase("BOLT")){
                                        imgprov.setImageResource(R.drawable.freen);
                                    }else if(prov.equalsIgnoreCase("SMARTFREN")){
                                        imgprov.setImageResource(R.drawable.smart);
                                    }else if(prov.equalsIgnoreCase("TRHEE")){
                                        imgprov.setImageResource(R.drawable.three);
                                    }else if(prov.equalsIgnoreCase("xl")){
                                        imgprov.setImageResource(R.drawable.xl);
                                    }else if(prov.equalsIgnoreCase("axis")){
                                        imgprov.setImageResource(R.drawable.axis);
                                    }else{
                                        imgprov.setImageResource(R.drawable.logogpoin);
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
                        Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("nohp", nohp);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void transjson(final String json, final int posisi, final String nohp, final String trxpass){
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(json);
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
            final JSONArray finalJsonArr = jsonArr;
            builder.setMessage( provider+" "+paketitem+" phone number :"+nohp+"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            // ui = 0;

                            String body= null;
                            try {
                                body = finalJsonArr.getJSONObject(posisi).getString("kode_produk")+"."+nohp+".6";
                                kode = body;
                                SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
                                cek_trx(sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"),
                                        sharedPreferences.getString(LogConfig.PASS_SESSION,"0"),trxpass);
                                id_a=finalJsonArr.getJSONObject(posisi).getString("id");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void simpan(final String id_paket, final String id_imei, final String pass, final String nohps, final double harga, final  String trxpassi){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(PulsaActivity.this, response , Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jo = new JSONObject(response);

    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
    builder.setMessage(jo.getString("catatan"))
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                }
            })
    ;
    final android.app.AlertDialog alert = builder.create();
    alert.show();
    if(jo.getString("sukses").equals("false")){

    }else {

        Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, kode_kirim);
        intent.putExtra(RoosterConnectionService.BUNDLE_TO, "h2h@nobita.harmonyb12.com");
        sendBroadcast(intent);

    txtnohp.setText("");

    imgprov.setVisibility(View.GONE);
    poinpulsa.setVisibility(View.GONE);

}
                            saldo();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Error.Response", e.toString());
                            Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        }


                        if(dialog.isShowing()){
                        dialog.dismiss();
                        }

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                        trxpass.setText("");

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Response Map", secret_code+", "+trxpassi);
                params.put("id_paket", id_paket);
                params.put("id_imei", id_imei);
                params.put("password", md5(pass));
                params.put("nohp", nohps.trim());
                params.put("harga", String.valueOf(harga));
                params.put("harga_jual", harga_jual);
                params.put("harga_rupiah", harga_rupiah);
                params.put("harga_beli", harga_beli);
                params.put("trxpass", md5(trxpassi));
                params.put("kode", kode);
                params.put("kode_rahasia", secret_code);
                params.put("ket", "Pembelian Pulsa");
                params.put("counter", String.valueOf(jumlah_kirim));

                return params;
            }
        };
        queue.add(postRequest);
    }
    public void cek_trx(final String id_imei, final String pass, final  String trxpass){
        final String[] respon = {""};
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_trx,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String r) {
                        Log.d("Response", r);

                            JSONObject jo = null;

                            try {
                                jo = new JSONObject(r);
                                if (jo.getString("sukses").equals("false")) {
                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
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
                                } else {
                                    String lengkap = "";
                                    int jumlah = Integer.parseInt(r);
//                                    if (jumlah == 0) {
//                                        kode_kirim = kode;
//                                    } else {
                                        jumlah_kirim = jumlah + 1;
                                        kode_kirim = jumlah_kirim + "." + kode;
//                                    }


                                    simpan(id_a, sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0"),
                                            sharedPreferences.getString(LogConfig.PASS_SESSION, "0"), nohp_a, hslpoin, trxpass);

                                }

                                respon[0] = r;

                            } catch (JSONException e) {
                                e.printStackTrace();
//                            String lengkap = "";
                                int jumlah = Integer.parseInt(r);
//                                if (jumlah == 0) {
//                                    kode_kirim = kode;
//                                } else {
                                    jumlah_kirim = jumlah + 1;
                                    kode_kirim = jumlah_kirim + "." + kode;
//                                }


                                simpan(id_a, sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0"),
                                        sharedPreferences.getString(LogConfig.PASS_SESSION, "0"), nohp_a, hslpoin, trxpass);

                            }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("password", md5(pass));
                params.put("trxpass", md5(trxpass));
                params.put("kode_rahasia", secret_code);
                params.put("kode", kode);

                return params;
            }
        };
        queue.add(postRequest);

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

    public void onPause() {
        super.onPause();


    }

    public void message(String body){
//        if(loadingbeli.isShowing()){
//            loadingbeli.dismiss();
//        }
String ket ="";
            String[] data = body.split("\\-");
        String data1[] = data[0].split("\\.");
        if(data.length>2){
            ket = "No Tujuan : "+data1[2]+", "+data[1];
        }else{
            if(data1.length==0){
ket = body;
            }else{
                ket = "No Tujuan : "+data1[2]+", "+data[1];
            }

        }

//        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
//                builder.setMessage(ket)
//                        .setCancelable(false)
//                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
//                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                                // ui = 0;
//
//                            }
//                        })
//                ;
//                final android.app.AlertDialog alert = builder.create();
//                alert.show();

            if (body.contains("SUKSES") ) {

            }

    }

    public  void cek_convert(final String nama_paket){


        double x=0;
        try {

            JSONArray jsonArr = new JSONArray(jsob);
            for(int a =0; a< jsonArr.length(); a++){
                if( jsonArr.getJSONObject(a).getString("nama_paket").equalsIgnoreCase(nama_paket)){
                    x =jsonArr.getJSONObject(a).getDouble("harga");

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
                                        poinpulsa.setText("Harga, "+rupiah(jo.getDouble("harga"))+" = "+hslpoin+" Poin");
                                        poinpulsa.setVisibility(View.VISIBLE);
                                        harga_jual =jo.getString("harga");
                                        harga_rupiah =jo.getString("harga");
//                                    Toast.makeText(getBaseContext(),finalX1+","+hslpoin, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        Toast.makeText(getBaseContext(),e.toString(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
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
                                    Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {         // Menambahkan parameters post
                            Map<String, String>  params = new HashMap<String, String>();

                            params.put("poin", String.valueOf(finalX1));
                            params.put("ket", "pulsa");


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


    public String rupiah(double harga) {
        String rupiah;
        double hargaawal=0;
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        String k = harga+"";
//        double hargaparsing = Double.parseDouble(k.substring(k.length()-4, k.length()));
//        Toast.makeText(getBaseContext(), k.substring(k.length()-2, k.length())+","+hargaparsing+"", Toast.LENGTH_LONG).show();
//       if( hargaparsing==0){
//           hargaawal=harga;
//       }else{
//           hargaawal= harga-hargaparsing+100;
//       }

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        rupiah =kursIndonesia.format(harga);
//        rupiah = kursIndonesia.format(Double.valueOf(hargaawal));

        return rupiah;
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
                        Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(PulsaActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                PulsaActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("saldo")){
                                    saldo();
                                }else if(menu.equals("byr")) {
                                    transjson(jsob, paket, nohp_a,trxpass.getText().toString() );

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
                        Toast.makeText(PulsaActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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


    public void kode_rahasia(){
        dialog = new Dialog(PulsaActivity.this);
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
                    if(txtnohp.getText().toString().equals("")|| poinpulsa.getText().toString().equals("")){
                        trxpass.setText("");
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PulsaActivity.this);
                        builder.setMessage("Your Trx Password or Id Coloumn is Empty")
                                .setCancelable(false)
                                .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    }
                                });
                        final android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }else {
                        loadingawal = ProgressDialog.show(PulsaActivity.this, "Cek Data", "Wait...", false, false);

                        nohp_a = txtnohp.getText().toString();
                        cektombol("byr");


                    }

                }
            }
        });
        dialog.setTitle("Secret Code");
        dialog.setCancelable(true);
        dialog.show();
    }

}
