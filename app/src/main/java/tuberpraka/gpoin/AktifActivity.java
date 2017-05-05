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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AktifActivity extends AppCompatActivity {
    private RequestQueue queue;
    SharedPreferences sharedPreferences;

    String id_imei="", kode_rhs="", email="", trxpass="";
    double poin=0;

    TextView txtpoin,txtcs;
    Button btnaktif, btntopup;
    ProgressDialog loadingawal;
    BPJSActivity b;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(AktifActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_aktif);

        txtpoin =(TextView)findViewById(R.id.txtpoinawal);
        txtcs =(TextView)findViewById(R.id.txtcs);
        btnaktif =(Button)findViewById(R.id.btnaktif);
        btntopup =(Button)findViewById(R.id.btntopup);

//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
//        loadingawal = ProgressDialog.show(AktifActivity.this, "Proses...", "Wait...", false, false);
        queue = Volley.newRequestQueue(this);
        b = new BPJSActivity();
//        Kode();
        cek_kode();
        saldo();
        cs();


        btnaktif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogConfig lg = new LogConfig();
                if(lg.session(getApplicationContext())) {
                    aktif();
                }
//                loadingawal = ProgressDialog.show(AktifActivity.this, "Proses...", "Wait...", false, false);
            }
        });

        btntopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogConfig lg = new LogConfig();
                if(lg.session(getApplicationContext())) {
                    Intent intent = new Intent(AktifActivity.this, TopupActivity.class);

                    startActivity(intent);
                }

            }
        });
    }

    public void saldo(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                        if(response.equals("false")) {
                            poin =0;
                            txtpoin.setText("0 Poin");
                        }else{
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    poin =jsonArray.getJSONObject(i).getDouble("poin");
                                    if(jsonArray.getJSONObject(i).getString("status").equals("1")){
                                        txtpoin.setText(poin+"Poin");
                                    }else{
                                        Intent in = new Intent(AktifActivity.this, MainPanel.class);
                                        AktifActivity.this.finish();
                                        startActivity(in);
                                    }
                                }
//                                    if(loadingawal.isShowing()){
//                                        loadingawal.dismiss();
//                                    }
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
                        Log.d("Error.Response", "error");
                        Toast.makeText(AktifActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void aktif(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_aktif,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")) {
                            Toast.makeText(AktifActivity.this, "Your Point Is Not Enough" , Toast.LENGTH_SHORT).show();
                        }else{

                                        Intent in = new Intent(AktifActivity.this, MainPanel.class);
                                        AktifActivity.this.finish();
                                        startActivity(in);

                        }
//                        if(loadingawal.isShowing()){
//                            loadingawal.dismiss();
//                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "error");
                        Toast.makeText(AktifActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menua ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_back:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    public void logout(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AktifActivity.this);

        builder.setMessage("Are you sure logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        Intent intent = new Intent(AktifActivity.this, MainActivity.class);
                        AktifActivity.this.finish();
                        startActivity(intent);

                        SharedPreferences sharedPreferences = AktifActivity.this.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, false);
                        editor.putString(LogConfig.PASS_SESSION, "");
                        editor.putString(LogConfig.ID_IMEI_SESSION, "");
                        editor.putString(LogConfig.EMAIL_SESSION, "");
                        editor.putString(LogConfig.USERNAME_SESSION, "");
                        editor.putString(LogConfig.STAT_REG, "");
                        editor.apply();
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

    public void cs(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_list_cs,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){

                            Toast.makeText(AktifActivity.this, "Sorry, no data found" , Toast.LENGTH_SHORT).show();
                        }else{
                            JSONObject jo = null;
                            try {
//                                    jo = new JSONObject(response);

                                JSONArray jsonArray = new JSONArray(response);

//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < jsonArray.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
//                                        categories.add();
                                        txtcs.setText(txtcs.getText()+"\n"+jsonArray.getJSONObject(i).getString("jenis")+
                                                " : "+  jsonArray.getJSONObject(i).getString("nomor"));

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(AktifActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AktifActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        saldo();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            lg();
        }
    }
    public void lg(){
        Intent intent = new Intent(AktifActivity.this, MainActivity.class);
        AktifActivity.this.finish();
        startActivity(intent);

        SharedPreferences sharedPreferences = AktifActivity.this.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, false);
        editor.putString(LogConfig.PASS_SESSION, "");
        editor.putString(LogConfig.ID_IMEI_SESSION, "");
        editor.putString(LogConfig.EMAIL_SESSION, "");
        editor.putString(LogConfig.USERNAME_SESSION, "");
        editor.putString(LogConfig.STAT_REG, "");
        editor.apply();
    }

//    public void Kode(){
//        dialog = new Dialog(AktifActivity.this);
//        dialog.setContentView(R.layout.layout_kode);
//
//        final EditText txtkode = (EditText) dialog.findViewById(R.id.txtkode);
//        final EditText txtemail = (EditText) dialog.findViewById(R.id.txtemail);
//
//        Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);
//
//        btntrxpass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(txtkode.getText().toString().equals("") || txtemail.getText().toString().equals("")){
//                    Toast.makeText(AktifActivity.this, "Data must be filled",Toast.LENGTH_SHORT).show();
//                }else{
////                    edittrxpass(passtrxlama.getText().toString(), passtrxbaru.getText().toString());
//                }
//            }
//        });
//
//        dialog.setTitle("Insert Secret Code");
//        dialog.setCancelable(false);
//        dialog.show();
//    }

    public void cek_kode(){
        loadingawal = ProgressDialog.show(AktifActivity.this, "Proses...", "Wait...", false, false);

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_kode,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response cek_kode", response);
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                        if(response.equals("true")){
                            if(loadingawal.isShowing()){
                                loadingawal.dismiss();
                            }
                        }else{
                            dialog = new Dialog(AktifActivity.this);
                            dialog.setContentView(R.layout.layout_kode);

                            final EditText txtemail = (EditText) dialog.findViewById(R.id.txtemail);
                            final EditText kode = (EditText) dialog.findViewById(R.id.passtrxbaru);
                            final EditText passtrxa = (EditText) dialog.findViewById(R.id.passtrx);

                            Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

                            btntrxpass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(txtemail.getText().toString().equals("") || kode.getText().toString().equals("") || passtrxa.getText().toString().equals("")){
                                        Toast.makeText(AktifActivity.this, "Data must be filled",Toast.LENGTH_SHORT).show();
                                    }else{
                                        email = txtemail.getText().toString();
                                        kode_rhs = kode.getText().toString();
                                        trxpass = passtrxa.getText().toString();
                                        Log.e("Response passtrx", trxpass);
                                        loadingawal = ProgressDialog.show(AktifActivity.this, "Proses...", "Wait...", false, false);
                                        insert_kode(trxpass);
                                    }
                                }
                            });

                            dialog.setTitle("Insert Secret Code");
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response cek_kode", error.toString());
                        Toast.makeText(AktifActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void insert_kode(final String trxpassa){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_insert_kode,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("status").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AktifActivity.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                AktifActivity.this.finish();
                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AktifActivity.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
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
                        Log.e("Error.Response insert", error.toString());
                        Toast.makeText(AktifActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Response passtrx a", trxpassa+", "+id_imei+", "+email+", "+kode_rhs);
                params.put("trxpass", b.md5(trxpassa));
                params.put("id_imei", id_imei);
                params.put("email", email);
                params.put("kode", kode_rhs);
                return params;
            }
        };
        queue.add(postRequest);

    }

}
