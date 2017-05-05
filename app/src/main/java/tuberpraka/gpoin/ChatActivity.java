package tuberpraka.gpoin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//import co.devcenter.androiduilibrary.ChatView;
//import co.devcenter.androiduilibrary.ChatViewEventListener;

public class ChatActivity extends AppCompatActivity {

    private RequestQueue queue;
    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;

    String username ="", id_imei="";
    ListChatAdapter adapter;
    ArrayList<Topup> data;
    PulsaActivity p;
    TextView txtDateto, txtDatefrom;
    private BroadcastReceiver mBroadcastReceiver;
    int hour, minute, mYear,mMonth, mDay;
    String datefrom="", dateto="";
    LogConfig lg;
    MainActivity m;
    private String[] arrMonth = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    static final int DATE_DIALOG_ID = 1;
    boolean cek=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m = new MainActivity();

        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(ChatActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_chat);

        recyclerView = (RecyclerView) findViewById(R.id.recycTupup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        username =sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0");
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        txtDatefrom = (TextView) findViewById(R.id.txtDatefrom);
        txtDateto = (TextView) findViewById(R.id.txtDateto);
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
       mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        lg = new LogConfig();
        queue = Volley.newRequestQueue(this);
        p = new PulsaActivity();
        cektombol("hasil");

        txtDatefrom.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View arg0, MotionEvent arg1) {

                if(lg.session(getApplicationContext())) {
                    cek = false;
                    showDialog(DATE_DIALOG_ID);


                }else{
                finish();
            }
                return true;
            }

        });

        txtDateto.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(lg.session(getApplicationContext())) {
                    cek = true;
                    showDialog(DATE_DIALOG_ID);


                }else{
                    finish();
                }
                return true;
            }

        });


    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {

            case DATE_DIALOG_ID:
                return new DatePickerDialog(
            this, mDateSetListener, mYear, mMonth, mDay);
        }

        return null;

    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()

    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
//        String sdate = arrMonth[mMonth] + " " +String.format("%2s", mDay).replace(' ', '0') + ", " + mYear;
            if(!cek) {
                datefrom = mYear + "-" + String.format("%2s", mMonth + 1).replace(' ', '0') + "-" + String.format("%2s", mDay).replace(' ', '0');
                txtDatefrom.setText(datefrom);

            }else{
                dateto = mYear + "-" + String.format("%2s", mMonth + 1).replace(' ', '0') + "-" + String.format("%2s", mDay).replace(' ', '0');
                txtDateto.setText(dateto);
            }

            if(dateto.equals("")||datefrom.equals("")){

            }else{

cektombol("search");
            }

    }

    };



    @Override
    protected void onResume() {
        super.onResume();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            finish();
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
//            i.putExtra("EXIT", true);
//            startActivity(i);
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
//                        txtketbpjs.setText(txtketbpjs.getText().toString()+body);
//                        txtketbpjs.setVisibility(View.VISIBLE);
//                        if(body.toUpperCase().contains("CEKBPJS".toUpperCase())){
//
////                        if(StringcontainsIgnoreCase(container, content))
//                            cek_convert(body);
//
//                        }

//                        String[] data = body.split(".");
////        for(int i =0; i<= data.length;i++ ){
//                        String tgl = data[0]+"0";
//                        String kodeproduk= data[1]+"1";
//                        String tujuan =data[2]+"2";
//                        String keterangan = data[3]+"3";
//                        final String harga = data[4]+"4";

//                        poinbpjs.setVisibility(View.VISIBLE);
//                        poinbpjs.setText(tgl+kodeproduk+tujuan+keterangan+harga+" Poin");
//                        double x=0;
//                        Toast.makeText(BPJSActivity.this, tgl+kodeproduk+tujuan+keterangan+harga,Toast.LENGTH_SHORT).show();
//                        n.onNotifShow(body,PlnActivity.class);
                        cektombol("hasil");
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

    public void hasil(){


            StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_hasil,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            if(response.equals("false")){

                                Toast.makeText(ChatActivity.this, "Sorry, no data found" , Toast.LENGTH_SHORT).show();
                            }else{
                                JSONObject jo = null;
                                try {
//                                    jo = new JSONObject(response);

                                JSONArray jsonArray = new JSONArray(response);
                                int count = 0;

                                data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String alasan =jsonArray.getJSONObject(i).getString("alasan");
                                        String harga = jsonArray.getJSONObject(i).getString("harga")+"Poin";
                                        String nohp = jsonArray.getJSONObject(i).getString("nohp");
                                        String ket = jsonArray.getJSONObject(i).getString("ket");
                                        String datea = jsonArray.getJSONObject(i).getString("date");
                                        String status = jsonArray.getJSONObject(i).getString("status");
                                        String id =  jsonArray.getJSONObject(i).getString("id");
                                        String stat_byr = jsonArray.getJSONObject(i).getString("stat_byr");

                                        if(alasan==null||alasan.equals(" ") ||alasan.equals("null")|| alasan.equals("")){
                                            alasan ="-";
                                        }if(harga==null || harga.equals(" ") || harga.equals("null")|| harga.equals("")){
                                            harga="-";
                                        }if(nohp==null || nohp.equals(" ") || nohp.equals("null")|| nohp.equals("")){
                                            nohp="-";
                                        }if(ket==null || ket.equals(" ")|| ket.equals("null")|| ket.equals("")){
                                            ket="-";
                                        }if(datea==null || datea.equals(" ")|| datea.equals("null")|| datea.equals("")){
                                            datea="-";
                                        }

                                        Topup topup = new Topup(alasan, harga,nohp,ket ,datea, status, id, stat_byr);
                                        data.add(topup);
                                    count++;
                                }

                                adapter = new ListChatAdapter(data, ChatActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ChatActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ChatActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void search(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_search,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){

                            Toast.makeText(ChatActivity.this, "Sorry, no data found" , Toast.LENGTH_SHORT).show();
                        }else{
                            JSONObject jo = null;
                            try {
//                                    jo = new JSONObject(response);

                                JSONArray jsonArray = new JSONArray(response);
                                int count = 0;

                                data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < jsonArray.length(); i++) {
//                               String nama=jsonArr.getJSONObject(i).getString("namaacc");
                                    String alasan =jsonArray.getJSONObject(i).getString("alasan");
                                    String harga = jsonArray.getJSONObject(i).getString("harga")+"Poin";
                                    String nohp = jsonArray.getJSONObject(i).getString("nohp");
                                    String ket = jsonArray.getJSONObject(i).getString("ket");
                                    String datea = jsonArray.getJSONObject(i).getString("date");
                                    String status = jsonArray.getJSONObject(i).getString("status");
                                    String id =  jsonArray.getJSONObject(i).getString("id");
                                    String stat_byr = jsonArray.getJSONObject(i).getString("stat_byr");

                                    if(alasan==null||alasan.equals(" ") ||alasan.equals("null")|| alasan.equals("")){
                                        alasan ="-";
                                    }if(harga==null || harga.equals(" ") || harga.equals("null")|| harga.equals("")){
                                        harga="-";
                                    }if(nohp==null || nohp.equals(" ") || nohp.equals("null")|| nohp.equals("")){
                                        nohp="-";
                                    }if(ket==null || ket.equals(" ")|| ket.equals("null")|| ket.equals("")){
                                        ket="-";
                                    }if(datea==null || datea.equals(" ")|| datea.equals("null")|| datea.equals("")){
                                        datea="-";
                                    }

                                    Topup topup = new Topup(alasan, harga,nohp,ket ,datea,status,id,stat_byr);
                                    data.add(topup);
                                    count++;
                                }

                                adapter = new ListChatAdapter(data, ChatActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ChatActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ChatActivity.this,"Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("username", username);
                params.put("date_from", datefrom);
                params.put("date_to", dateto);
                return params;
            }
        };
        queue.add(postRequest);

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

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                ChatActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("hasil")){
                                    hasil();
                                }else if(menu.equals("search")) {
                                    search();
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
                        Toast.makeText(ChatActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    }

