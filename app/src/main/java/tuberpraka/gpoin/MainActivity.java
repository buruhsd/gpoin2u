package tuberpraka.gpoin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111;
    Button btnlogin;
    Button  btnref;
    TextView btnreg,txtforgot;

    EditText txtusername,txtpass, txtec;
    Spinner id_bisnis, bahasa;
    String pass="", username="", id_member ="", id_imei="", type="", status="", nama="", txtbisnis="", link_logo ="", alias_menu="";
    TextView txtc,txt_news;
    String cekreg="1", bisnis ="";
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext;
ProgressDialog loadingawal;
    String txtidbisnis;
    int captcha=0;
    private RequestQueue queue;
    notif n;
    Dialog dialog;
    int socketTimeout = 30000;
    RetryPolicy policy;

//    BroadcastReceiver broadcastReceiver;
    private char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
String a="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar_login);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        setContentView(R.layout.activity_main);

//        id_bisnis = (Spinner) findViewById(R.id.spinner1);
        txtpass = (EditText) findViewById(R.id.txtpass);
        txtusername =(EditText) findViewById(R.id.txtusername);
        txtc = (TextView)findViewById(R.id.txtc);
        txtec = (EditText) findViewById(R.id.txtec);
        queue = Volley.newRequestQueue(this);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnreg =  (TextView) findViewById(R.id.btnreg);
        btnref = (Button) findViewById(R.id.imgref);
        txtforgot = (TextView)findViewById(R.id.txtforgot);
        txt_news = (TextView)findViewById(R.id.txt_news);
            n = new notif();
        loadingawal = new ProgressDialog(MainActivity.this);
        policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        bisnis = "KONVENSIONAL";
//        bisnis();
        ref();
cek_versi();


//        id_bisnis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Object item = parent.getItemAtPosition(position);
//                bisnis =  String.valueOf(item);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        btnref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref();

            }
        });



            bahasa = (Spinner) findViewById(R.id.spinner2);

            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.bahasa, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bahasa.setAdapter(adapter2);

        if (cek_status(this)) {
            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtec.getText().toString().equalsIgnoreCase(String.valueOf(a))) {
                       /* if (txtidbisnis.equals("Id Bisnis")) {
                            Toast.makeText(getApplicationContext(), "Pilih Id Bisnis Anda", Toast.LENGTH_SHORT).show();
                        } else {*/

                        loadingawal = ProgressDialog.show(MainActivity.this, "Process...", "Wait...", false, false);
                            pass = txtpass.getText().toString();
                            username = txtusername.getText().toString();
                            cekreg = "1";
                            imei();

                       // }
                    } else {
                        ref();
                        Toast.makeText(getApplicationContext(), "Invalid Captcha", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btnreg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                    MainActivity.this.finish();
                    startActivity(i);
                }
            });
         /*   id_bisnis.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            Object item = parent.getItemAtPosition(pos);
//                        paket=pos;
                            txtidbisnis = String.valueOf(item);
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });*/
        }else{

            Log.e("Main", "main");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You are not connected internet. Please, connect first")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();

                            MainActivity.this.finish();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
       txtforgot.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Pass();
           }
       });

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
            Toast.makeText(MainActivity.this, e.toString()+"md5",Toast.LENGTH_SHORT).show();
            if(loadingawal.isShowing()){
                loadingawal.dismiss();
            }
//            bahaya(e.toString());
        }
        return "";
    }

    public void login(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_login,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                        if(response.equals("false")){
                            Toast.makeText(MainActivity.this, "Wrong username or password" , Toast.LENGTH_SHORT).show();
                                ref();
                            if(loadingawal.isShowing()){
                                loadingawal.dismiss();
                            }

                        }else {
                            JSONArray jsonArr = null;
                            try {
                                jsonArr = new JSONArray(response);

                            for(int a =0; a< jsonArr.length(); a++){
                               id_member= jsonArr.getJSONObject(a).getString("id_user");
                                id_imei = jsonArr.getJSONObject(a).getString("id_imei");
                                status = jsonArr.getJSONObject(a).getString("stat");
                                nama =jsonArr.getJSONObject(a).getString("namaacc");
                                txtbisnis = jsonArr.getJSONObject(a).getString("bisnis");
                                link_logo= jsonArr.getJSONObject(a).getString("logo");
                                alias_menu = jsonArr.getJSONObject(a).getString("alias_menu");
                            }

                            TambahSession();
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(MainActivity.this, e.toString()+"login",Toast.LENGTH_SHORT).show();
                                if(loadingawal.isShowing()){
                                    loadingawal.dismiss();
                                }
                            }
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Main", error.toString());

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("You are not connected internet. Please, connect first")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();

                                        MainActivity.this.finish();

                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
//                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                Map<String, String>  params = new HashMap<String, String>();
//                Log.e()
                params.put("username",username);
                params.put("password", md5(pass));
                params.put("pass", pass);
                params.put("imei", id_imei);
                params.put("type", type);
                params.put("kode", bisnis);


                return params;
            }
        };
        queue.add(postRequest);
        postRequest.setRetryPolicy(policy);
//        AppController.getInstance().addToRequestQueue(postRequest);
    }
    public void TambahSession(){
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = s.format(new Date());
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, true);
        editor.putString(LogConfig.PASS_SESSION, pass);
        editor.putString(LogConfig.USERNAME_SESSION, id_member);
        editor.putString(LogConfig.ID_IMEI_SESSION, id_imei);
        editor.putString(LogConfig.NAMAACC_SESSION, nama);
        editor.putString(LogConfig.NAMA_SESSION, username);
        editor.putString(LogConfig.LOGO, link_logo);
        editor.putString(LogConfig.ID_BISNIS, txtbisnis);
        editor.putString(LogConfig.STAT_REG, cekreg);
        editor.putString(LogConfig.SESSION_TIME, format);
        editor.putString(LogConfig.ALIAS_MENU, alias_menu);
        editor.apply();
        Intent i1 = new Intent(getApplicationContext(),RoosterConnectionService.class);
        startService(i1);
if(status.equals("1")) {
    Intent i = new Intent(MainActivity.this, AktifActivity.class);
    MainActivity.this.finish();
    startActivity(i);
}else if(status.equals("2")){
    Intent i = new Intent(MainActivity.this, MainPanel.class);
    MainActivity.this.finish();
    startActivity(i);
}

        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action)
                {
                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG,"Got a broadcast to show the main app window");
                        //Show the main app window
//                        showProgress(false);
//                        Intent i2 = new Intent(mContext,ContactListActivity.class);
//                        startActivity(i2);
//                        finish();n.

                        break;

                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
                        n.onNotifShow(body, MainActivity.class);
                                              return;
                }

            }
        };
        IntentFilter filter = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        registerReceiver(mBroadcastReceiver, filter);
        IntentFilter filter1 = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver,filter1);
    }

//    public void onPause() {
//        super.onPause();
////        if(mBroadcastReceiver.isOrderedBroadcast()) {
//            unregisterReceiver(mBroadcastReceiver);
////        }
//    }

    public boolean cek_status(Context cek) {
        ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void onStop(){
        super.onStop();
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch (IllegalArgumentException e) {
            Log.e("unregisterReceiver",e.toString());
        }
    }

    public void imei(){

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED  ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                id_imei = telephonyManager.getDeviceId();
                type = String.valueOf(telephonyManager.getPhoneType());
                login();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            id_imei = telephonyManager.getDeviceId();
            type = String.valueOf(telephonyManager.getPhoneType());
            login();
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
imei();

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

    public void ref(){
        Random r = new Random();
        captcha = r.nextInt(9999 - 1000 + 1) + 1000;
        Random c = new Random();
        char character = chars[c.nextInt(chars.length)];
        a = String.valueOf(captcha + "" + character);

        txtc.setText(a);
    }

    public void Pass(){
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.row_forgot);
        final EditText  txtemail, txtxuser;
        Button btnpass;
        String email,username;

        txtemail = (EditText) dialog.findViewById(R.id.txtemail);
        txtxuser = (EditText) dialog.findViewById(R.id.txtuser);

        btnpass = (Button) dialog.findViewById(R.id.btnsend);

        btnpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtemail.getText().toString().equals("") || txtxuser.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Data must be filled",Toast.LENGTH_SHORT).show();
                }else{
                    simpan_pass(txtemail.getText().toString(), txtxuser.getText().toString());
                    dialog.cancel();
                }
            }
        });
        dialog.setTitle("Forgot Password");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void simpan_pass(final String email, final String user){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_forgot,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(jo.getString("id"))
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
                            } catch (JSONException ex) {
                                Log.e("forgot",ex.toString());
                            }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Main simpan_pass", error.toString());

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("You are not connected internet. Please, connect first")
                                .setCancelable(false)
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();

                                        MainActivity.this.finish();

                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username",user);
                params.put("email", email);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void cek_versi(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_versi,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                        if(response.equals("false")){

                        }else {
                            txt_news.setText(response);

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "error");

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("versi",BuildConfig.VERSION_NAME);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void bisnis(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_bisnis,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                        if(response.equals("false")){
                            Toast.makeText(MainActivity.this, "Wrong Data" , Toast.LENGTH_SHORT).show();

                            if(loadingawal.isShowing()){
                                loadingawal.dismiss();
                            }

                        }else {

                            try {
                                JSONArray ja = new JSONArray(response);
                                ArrayList  categories = new ArrayList<String>();
                                for(int i = 0; i< ja.length();i++){
                                    categories.add(ja.getJSONObject(i).getString("nama"));
                                }

                            ArrayAdapter<String> adapter =  new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            id_bisnis.setAdapter(adapter);
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
                        Log.e("Main", error.toString());

                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("You are not connected internet. Please, connect first")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();

                                        MainActivity.this.finish();

                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username",username);
                return params;
            }
        };
        queue.add(postRequest);
        postRequest.setRetryPolicy(policy);
//        AppController.getInstance().addToRequestQueue(postRequest);
    }

}
