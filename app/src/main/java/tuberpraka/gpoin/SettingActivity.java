package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    EditText bank, acc, alamat,zipcode, passlama, passbaru, trxlama, trxbaru;
    TextView name;
    Spinner city,provinsi;
    Button save, trx, pass, btntrx, btnpass;
    LogConfig lg;
    private RequestQueue queue;
    SharedPreferences sharedPreferences;
    String id_imei="",js="", nama_prov="", password="", nama_kota="";
    ProgressDialog loadingawal;

    List<String> categories;
    List<String> categorieskota;

    BPJSActivity b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        lg = new LogConfig();
        bank = (EditText)findViewById(R.id.namabank);
        acc = (EditText)findViewById(R.id.noacc);
        alamat =(EditText)findViewById(R.id.address);
        zipcode=(EditText)findViewById(R.id.kodepos);
        name = (TextView)findViewById(R.id.nama);
        city =(Spinner)findViewById(R.id.spinkota);
        provinsi =(Spinner)findViewById(R.id.spinprovinsi);
        save =(Button)findViewById(R.id.btnsimpansetting);
        trx =(Button)findViewById(R.id.btnedittrx);
        pass =(Button)findViewById(R.id.btneditpass);

        b =new BPJSActivity();

        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        password = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        loadingawal = ProgressDialog.show(SettingActivity.this, "Proses...", "Wait...", false, false);
        queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        pass.setVisibility(View.GONE);
        trx.setVisibility(View.GONE);
        if(intent.getStringExtra("ket").equals("profil")){
            save.setVisibility(View.GONE);
            acc.setEnabled(false);
            bank.setEnabled(false);
            alamat.setEnabled(false);
            zipcode.setEnabled(false);
            city.setEnabled(false);
            provinsi.setEnabled(false);
//            acc.setInputType(View.NONE);
        }else{
            save.setVisibility(View.VISIBLE);
        }
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                    cektombol("pass");
                }else{
                    finish();
                }
            }
        });

        trx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                    cektombol("trx");
                }else{
                    finish();
                }
            }
        });
        cektombol("data");

        provinsi.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        nama_prov = String.valueOf(item);
//                        Toast.makeText(SettingActivity.this, nama_prov , Toast.LENGTH_SHORT).show();
                        ambilkota();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        city.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        nama_kota = String.valueOf(item);

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                    cektombol("simpan");
                }
            }
        });

    }

    public void ambilprovinsi(){
//        Toast.makeText(SettingActivity.this, "ambilprovinsi" , Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.provinsi,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        js =response;
                        if(response.equals("false")){

                        }else {
                            try {
                                JSONArray jsonArr = new JSONArray(response);
                                categories = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                                String prov = "";
                                for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                    categories.add(jsonArr.getJSONObject(i).getString("name"));
                                    if(nama_prov.equals(jsonArr.getJSONObject(i).getString("name"))){
                                        provinsi.setSelection(i);
                                    }

                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_spinner_item, categories);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                provinsi.setAdapter(spinnerAdapter);

                                for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                    categories.add(jsonArr.getJSONObject(i).getString("name"));
                                    if(nama_prov.equals(jsonArr.getJSONObject(i).getString("name"))){
                                        provinsi.setSelection(i);
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SettingActivity.this, e.toString() + "provinsi", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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


    public void ambilkota(){

        String id_prov="";
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(js);
            for (int i = 0; i < jsonArr.length(); i++) {
                if(jsonArr.getJSONObject(i).getString("name").equals(nama_prov)){
                    id_prov =jsonArr.getJSONObject(i).getString("nid");
//                    Toast.makeText(SettingActivity.this, id_prov+"kota1" , Toast.LENGTH_SHORT).show();

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SettingActivity.this, e.toString()+"kota1" , Toast.LENGTH_SHORT).show();
        }


        final String finalId_prov = id_prov;
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.kota,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        js =response;
                        try {
                            JSONArray jsonArr = new JSONArray(response);
                            categorieskota  = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                            String prov = "";
                            for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");

                                categorieskota.add(jsonArr.getJSONObject(i).getString("name"));

                                if(nama_kota.equals(jsonArr.getJSONObject(i).getString("name"))){
                                    city.setSelection(i);
                                }

                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_spinner_item, categorieskota);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            city.setAdapter(spinnerAdapter);

                            for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");

                                categorieskota.add(jsonArr.getJSONObject(i).getString("name"));

                                if(nama_kota.equals(jsonArr.getJSONObject(i).getString("name"))){
                                    city.setSelection(i);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("provinsi", finalId_prov);


                return params;
            }
        };
        queue.add(postRequest);
    }

    public void cekdata(){
//        Toast.makeText(SettingActivity.this, "cekdata" , Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.profil,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        js =response;
                        if(response.equals("false")) {
                            Toast.makeText(SettingActivity.this,"no data found", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                JSONArray jsonArr = new JSONArray(response);

                                for (int i = 0; i < jsonArr.length(); i++) {
                                    String nama=jsonArr.getJSONObject(i).getString("namaacc");
                                    String noacc =jsonArr.getJSONObject(i).getString("noacc");
                                   String ala = jsonArr.getJSONObject(i).getString("alamat");
                                    String zip = jsonArr.getJSONObject(i).getString("zipcode");
                                    nama_prov = jsonArr.getJSONObject(i).getString("provinsi");
                                    String bankname = jsonArr.getJSONObject(i).getString("bankname");
                                    nama_kota = jsonArr.getJSONObject(i).getString("city");


                                    if(nama.equals("null")){
                                       nama ="";
                                    }if(noacc.equals("null")){
                                        noacc="";
                                    }if(ala.equals("null")){
                                        ala="";
                                    }if(zip.equals("null")){
                                        zip="";
                                    }if(bankname.equals("null")){

                                    }
                                    name.setText(nama);
                                    acc.setText(noacc);
                                    alamat.setText(ala);
                                    zipcode.setText(zip);
                                    bank.setText(bankname);

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SettingActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                            }

                            ambilprovinsi();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
                params.put("pass", b.md5(password));


                return params;
            }
        };
        queue.add(postRequest);

        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }
    }

    public void simpan(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.save_profil,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        cekdata();
//                        if(loadingawal.isShowing()){
//                            loadingawal.dismiss();
//                        }

                        Toast.makeText(SettingActivity.this, "Sucess" , Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("provinsi", nama_prov);
                params.put("city", nama_kota);
                params.put("address", alamat.getText().toString());
                params.put("zipcode", zipcode.getText().toString());
                params.put("bank", bank.getText().toString());
                params.put("noacc", acc.getText().toString());
                params.put("pass", b.md5(password));
                params.put("id_imei", id_imei);


                return params;
            }
        };
        queue.add(postRequest);
    }

    public void Pass(){
        Dialog dialog = new Dialog(SettingActivity.this);
        dialog.setContentView(R.layout.layout_pass);

        passlama = (EditText) dialog.findViewById(R.id.passlama);
        passbaru = (EditText) dialog.findViewById(R.id.passbaru);

        btnpass = (Button) dialog.findViewById(R.id.btnepass);

btnpass.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(passlama.getText().toString().equals("") || passbaru.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
        }else{
editpass(passlama.getText().toString(), passbaru.getText().toString());
        }
    }
});
        dialog.setTitle("Edit Password");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void editpass(final String lama, final String baru){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_pass,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){
                            Pass();
                            Toast.makeText(getApplicationContext(), "Wrong data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lama", b.md5(lama));
                params.put("baru", b.md5(baru));
                params.put("ps", baru);
                params.put("id_imei", id_imei);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void TrxPass(){
        Dialog dialog = new Dialog(SettingActivity.this);
        dialog.setContentView(R.layout.layout_trxpass);

        final EditText passtrxlama = (EditText) dialog.findViewById(R.id.passtrxlama);
        final EditText passtrxbaru = (EditText) dialog.findViewById(R.id.passtrxbaru);

        Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

        btntrxpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passtrxlama.getText().toString().equals("") || passtrxbaru.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Data must be filled",Toast.LENGTH_SHORT).show();
                }else{
                    edittrxpass(passtrxlama.getText().toString(), passtrxbaru.getText().toString());
                }
            }
        });

        dialog.setTitle("Edit Trx Password");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void edittrxpass(final String lama, final String baru){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_trxpass,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){
                            TrxPass();
                            Toast.makeText(getApplicationContext(), "Wrong data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString() );
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lama", b.md5(lama));
                params.put("baru", b.md5(baru));
                params.put("pt", baru);
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
        RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                SettingActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("data")){
                                   cekdata();
                                }else if(menu.equals("simpan")) {
                                    simpan();
                                }else if(menu.equals("pass")){
                                    Pass();
                                }else if(menu.equals("trx")){
                                    TrxPass();
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
                        Toast.makeText(SettingActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
}
