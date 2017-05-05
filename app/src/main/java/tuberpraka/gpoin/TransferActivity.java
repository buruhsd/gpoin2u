package tuberpraka.gpoin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransferActivity extends AppCompatActivity {
    String poin ="";
    EditText txtuser, trxpass;
    Button Transfer, scan;
    EditText txtpoin;
    private RequestQueue queue;
    LogConfig lg;
    ProgressDialog mulai;
    BPJSActivity b;
    private BroadcastReceiver mBroadcastReceiver;
    notif n;
    SharedPreferences sharedPreferences;
    TextView menupoin,txtpersen;
    String id_imei="";
    TextView id_username, nama;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2222;

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

            Glide.with(TransferActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_transfer);

        Intent intent = getIntent();
        poin = intent.getStringExtra("poin");
        txtuser = (EditText)findViewById(R.id.txttfuser);
        Transfer = (Button)findViewById(R.id.tfbtn);
        txtpoin = (EditText)findViewById(R.id.txtpointf);
        trxpass = (EditText) findViewById(R.id.txttrxpasstf);
        menupoin =(TextView)findViewById(R.id.menupoin);
        txtpersen = (TextView)findViewById(R.id.txtpersen);
        id_username = (TextView)findViewById(R.id.id_username);
        nama =(TextView)findViewById(R.id.nama);
        scan = (Button)findViewById(R.id.scan);

        lg = new LogConfig();
        queue = Volley.newRequestQueue(this);

        b = new BPJSActivity();
        n = new notif();
        txtpoin.setText(poin);
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0");
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));

        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        cektombol("saldo");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TransferActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED  ) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TransferActivity.this,
                            Manifest.permission.CAMERA)) {

                        new IntentIntegrator(TransferActivity.this).initiateScan();

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(TransferActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }else{
                    new IntentIntegrator(TransferActivity.this).initiateScan();
                }


            }
        });

        Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lg.session(getApplicationContext())) {

                if(txtuser.getText().toString().equals("")){
                    Toast.makeText(TransferActivity.this,"Username coloumn must be filled", Toast.LENGTH_SHORT).show();
                }else{
                    if(trxpass.getText().toString().equals("")){
                        Toast.makeText(TransferActivity.this,"Trx Password coloumn must be filled", Toast.LENGTH_SHORT).show();
                    }else if(Double.parseDouble(txtpoin.getText().toString()) < 1) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TransferActivity.this);
                        builder.setMessage("Your transfer poin min 1")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                    }
                                })

                        ;
                        final android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        poin= txtpoin.getText().toString();
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TransferActivity.this);
                        builder.setMessage("Are you sure want transfer "+poin+"poin to "+txtuser.getText().toString())
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        mulai = ProgressDialog.show(TransferActivity.this, "Process...", "Wait...", false, false);

//                        Toast.makeText(TransferActivity.this, sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0")+","+
//                                sharedPreferences.getString(LogConfig.PASS_SESSION, "0")+","+ trxpass.getText().toString(), Toast.LENGTH_SHORT).show();
                                        cektombol("transfer");
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                trxpass.setText("");
                            }
                        })
                        ;
                        final android.app.AlertDialog alert = builder.create();
                        alert.show();


                    }
                }
                }else{
                    finish();
                }
            }
        });
    }

    public  void transfer(final String username, final String password, final String trxpass1){

//        Toast.makeText(TransferActivity.this, username+","+
//                password+","+ trxpass1+","+txtuser.getText().toString(), Toast.LENGTH_SHORT).show();
        double x=0;
final String user=txtuser.getText().toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_transfer,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(TransferActivity.this,response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject js = new JSONObject(response);
                            boolean cek = js.getBoolean("sukses");
                            if(cek){

                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TransferActivity.this);
                                builder.setMessage(js.getString("ket"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TransferActivity.this);
                                builder.setMessage(js.getString("ket"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }
                            txtuser.setText("");
                            trxpass.setText("");
                            txtpoin.setText("");
                            saldo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TransferActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        if(mulai.isShowing()){
                                mulai.dismiss();
                            }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(TransferActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        if(mulai.isShowing()){
                            mulai.dismiss();
                        }
//                        Toast.makeText(TransferActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("poin", poin);
                params.put("username", user);
                params.put("id_imei_transfer", username);
                params.put("password_transfer", b.md5(password));
                params.put("trxpass", b.md5(trxpass1));


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

                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
                        n.onNotifShow(body,PDAMActivity.class);
                        return;

                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();


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

    public void onStop(){
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
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

    public void saldo() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (response.equals("false")) {

                            menupoin.setText("0 Poin");
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    menupoin.setText(jsonArray.getJSONObject(i).getDouble("poin") + "Poin");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        persen();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(TransferActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(TransferActivity.this);

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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TransferActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                TransferActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("saldo")){
                                    saldo();
                                }else if(menu.equals("transfer")) {
                                    transfer(id_imei, pass, trxpass.getText().toString());
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
                        Toast.makeText(TransferActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void persen(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_persen,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);

                          txtpersen.setText(" *Ppn "+response);
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(TransferActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Transfer", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.e("Transfer", "Scanned");
                txtuser.setText(result.getContents());
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new IntentIntegrator(TransferActivity.this).initiateScan();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
