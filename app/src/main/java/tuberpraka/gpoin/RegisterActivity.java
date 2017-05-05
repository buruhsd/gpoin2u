package tuberpraka.gpoin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
Button reg;
    EditText txtusername, txtpass, txthp, txtemail, txttrxpass, txtnama,txtkode;
    int type=0;
    String imei="",username ="", pass="", email="", nohp="", nama="", id="", passtrx="", id_imei="", kode="";

    private RequestQueue queue;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg = (Button) findViewById(R.id.btnreg);
        txtusername = (EditText) findViewById(R.id.txtuserreg);
        txtemail = (EditText) findViewById(R.id.txtemailreg);
        txthp = (EditText) findViewById(R.id.txthpreg);
        txtpass =(EditText) findViewById(R.id.txtpassreg);
        txtnama =(EditText) findViewById(R.id.txtname);
        txttrxpass =(EditText)findViewById(R.id.txtpasstrx);
        txtkode =(EditText)findViewById(R.id.txtkode);
        queue = Volley.newRequestQueue(this);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = txtusername.getText().toString();
                pass = txtpass.getText().toString();
                nohp = txthp.getText().toString();
                email= txtemail.getText().toString();
                passtrx = txttrxpass.getText().toString();
                nama = txtnama.getText().toString();
                kode = txtkode.getText().toString();
                if(username.equals("") || pass.equals("")||nohp.equals("")||email.equals("") || nama.equals("") || kode.equals("")){
                    Toast.makeText(RegisterActivity.this, "You must be complite the data", Toast.LENGTH_SHORT).show();

                }else if (passtrx.equalsIgnoreCase(pass)){
                    imei();
                }else{
                    Toast.makeText(RegisterActivity.this, "Confirm Password didn't match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void imei(){

        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED  ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                id_imei = telephonyManager.getDeviceId();
                type = telephonyManager.getPhoneType();
                regis();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            id_imei = telephonyManager.getDeviceId();
            type = telephonyManager.getPhoneType();
            regis();
        }

    }
    public void regis(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_register,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);
//                        Toast.makeText(RegisterActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
//                        if(response.equals("true")){
//                            TambahSession();
//
//                        }
                        try {
                            JSONObject js = new JSONObject(response);
                            boolean cek = js.getBoolean("sukses");
                            if(cek){
                                id = js.getString("id");
                                id_imei = js.getString("id_imei");
                               // Toast.makeText(RegisterActivity.this, id_imei , Toast.LENGTH_SHORT).show();
                                TambahSession();
                            }else{
                                Toast.makeText(RegisterActivity.this, js.getString("id"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(RegisterActivity.this, "Connection Invalid" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username",username);
                params.put("password", md5(pass));
                params.put("pass", pass);
                params.put("pt", pass);
                params.put("hp", nohp);
                params.put("email", email);
                params.put("imei", id_imei);
                params.put("nama", nama);
                params.put("passtrx", md5(pass));
                params.put("type", String.valueOf(type));
                params.put("kode", kode);
//                Toast.makeText(RegisterActivity.this, username+","+pass+","+nohp+","+email , Toast.LENGTH_SHORT).show();
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void TambahSession(){
        SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, true);
        editor.putString(LogConfig.PASS_SESSION, pass);
        editor.putString(LogConfig.NAMA_SESSION, nama);
        editor.putString(LogConfig.ID_IMEI_SESSION, id_imei);
        editor.putString(LogConfig.USERNAME_SESSION, id);
        editor.putString(LogConfig.NAMA_SESSION, username);
        editor.putString(LogConfig.EMAIL_SESSION, email);
        editor.putString(LogConfig.STAT_REG, "2");
        editor.apply();
//        Toast.makeText(RegisterActivity.this, "masuk" , Toast.LENGTH_SHORT).show();
        Intent i1 = new Intent(getApplicationContext(),RoosterConnectionService.class);
        startService(i1);
        Intent i = new Intent(RegisterActivity.this, MainPanel.class);
        RegisterActivity.this.finish();
        startActivity(i);

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

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



}
