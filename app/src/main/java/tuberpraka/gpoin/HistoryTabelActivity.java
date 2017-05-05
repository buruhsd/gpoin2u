package tuberpraka.gpoin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import android.view.ViewGroup.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryTabelActivity extends AppCompatActivity {
    private RequestQueue queue;
    RecyclerView recyclerView;
    LogConfig lg;
    SharedPreferences sharedPreferences;
    Button btnprev, btnext, btnprint;

    String username = "", id_imei = "", pass="";
    ListCsAdapter adapter;
    ArrayList<csClass> data;
    PulsaActivity p;
    List<String> categories;
    TextView txtDateto, txtDatefrom, txtpoin;
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

    TableRow tr_head;
    int jmlrow=5;
    TableLayout tl;
    TextView tbket,tbtgl, tbkredit, tbdebit, tbstatus, nama, id_username, tbsaldo_sisa;
    String js="";
    int awal=0;
    int a=0;
    String kket="";

    double saldo=0;
    double sal[];

    ProgressDialog loadingawal;

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

            Glide.with(HistoryTabelActivity.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }
        setContentView(R.layout.activity_history_tabel);

//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LogConfig.USERNAME_SESSION, "0");
//        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0");
        pass = sharedPreferences.getString(LogConfig.PASS_SESSION, "0");
        txtDatefrom = (TextView) findViewById(R.id.txtDatefrom);
        txtDateto = (TextView) findViewById(R.id.txtDateto);
        spinmonth =(Spinner)findViewById(R.id.spinmonth);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        nama =(TextView)findViewById(R.id.nama);
        id_username =(TextView)findViewById(R.id.id_username);
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION, "0"));
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));

loadingawal = new ProgressDialog(HistoryTabelActivity.this);
        btnext = (Button)findViewById(R.id.next);
        btnprev =(Button)findViewById(R.id.prev);
        btnprint =(Button)findViewById(R.id.btnprint);

        btnext.setVisibility(View.GONE);
        btnprev.setVisibility(View.GONE);
        btnprint.setVisibility(View.GONE);
        final Calendar c = Calendar.getInstance();
        lg = new LogConfig();
        day = (RadioButton) findViewById(R.id.idday);
        month = (RadioButton) findViewById(R.id.idmonth);

        rgroup = (RadioGroup) findViewById(R.id.rgroupo);
        spinmonth.setEnabled(false);

//        PaginationLayout paginationLayout = new PaginationLayout(this);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        tl = (TableLayout) findViewById(R.id.tbtopup);


        queue = Volley.newRequestQueue(this);
        p = new PulsaActivity();
        b = new BPJSActivity();

        saldo();
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

        btnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                awal=awal+1;

                tampil();
                }else{
                    finish();
                }
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {
                awal=awal-1;

                tampil();
                }else{
                    finish();
                }
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
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(HistoryTabelActivity.this, android.R.layout.simple_spinner_item, categories);
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

        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(HistoryTabelActivity.this, "ok",Toast.LENGTH_SHORT).show();
                loadingawal = ProgressDialog.show(HistoryTabelActivity.this, "Process", "Wait...", false, false);
                print();
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
                                Toast.makeText(HistoryTabelActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {

//                            Toast.makeText(HistoryPoinActivity.this, "Maaf Data Tidak ada", Toast.LENGTH_SHORT).show();
                                js = response;
                                awal =0;
                                tampil();
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
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
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
                                tl.removeAllViews();
                                btnext.setVisibility(View.GONE);
                                btnprev.setVisibility(View.GONE);
                                btnprint.setVisibility(View.GONE);
                                Toast.makeText(HistoryTabelActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {
                                js = response;
                                awal =0;
                                tampil();
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
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
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
                                tl.removeAllViews();
                                btnext.setVisibility(View.GONE);
                                btnprev.setVisibility(View.GONE);
                                btnprint.setVisibility(View.GONE);
                                Toast.makeText(HistoryTabelActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {
                                js = response;
                                awal =0;
                                JSONArray jsonArray = new JSONArray(jo.getString("catatan"));
                                sal = new double[jsonArray.length()+1];
                                tampil();
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
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
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

    public void tampil(){

//                                Toast.makeText(HistoryPoinActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        btnext.setVisibility(View.VISIBLE);
        btnprev.setVisibility(View.VISIBLE);
        btnprint.setVisibility(View.VISIBLE);
        JSONObject joi = null;
        try {
            joi = new JSONObject(js);
            String id = joi.getString("id");
            JSONArray jsonArray = new JSONArray(joi.getString("catatan"));
            int count = 0;
            String debit = null, kredit =null,tanggal =null, keterangan = null, atasnama = null;
            data = new ArrayList<>();
            Log.e("String id", id);
            double  deb=0, kre=0;
            tl.removeAllViewsInLayout();
//            tl = null;

            tr_head = new TableRow(this);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tbket = new TextView(this);
            tbket.setText("Ket");
            tbket.setTextColor(Color.BLACK);
            tbket.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbket.setPadding(10, 10, 10, 10);
            tbket.setBackgroundResource(R.drawable.border);
            tbket.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tbket.setGravity(Gravity.CENTER);
            tr_head.addView(tbket); // Adding textView to tablerow.

            tbstatus = new TextView(this);
            tbstatus.setText("Status");
            tbstatus.setTextColor(Color.BLACK);
            tbstatus.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbstatus.setPadding(10, 10, 10, 10);
            tbstatus.setBackgroundResource(R.drawable.border);
            tbstatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tbstatus.setGravity(Gravity.CENTER);
            tr_head.addView(tbstatus); // Adding textView to tablerow.

            tbdebit = new TextView(this);
            tbdebit.setText("In");
            tbdebit.setTextColor(Color.BLACK);
            tbdebit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbdebit.setPadding(10, 10, 10, 10);
            tbdebit.setGravity(Gravity.CENTER);
            tbdebit.setBackgroundResource(R.drawable.border);
            tbdebit.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tr_head.addView(tbdebit); // Adding textView to tablerow.


            tbkredit = new TextView(this);
            tbkredit.setText("Out");
            tbkredit.setTextColor(Color.BLACK);
            tbkredit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbkredit.setPadding(10, 10, 10, 10);
            tbkredit.setGravity(Gravity.CENTER);
            tbkredit.setBackgroundResource(R.drawable.border);
            tbkredit.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tr_head.addView(tbkredit); // Adding textView to tablerow.


            tbtgl = new TextView(this);
            tbtgl.setText("Tanggal");
            tbtgl.setTextColor(Color.BLACK);
            tbtgl.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbtgl.setPadding(10, 10, 10, 10);
            tbtgl.setBackgroundResource(R.drawable.border);
            tbtgl.setGravity(Gravity.CENTER);
            tbtgl.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tr_head.addView(tbtgl); // Adding textView to tablerow.

            tbsaldo_sisa = new TextView(this);
            tbsaldo_sisa.setText("Saldo");
            tbsaldo_sisa.setTextColor(Color.BLACK);
            tbsaldo_sisa.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tbsaldo_sisa.setPadding(10, 10, 10, 10);
            tbsaldo_sisa.setBackgroundResource(R.drawable.border);
            tbsaldo_sisa.setGravity(Gravity.CENTER);
            tbsaldo_sisa.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tr_head.addView(tbsaldo_sisa); // Adding textView to tablerow.

            tl.addView(tr_head, new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            View v = new View(this);
            v.setBackgroundColor(Color.BLACK);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
            int abbc = jsonArray.length()-(awal*jmlrow);

            int akhir=0;
            if(abbc>= jmlrow){
                akhir=awal*jmlrow+jmlrow ;
                btnext.setVisibility(View.VISIBLE);
            }else{
                akhir=awal*jmlrow+abbc ;
                btnext.setVisibility(View.GONE);
            }

            if(awal ==0){
                btnprev.setVisibility(View.GONE);
            }else{
                btnprev.setVisibility(View.VISIBLE);
            }

            int awalan =awal*jmlrow;
            if(jsonArray.length() >=akhir) {
                akhir = akhir;
            }else{
                akhir= jsonArray.length()-akhir;
            }

            if(awalan<=0){
                awalan =0;
                saldo=0;
            }else{
                awalan = awalan;
                try {
                    saldo = sal[awal - 1];
                }catch(Exception e){
                    e.printStackTrace();

                }
            }

            for (int i = awalan; i < akhir; i++) {

                if(awal == 0 && i ==0){

                    tr_head = new TableRow(this);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));


                    tbket = new TextView(this);
                    tbket.setText("Data Sebelumnya");
                    tbket.setTextColor(Color.BLACK);
                    tbket.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbket.setPadding(10, 10, 10, 10);
                    tbket.setBackgroundResource(R.drawable.border);

                    tbket.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    tr_head.addView(tbket); // Adding textView to tablerow.

                    tbstatus = new TextView(this);
                    tbstatus.setText("");
                    tbstatus.setTextColor(Color.BLACK);
                    tbstatus.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbstatus.setPadding(10, 10, 10, 10);
                    tbstatus.setBackgroundResource(R.drawable.border);
                    tbstatus.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

                    tr_head.addView(tbstatus); // Adding textView to tablerow.

                    tbdebit = new TextView(this);
                    tbdebit.setText(joi.getString("debit"));
                    tbdebit.setTextColor(Color.BLACK);
                    tbdebit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbdebit.setPadding(10, 10, 10, 10);
                    tbdebit.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    tbdebit.setBackgroundResource(R.drawable.border);
                    tr_head.addView(tbdebit); // Adding textView to tablerow.

                    tbkredit = new TextView(this);
                    tbkredit.setText(joi.getString("kredit"));
                    tbkredit.setTextColor(Color.BLACK);
                    tbkredit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbkredit.setPadding(10, 10, 10, 10);
                    tbkredit.setBackgroundResource(R.drawable.border);
                    tbkredit.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    tr_head.addView(tbkredit); // Adding textView to tablerow.



                    tbtgl = new TextView(this);
                    tbtgl.setText("");
                    tbtgl.setTextColor(Color.BLACK);
                    tbtgl.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbtgl.setPadding(10, 10, 10, 10);
                    tbtgl.setBackgroundResource(R.drawable.border);
                    tbtgl.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    tr_head.addView(tbtgl); // Adding textView to tablerow.

                    tbsaldo_sisa = new TextView(this);
                    tbsaldo_sisa.setText(joi.getString("saldo"));
                    tbsaldo_sisa.setTextColor(Color.BLACK);
                    tbsaldo_sisa.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tbsaldo_sisa.setPadding(10, 10, 10, 10);
                    tbsaldo_sisa.setBackgroundResource(R.drawable.border);
                    tbsaldo_sisa.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    tr_head.addView(tbsaldo_sisa); // Adding textView to tablerow.

                    tl.addView(tr_head, new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    saldo = joi.getDouble("saldo");
                }

                String status="";
                String ket = jsonArray.getJSONObject(i).getString("ket");
                if(ket.toUpperCase().contains("TRANSFER")){
                    if(ket.contains("FEE")){
                        keterangan = ket;
                        status="";
                        debit = "0Poin";
                        kredit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                        deb = 0;
                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }
                    else if(ket.contains("FROM")){
                        keterangan = ket;
                        status="";
                        debit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                        kredit = "0Poin";
                        deb = jsonArray.getJSONObject(i).getDouble("kredit");
                        kre =0;
                    }else if(ket.contains("TO")){
                        keterangan = ket;
                        status="";
                        debit = "0Poin";
                        kredit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                        deb = 0;
                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }else{
                        if(id.equals(jsonArray.getJSONObject(i).getString("id_user"))) {
                            if (jsonArray.getJSONObject(i).getString("username2").equals("sepuluh")) {
                                atasnama = "FROM Admin";
                                debit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                                kredit = "0Poin";

                                deb = jsonArray.getJSONObject(i).getDouble("kredit");
                                kre =0;

                            } else{
                                atasnama = "TO " + jsonArray.getJSONObject(i).getString("username2");
                                debit = "0Poin";
                                kredit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                                deb = 0;
                                kre =jsonArray.getJSONObject(i).getDouble("kredit");
                            }
                        }else{
                            atasnama = "FROM "+jsonArray.getJSONObject(i).getString("username");
                            debit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                            kredit = "0Poin";

                            deb = jsonArray.getJSONObject(i).getDouble("kredit");
                            kre =0;

                        }
                        keterangan = ket+" "+atasnama;
                        status="";

                    }
                }else  if(ket.toUpperCase().contains("BUY VOUCHER")) {
//                    keterangan = ket;
                    Log.e("status", jsonArray.getJSONObject(i).getString("status"));
                    kredit = jsonArray.getJSONObject(i).getString("kredit") + "Poin";
                    debit = "0Poin";

                    deb = 0;

                    if(jsonArray.getJSONObject(i).getString("status").contains("CANCEL")){
                        keterangan = ket;
                        status = jsonArray.getJSONObject(i).getString("status");

                        kre =0;
                    }else if(jsonArray.getJSONObject(i).getString("status").equals("VALID")) {
                        keterangan = ket;
                        status = "PROCESS";

                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }else{
                        keterangan = ket;
                        status = jsonArray.getJSONObject(i).getString("status");

                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }
                }else  if(ket.toUpperCase().contains("BUY")){
                    keterangan = ket;
                    status="";
                    kredit = "0Poin";
                    debit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                    deb = jsonArray.getJSONObject(i).getDouble("kredit");
                    kre =0;
                }else if(ket.contains("CREDIT PURCHASE WITH G POIN")){
                    if(jsonArray.getJSONObject(i).getString("status").contains("CANCEL")){
                        keterangan = ket;
                        status = jsonArray.getJSONObject(i).getString("status");
                        deb =0;
                        kre =0;
                    }else if(jsonArray.getJSONObject(i).getString("status").equals("VALID")) {
                        keterangan = ket;
                        status = "PROCESS";
                        deb = 0;
                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }else{
                        keterangan = ket;
                        status = jsonArray.getJSONObject(i).getString("status");
                        deb = 0;
                        kre =jsonArray.getJSONObject(i).getDouble("kredit");
                    }

                    kredit = jsonArray.getJSONObject(i).getString("kredit")+"Poin";
                    debit = "0Poin";
                }else  if(ket.toUpperCase().contains("AKTIFASI")) {
                    keterangan = ket;
                    status = "";
                    kredit = jsonArray.getJSONObject(i).getString("kredit") + "Poin";
                    debit = "0Poin";

                    deb = 0;
                    kre =jsonArray.getJSONObject(i).getDouble("kredit");
                }

//                Toast.makeText(HistoryTabelActivity.this, saldo+" "+deb+" "+kre,Toast.LENGTH_LONG).show();
                int angkaSignifikan = 3;
                double temp = Math.pow(10, angkaSignifikan);
                saldo= (double) Math.round(Double.valueOf(saldo+deb-kre)*temp)/temp;


                tr_head = new TableRow(this);
                tr_head.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));


                tbket = new TextView(this);
                tbket.setText(keterangan);
                tbket.setTextColor(Color.BLACK);
                tbket.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbket.setPadding(10, 10, 10, 10);
                tbket.setBackgroundResource(R.drawable.border);

                tbket.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tr_head.addView(tbket); // Adding textView to tablerow.

                tbstatus = new TextView(this);
                tbstatus.setText(status);
                tbstatus.setTextColor(Color.BLACK);
                tbstatus.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbstatus.setPadding(10, 10, 10, 10);
                tbstatus.setBackgroundResource(R.drawable.border);
                tbstatus.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

                tr_head.addView(tbstatus); // Adding textView to tablerow.

                tbdebit = new TextView(this);
                tbdebit.setText(debit);
                tbdebit.setTextColor(Color.BLACK);
                tbdebit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbdebit.setPadding(10, 10, 10, 10);
                tbdebit.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tbdebit.setBackgroundResource(R.drawable.border);
                tr_head.addView(tbdebit); // Adding textView to tablerow.

                tbkredit = new TextView(this);
                tbkredit.setText(kredit);
                tbkredit.setTextColor(Color.BLACK);
                tbkredit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbkredit.setPadding(10, 10, 10, 10);
                tbkredit.setBackgroundResource(R.drawable.border);
                tbkredit.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tr_head.addView(tbkredit); // Adding textView to tablerow.



                tbtgl = new TextView(this);
                tbtgl.setText(jsonArray.getJSONObject(i).getString("tgl"));
                tbtgl.setTextColor(Color.BLACK);
                tbtgl.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbtgl.setPadding(10, 10, 10, 10);
                tbtgl.setBackgroundResource(R.drawable.border);
                tbtgl.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tr_head.addView(tbtgl); // Adding textView to tablerow.

                tbsaldo_sisa = new TextView(this);
                tbsaldo_sisa.setText(saldo+"");
                tbsaldo_sisa.setTextColor(Color.BLACK);
                tbsaldo_sisa.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tbsaldo_sisa.setPadding(10, 10, 10, 10);
                tbsaldo_sisa.setBackgroundResource(R.drawable.border);
                tbsaldo_sisa.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                tr_head.addView(tbsaldo_sisa); // Adding textView to tablerow.

                tl.addView(tr_head, new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));


            }
            if(awalan<0){
                saldo=0;
                sal[0]=saldo;
            }else{
                try {
                    sal[awal] = saldo;
                }catch(Exception e){
                    e.printStackTrace();

//                    sal[0] = saldo;
                }
            }


        } catch (JSONException ei) {
            ei.printStackTrace();
            Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
//            Toast.makeText(HistoryTabelActivity.this, ei.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(HistoryTabelActivity.this);
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HistoryTabelActivity.this);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                HistoryTabelActivity.this.finish();
                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                if(menu.equals("month")){
                                    kket ="month";
                                    search_month();
                                }else if(menu.equals("day")) {
                                    kket ="day";
                                    search();
                                }else if(menu.equals("hasil")){
                                    hasil();
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void print() {


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.URL_PRINT_MUTASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response print", response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

                            if(loadingawal.isShowing()) {
                                loadingawal.dismiss();
                            }
                                Toast.makeText(HistoryTabelActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HistoryTabelActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        if(loadingawal.isShowing()) {
                            loadingawal.dismiss();
                        }
                        Toast.makeText(HistoryTabelActivity.this, "Invalid Connection", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_imei", id_imei);
                params.put("ket", kket);
                params.put("date_from", datefrom);
                params.put("date_to", dateto);
                params.put("month", bulan);
                params.put("pass", b.md5(pass));
                return params;
            }
        };
        queue.add(postRequest);

    }
}