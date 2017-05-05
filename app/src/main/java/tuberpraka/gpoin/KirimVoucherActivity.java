package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class KirimVoucherActivity extends AppCompatActivity {
String id_paket="", harga_jual="", harga_beli="", poin="", txtnama="", txtalamat="", txtkodepos="", txttlp="", txttrxpass="", paketitem="";


    EditText nama, alamat, kodepos, tlp, trxpass;
    Button btnpay;
    private RequestQueue queue;
    ProgressDialog loadingawal;
    BPJSActivity b;
    Dialog dialog;
    String secret_code="";
    LogConfig lg;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(KirimVoucherActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_kirim_voucher);

       id_paket = getIntent().getExtras().getString("id");
        harga_jual = getIntent().getExtras().getString("harga_jual");
        harga_beli = getIntent().getExtras().getString("harga_beli");
        poin = getIntent().getExtras().getString("poin");
        paketitem = getIntent().getExtras().getString("paketitem");

        nama =(EditText)findViewById(R.id.txtname);
        alamat = (EditText)findViewById(R.id.txtadd);
        kodepos =(EditText)findViewById(R.id.txtkodepos);
        tlp =(EditText)findViewById(R.id.txttlp);
        trxpass =(EditText)findViewById(R.id.trxpass);
        btnpay =(Button)findViewById(R.id.btnpay);

        queue = Volley.newRequestQueue(this);

        loadingawal = new ProgressDialog(KirimVoucherActivity.this);
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);

        b = new BPJSActivity();
        lg = new LogConfig();

btnpay.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(lg.session(getApplicationContext())) {
            if(nama.getText().toString().equals("")|| alamat.getText().toString().equals("") || kodepos.getText().toString().equals("")||
                    tlp.getText().toString().equals("") || trxpass.getText().toString().equals("")){

            }else{
                txtnama = nama.getText().toString();
                txtalamat= alamat.getText().toString();
                txtkodepos=kodepos.getText().toString();
                txttlp=tlp.getText().toString();
                txttrxpass=trxpass.getText().toString();


               kode_rahasia();
            }
        }else{
            finish();
        }




    }
});

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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KirimVoucherActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KirimVoucherActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                // ui = 0;
                                                KirimVoucherActivity.this.finish();
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


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(KirimVoucherActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
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
Log.e("Response",sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0")+", "+txtnama+", "+txtalamat+", "+txtkodepos+", " +
        txttlp+", "+b.md5(txttrxpass)+", "+id_paket+", "+harga_jual+", "+harga_beli+", "+poin);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"));
                params.put("nama", txtnama);
                params.put("alamat", txtalamat);
                params.put("kodepos", txtkodepos);
                params.put("telp", txttlp);
                params.put("trxpass", b.md5(txttrxpass));
                params.put("id_paket", id_paket);
                params.put("harga_jual", harga_jual);
                params.put("harga_beli", harga_beli);
                params.put("kode_rahasia", secret_code);
                params.put("poin", poin);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void kode_rahasia(){
        dialog = new Dialog(KirimVoucherActivity.this);
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
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(KirimVoucherActivity.this);
                    builder.setMessage("Your Data :\n"+ "Package    :"+paketitem+"("+poin+")\nNama   :"+txtnama+"\nAlamat    :"+txtalamat+"\nZipcode :"+txtkodepos+"\nTelephone"
                            +txttlp)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    loadingawal = ProgressDialog.show(KirimVoucherActivity.this, "Process", "Wait...", false, false);
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

}
