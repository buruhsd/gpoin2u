package tuberpraka.gpoin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import android.database.Cursor;


import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class TopupActivity extends AppCompatActivity {
EditText txtsaldoTopup, txtan;
    LogConfig lg;
    Button btnSubmitTopup, btnup, btnSubmitCC;
    boolean tombol=false;
    Spinner konfirmbank;
    String saldo="", param="", id_imei="",atasnama="", databank="", password="", passtrx="",  harga="", kode_unik="";
    String saldoTopup="";
    TextView txtket, txtketjelas;
    LinearLayout ln;
    List<String> categories;
    ProgressDialog loading;
    private RequestQueue queue;
    Dialog dialog;
    ImageView imageView;
    RecyclerView recyclerView;
    ListTopupAdapter adapter;
    ArrayList<Topup> data;

    TableRow tr_headtopup;
    TableLayout tltopup;
    TextView kltanggal,kltopup, klstatus, klprice;
    BPJSActivity b ;

    //variable for upload data into http
    HttpURLConnection connection = null;
    public static final String TAG = "Upload Image";

    // I am using my local server for uploading image, you should replace it with your server address


    public static final String UPLOAD_KEY = "upload_image";
    public final static String MESSAGE_KEY = "tuberpraka.gpoin.meesage_key";

    private int PICK_IMAGE_REQUEST = 100;

    private Button btnSelect, btnUpload;
    private TextView txtStatus;

    private ImageView imgView;

    private Bitmap bitmap;

    private Uri filePath;
    SharedPreferences sharedPreferences;
    private String selectedFilePath, js="", paket="";

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

            Glide.with(TopupActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_topup);
        lg = new LogConfig();
        txtsaldoTopup = (EditText) findViewById(R.id.txttopup);
        btnSubmitTopup = (Button) findViewById(R.id.btnSubmitTopup);
        konfirmbank =(Spinner)findViewById(R.id.konfirmbank);
        txtan =(EditText)findViewById(R.id.passtrx);
        txtket =(TextView)findViewById(R.id.txtketBayar);
        txtketjelas =(TextView)findViewById(R.id.txtket);
        ln= (LinearLayout) findViewById(R.id.layout_paneltopup) ;
        btnup =(Button)findViewById(R.id.btnup);
        recyclerView = (RecyclerView) findViewById(R.id.bgp);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //via cc
        btnSubmitCC = (Button) findViewById(R.id.btnSubmitCC);

        btnSubmitCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amt = txtsaldoTopup.getText().toString();
                Log.d("HARUS SAMA", amt);
                Intent intent = new Intent(TopupActivity.this, TopupCCActivity.class );
                intent.putExtra(MESSAGE_KEY, amt);
                startActivity(intent);
            }
        });

//        ln.setVisibility(View.GONE);
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//
//        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0");
        password = sharedPreferences.getString(LogConfig.PASS_SESSION, "0");
        queue = Volley.newRequestQueue(this);

        b = new BPJSActivity();

        cek_status();
        bank();
        SelectAllTopup();

//        SelectAllTopup st = new SelectAllTopup();
//        st.execute();


//        tltopup = (TableLayout) findViewById(R.id.tbtopup);
//
//        tr_headtopup = new TableRow(this);
//        tr_headtopup.setLayoutParams(new TableRow.LayoutParams(
//                TableRow.LayoutParams.WRAP_CONTENT,
//                TableRow.LayoutParams.WRAP_CONTENT));
//
//        kltanggal = new TextView(this);
//        kltanggal.setText("Date");
//        kltanggal.setTextColor(Color.BLACK);
//        kltanggal.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        kltanggal.setPadding(10, 10, 10, 10);
//        kltanggal.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//        tr_headtopup.addView(kltanggal); // Adding textView to tablerow.
//
//        kltopup = new TextView(this);
//        kltopup.setText("G Poin");
//        kltopup.setTextColor(Color.BLACK);
//        kltopup.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//        kltopup.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        kltopup.setPadding(10, 10, 10, 10);
//        tr_headtopup.addView(kltopup);  // Adding textView to tablerow.
//
//        /** Creating another textview **/
//        klstatus = new TextView(this);
//        klstatus.setText("Status");
//        klstatus.setTextColor(Color.BLACK);
//        klstatus.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        klstatus.setPadding(10, 10, 10, 10);
//        klstatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//        tr_headtopup.addView(klstatus); // Adding textView to tablerow.
//
//        /** Creating another textview **/
//        klprice = new TextView(this);
//        klprice.setText("Price");
//        klprice.setTextColor(Color.BLACK);
//        klprice.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        klprice.setPadding(10, 10, 10, 10);
//        klprice.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//        tr_headtopup.addView(klprice); // Adding textView to tablerow.
//
//
//        /** Creating another textview **/
//
//        tltopup.addView(tr_headtopup, new TableRow.LayoutParams(
//                TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.WRAP_CONTENT));


//        cekTransakasi ck = new cekTransakasi();
//        ck.execute();
//        if(!tombol) {
//            //tombol false konfirmasi
//            konfirmbank.setVisibility(View.GONE);
//            txtan.setVisibility(View.GONE);
//        }else {
//            //tombol true untuk pesan
//            txtsaldoTopup.getText().toString();
//            btnSubmitTopup.setText("Konfirmasi");
//            konfirmbank.setVisibility(View.VISIBLE);
//            txtan.setVisibility(View.VISIBLE);
//        }
        konfirmbank.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);
                        paket = String.valueOf(item);
                        pecahjson();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        btnSubmitTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lg.session(getApplicationContext())) {
                    try {
                        //tombol false konfirmasi
                        saldoTopup = txtsaldoTopup.getText().toString();
                        passtrx= txtan.getText().toString();
                        txtan.setText("");
                        txtsaldoTopup.setText("");
                        Log.e("saldo", saldoTopup);
                        tombol = true;
                        if (saldoTopup.isEmpty() || saldoTopup.equalsIgnoreCase(" ") || saldoTopup.equals("")) {
                            txtket.setText("Fill the point coloumn");
                        } else {
                            txtket.setText("Poin =" + saldoTopup);
                        if (Integer.parseInt(saldoTopup) % 10 == 0) {

                           cek_convert();

                        } else {
                            txtket.setText("Minimum purchase is 10 points and multiples of 10 points");
                        }

                        }
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        txtket.setText("Fill the point coloumn with number");
                    }
                }else{
                    finish();
                }
            }
        });

        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Pass();
            }
        });


    }









        public void SelectAllTopup() {


            StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_selectall_topup,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String status = "";
                            try {


                                JSONObject jo = new JSONObject(response);
//                            if(jo.getString("sukses").equals("false")){
                                if(jo.getString("sukses").equals("false")){
                                     Toast.makeText(TopupActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                                }else {


                                        JSONArray jsonArray = new JSONArray(jo.getString("catatan"));
                                        int count = 0;
                                        String alasan = null;
                                        data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
//                                        categories.add();

                                            if(jsonArray.getJSONObject(i).getInt("status")==1){
                                                status = "New";
                                                alasan ="click here for confirmation";
                                            }else if(jsonArray.getJSONObject(i).getInt("status")==2){
                                                status ="Wait";
                                                alasan ="Wait for a confirmation";
                                            }else if(jsonArray.getJSONObject(i).getInt("status")==3){
                                                status="Accept";
                                                alasan ="Request accepted";
                                            }else if(jsonArray.getJSONObject(i).getInt("status")==4){
                                                status ="Reject";
                                                alasan ="Request rejected";
                                            }
                                        String total = String.valueOf( jsonArray.getJSONObject(i).getInt("harga")+ jsonArray.getJSONObject(i).getInt("kode_unik"));
                                            Topup topup = new Topup(alasan,
                                                    total,jsonArray.getJSONObject(i).getString("id")
                                                    ,jsonArray.getJSONObject(i).get("poin").toString()+" Poin" ,jsonArray.getJSONObject(i).getString("tgl_beli"),"", "","");
                                            data.add(topup);
                                            count++;
                                        }

                                        adapter = new ListTopupAdapter(data, TopupActivity.this);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                }

                                // loading.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TopupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
//           bahaya("6");

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.e("Error.Response", error.toString());
                            Toast.makeText(TopupActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {         // Menambahkan parameters post
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("id_imei", id_imei);
                    params.put("pass", b.md5(password));

                    return params;
                }
            };
            queue.add(postRequest);


        }


    public void onBackPressed(){
        super.onBackPressed();

        TopupActivity.this.finish();

    }

    public void bahaya(String s){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Invalid Connection"+s)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        Intent intent = new Intent(TopupActivity.this
                                ,
                                MainPanel.class);
                        TopupActivity.this.finish();
                        startActivity(intent);
                    }
                })
        ;
        final android.app.AlertDialog alert = builder.create();
        alert.show();
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


    public void cek_status(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_status,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(TopupActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
                params.put("pass", password);


                return params;
            }
        };
        queue.add(postRequest);
    }

    public void cek_convert(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert_topup,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TopupActivity.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                dialog.cancel();
                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
passtrx="";
                            }else {
                                harga = jo.getString("harga");
                                kode_unik = jo.getString("kode_unik");
                                double total = Double.valueOf(harga)+Double.valueOf(kode_unik);
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TopupActivity.this);

                                builder.setMessage("Are you sure to buy "+saldoTopup+" poin, price = "+total+" ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            simpan();


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


                            // loading.dismiss();
                        } catch (JSONException e) {
                            bahaya(e.toString());
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(TopupActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
                params.put("passtrx", b.md5(passtrx));
                params.put("poin", saldoTopup);


                return params;
            }
        };
        queue.add(postRequest);
    }
    public void simpan(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_beli_topup,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("sukses").equals("false")){
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TopupActivity.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                dialog.cancel();
                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }else {
                                txtket.setText(jo.getString("catatan"));
                                txtsaldoTopup.setText("");
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TopupActivity.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                                dialog.cancel();

                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();


                            }

                            SelectAllTopup();
                            passtrx="";
                            // loading.dismiss();
                        } catch (JSONException e) {
                            bahaya(e.toString());
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(TopupActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
                params.put("passtrx", b.md5(passtrx));
                params.put("poin", saldoTopup);
                params.put("harga", harga);
                params.put("kode_unik", kode_unik);


                return params;
            }
        };
        queue.add(postRequest);
    }

    public void bank(){
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_bank_topup,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(TopupActivity.this, response , Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("sukses").toUpperCase().equals("FALSE")){

                            }else {
                                JSONArray jsonArr = new JSONArray(jo.getString("catatan"));
                                js = jo.getString("catatan");
                                categories = new ArrayList<String>();

                                String prov = "";
                                for (int i = 0; i < jsonArr.length(); i++) {

//                                    jsonArr.getJSONObject(i).getDouble("lat_from");
//                                    prov = jsonArr.getJSONObject(i).getString("nama");
//                                    categories.add(jsonArr.getJSONObject(i).getString("bank")+" a.n "+jsonArr.getJSONObject(i).getString("atasnama")+" no.rek "+ jsonArr.getJSONObject(i).getString("no_rek"));
                                    categories.add(jsonArr.getJSONObject(i).getString("bank"));
                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(TopupActivity.this, android.R.layout.simple_spinner_item, categories);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                konfirmbank.setAdapter(spinnerAdapter);

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
                        Toast.makeText(TopupActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

//    public void Pass(){
//        dialog = new Dialog(TopupActivity.this);
//        dialog.setContentView(R.layout.upload);
//        Button buttonChoose, send;
//        imgView  = (ImageView) dialog.findViewById(R.id.imga);
//        buttonChoose = (Button) dialog.findViewById(R.id.buttonChoose);
//        send = (Button) dialog.findViewById(R.id.btnkirim);
//
//       buttonChoose.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               showFileChooser();
//           }
//       });
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadImage();
//            }
//        });
//        dialog.setTitle("Upload Bukti Pembayaran");
//        dialog.setCancelable(true);
//        dialog.show();
//    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            selectedFilePath = getPath(filePath);
            Log.e(TAG, " File path : " + selectedFilePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public String getPath(Uri uri) {
        Log.e(TAG, "getPath ");
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    Handler handler = handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "Handler " + msg.what);
            if (msg.what == 1) {
                txtket.setText("Upload Success");
            } else {
                txtket.setText("Upload Error");
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            finish();
        }
            SelectAllTopup();
    }

    public void pecahjson(){
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(js);

        categories = new ArrayList<String>();

        String prov = "";
        for (int i = 0; i < jsonArr.length(); i++) {
if(jsonArr.getJSONObject(i).getString("bank").equalsIgnoreCase(paket)) {
    txtketjelas.setText(" A.n " + jsonArr.getJSONObject(i).getString("atasnama") + "\n No.rek " + jsonArr.getJSONObject(i).getString("no_rek"));

}
        }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
