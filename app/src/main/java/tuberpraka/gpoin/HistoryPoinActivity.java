package tuberpraka.gpoin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryPoinActivity extends AppCompatActivity {
    private RequestQueue queue;
    RecyclerView recyclerView;
    LogConfig lg;
    SharedPreferences sharedPreferences;

    String username = "", id_imei = "", pass="";
    ListCsAdapter adapter;
    ArrayList<csClass> data;
    PulsaActivity p;
    List<String> categories;
    TextView txtDateto, txtDatefrom;
    private BroadcastReceiver mBroadcastReceiver;
    int hour, minute, mYear, mMonth, mDay;
    String datefrom = "", dateto = "";
    RadioButton day, month;
    RadioGroup rgroup;
    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    static final int DATE_DIALOG_ID = 1;
    boolean cek = false;
    BPJSActivity b;
    Spinner spinmonth;
    String bulan="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_poin);
        recyclerView = (RecyclerView) findViewById(R.id.recycTupup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LogConfig.USERNAME_SESSION, "0");
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION, "0");
        txtDatefrom = (TextView) findViewById(R.id.txtDatefrom);
        txtDateto = (TextView) findViewById(R.id.txtDateto);
        spinmonth =(Spinner)findViewById(R.id.spinmonth);
        final Calendar c = Calendar.getInstance();
        lg = new LogConfig();
        day = (RadioButton) findViewById(R.id.idday);
        month = (RadioButton) findViewById(R.id.idmonth);

        rgroup = (RadioGroup) findViewById(R.id.rgroupo);
        spinmonth.setEnabled(false);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        queue = Volley.newRequestQueue(this);
        p = new PulsaActivity();
        b = new BPJSActivity();
//        hasil();
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

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(lg.session(getApplicationContext())) {
                switch (checkedId) {

                    case R.id.idday: // first button
                       txtDatefrom.setEnabled(true);
                        txtDateto.setEnabled(true);
                        spinmonth.setEnabled(false);
                        break;
                    case R.id.idmonth: // secondbutton
                       spinmonth.setEnabled(true);
                        txtDatefrom.setEnabled(false);
                        txtDateto.setEnabled(false);
                        categories  = new ArrayList<String>();

                        for (int i = 0; i < arrMonth.length; i++) {
                            categories.add(arrMonth[i]);

                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(HistoryPoinActivity.this, android.R.layout.simple_spinner_item, categories);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinmonth.setAdapter(spinnerAdapter);
                        break;
                }
                }else{
                    finish();
                }
            }
        });




        spinmonth.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if(lg.session(getApplicationContext())) {
                        Object item = parent.getItemAtPosition(pos);

                        bulan=String.format("%2s", pos + 1).replace(' ', '0');
//                        Toast.makeText(getApplicationContext(), bulan,Toast.LENGTH_SHORT).show();
//                        paketitem = String.valueOf(item);
                        cektombol("month");
                    }else{
                        finish();
                    }

//                        poinpulsa.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
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
            if (!cek) {
                datefrom = mYear + "-" + String.format("%2s", mMonth + 1).replace(' ', '0') + "-" + String.format("%2s", mDay).replace(' ', '0');
                txtDatefrom.setText(datefrom);

            } else {
                dateto = mYear + "-" + String.format("%2s", mMonth + 1).replace(' ', '0') + "-" + String.format("%2s", mDay).replace(' ', '0');
                txtDateto.setText(dateto);
            }

            if (dateto.equals("") || datefrom.equals("")) {

            } else {
                cektombol("day");
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
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);

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

    public void hasil() {

//Toast.makeText(HistoryPoinActivity.this, pass+","+id_imei, Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_selectall_history,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(HistoryPoinActivity.this, response, Toast.LENGTH_SHORT).show();
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

//                            if(jo.getString("sukses").equals("false")){
                        if(jo.getString("sukses").equals("false")){
                            Toast.makeText(HistoryPoinActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                        }else {

//                            Toast.makeText(HistoryPoinActivity.this, "Maaf Data Tidak ada", Toast.LENGTH_SHORT).show();

                         tampil(response);
                            }

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
                        Toast.makeText(HistoryPoinActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("pass", b.md5(pass));
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void search() {


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_search_topup,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

//                            if(jo.getString("sukses").equals("false")){
                            if(jo.getString("sukses").equals("false")){
                                Toast.makeText(HistoryPoinActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {

                                tampil(response);
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(HistoryPoinActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("date_from", datefrom);
                params.put("date_to", dateto);
                params.put("pass", b.md5(pass));
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void search_month() {


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_search_month,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

//                            if(jo.getString("sukses").equals("false")){
                            if(jo.getString("sukses").equals("false")){

                                Toast.makeText(HistoryPoinActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {

                                tampil(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(HistoryPoinActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("month", bulan);
                params.put("pass", b.md5(pass));
                return params;
            }
        };
        queue.add(postRequest);

    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menua, menu);
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

    public void tampil(String response){

//                                Toast.makeText(HistoryPoinActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            JSONObject joi = null;
            try {
                joi = new JSONObject(response);
                String id = joi.getString("id");
                JSONArray jsonArray = new JSONArray(joi.getString("catatan"));
                int count = 0;
                String atasnama, atasnama2,saldo, date, keterangan, databank;
                data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                for (int i = 0; i < jsonArray.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
//                                        categories.add();
                    String ket = jsonArray.getJSONObject(i).getString("ket");
                    if(jsonArray.getJSONObject(i).getString("username").equals("sepuluh")){
                        atasnama = "admin";
                    }else{
                        atasnama = jsonArray.getJSONObject(i).getString("username");
                    }

                    if(jsonArray.getJSONObject(i).getString("username2").equals("sepuluh")){
                        atasnama2 = "admin";
                    }else{
                        atasnama2 = jsonArray.getJSONObject(i).getString("username2");
                    }

                    if(id.equals(jsonArray.getJSONObject(i).getDouble("id_user"))) {
                        if (jsonArray.getJSONObject(i).getDouble("debit") > 0) {
                            keterangan = "Out " + ket + " To " + atasnama;
                            saldo = jsonArray.getJSONObject(i).getString("debit")+"Poin";
                        }else{
                            keterangan = "In " + ket + " From " + atasnama2;
                            saldo = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                        }
                    }else{
                        if (jsonArray.getJSONObject(i).getDouble("debit") > 0) {
                            keterangan = "In " + ket + " From " + atasnama;
                            saldo = jsonArray.getJSONObject(i).getString("debit")+"Poin";
                        }else{
                            keterangan = "Out " + ket + " To " + atasnama2;
                            saldo = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                        }
                    }
                    csClass topup = new csClass(" ",
                            saldo, keterangan
                            , jsonArray.getJSONObject(i).getString("tgl"));
                    data.add(topup);
                    count++;
                }

                adapter = new ListCsAdapter(data, HistoryPoinActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (JSONException ei) {
                ei.printStackTrace();
                Toast.makeText(HistoryPoinActivity.this, ei.toString(), Toast.LENGTH_SHORT).show();
            }
    }

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(HistoryPoinActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HistoryPoinActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                HistoryPoinActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("month")){
                                    search_month();
                                }else if(menu.equals("day")) {
                                    search();
                                }else if(menu.equals("hasil")){
                                    hasil();
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
                        Toast.makeText(HistoryPoinActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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