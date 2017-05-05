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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class MainSubPanel extends AppCompatActivity {

    RecyclerView recyclerView;

    MenuPanelAdapter subadapter;
    EditText txtcon;
    Spinner spinmatauang;
    Button btncon;
    ImageButton btnback;
    EditText  passlama, passbaru;
    Button pass, btnpass;
    TextView id_username;

    TextView txtpoin,nama;
    String matauang;
    Dialog dialog;

    private RequestQueue queue;
    double hslpoin=0;
    private EditText coba1;
    private TextView coba2, txtback;
    private double poin;
    private Spinner coba3;
    private Button coba4, coba5;
    private BroadcastReceiver mBroadcastReceiver;
    ArrayAdapter<CharSequence> adapter;
    SharedPreferences sharedPreferences;
    ProgressDialog loadingawal;
    BPJSActivity b;
    BroadcastReceiver broadcastReceiver;
    String id_imei="", alias_menu="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sub_panel);

//        final TextView titleSubbMenu = (TextView) findViewById(R.id.submenutitle);
        final String sub_menu = getIntent().getExtras().getString("menu");


        btncon =(Button) findViewById(R.id.btncon);
        id_username = (TextView)findViewById(R.id.id_username);
        txtpoin =(TextView)findViewById(R.id.menupoin);
        nama =(TextView)findViewById(R.id.nama);
        txtback =(TextView)findViewById(R.id.txtback);

        ActionBar actionBar = getSupportActionBar();

        loadingawal = ProgressDialog.show(MainSubPanel.this, "Proses...", "Wait...", false, false);
        queue = Volley.newRequestQueue(this);

       adapter = ArrayAdapter.createFromResource(this,
                R.array.matauang, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LogConfig lg = new LogConfig();
        b = new BPJSActivity();
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        alias_menu=sharedPreferences.getString(LogConfig.ALIAS_MENU,"0");
        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));
        nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));
        saldo();
        btncon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                conv();

            }
        });


//        ActionBar actionBar = getSupportActionBar();
//        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_action_bar);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            ImageView ia = (ImageView)findViewById(R.id.logo);
            ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

            Glide.with(MainSubPanel.this)
                    .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                    .into(ia);

            if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                ib.setVisibility(View.GONE);
            }
        }

        if(sub_menu == null){
            Toast.makeText(getApplicationContext(), "Not Valid", Toast.LENGTH_SHORT).show();
            return;
        }
//        Toast.makeText(getApplicationContext(), sub_menu, Toast.LENGTH_SHORT).show();
//        titleSubbMenu.setText(sub_menu);

//        setContentView(R.layout.activity_main_sub_panel);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView2);

        switch (sub_menu) {
            case "G-Info":
//                ArrayList categories = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories.add(alias_menu+" "+BindMenu.getDataSub_GInfo().get(i).menu);
//                }

                subadapter = new MenuPanelAdapter(this, BindMenu.getDataSub_GInfo());

                break;

            case "G-PAY":
//                ArrayList categories_pay = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories_pay.add(alias_menu+" "+BindMenu.getPay().get(i).menu);
//                }
                subadapter = new MenuPanelAdapter(this, BindMenu.getPay());
                break;
            case "G-MULTI FINANCE":
//                ArrayList categories_finance = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories_finance.add(alias_menu+" "+BindMenu.getDataSub_GPayment().get(i).menu);
//                }
                subadapter = new MenuPanelAdapter(this, BindMenu.getDataSub_GPayment());
                break;
            case "Setting":
//                ArrayList categories_setting = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories_setting.add(alias_menu+" "+BindMenu.getsetting().get(i).menu);
//                }
                subadapter = new MenuPanelAdapter(this, BindMenu.getsetting());
                break;
            case "DEPOSIT/TRANSFER":
//                ArrayList categories_transfer = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories_transfer.add(alias_menu+" "+BindMenu.getDataSub_GTransfer().get(i).menu);
//                }
                subadapter = new MenuPanelAdapter(this, BindMenu.getDataSub_GTransfer());
                break;

            default:

//                ArrayList categories_def = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories_def.add(alias_menu+" "+BindMenu.getDataSub_GHottel().get(i).menu);
//                }

                subadapter = new MenuPanelAdapter(this, BindMenu.getDataSub_GHottel());
//                        Toast.makeText(context, data.get(holder.getAdapterPosition()).menu,Toast.LENGTH_SHORT).show();
                break;



        }

        recyclerView.setAdapter(subadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public  void cek_convert(final String nama_paket){


        double x=0;


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_convert,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        int angkaSignifikan = 2;
                        double temp = Math.pow(10, angkaSignifikan);
                        hslpoin= (double) Math.round(Double.valueOf(response)*temp)/temp;
                        coba2.setText(String.valueOf(hslpoin));
//                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSubPanel.this);
//                        builder.setMessage(matauang+txtcon.getText().toString()+" = "+hslpoin)
//                                .setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                                        // ui = 0;
//                                        coba2.setText("");
//                                    }
//                                })
//                        ;
//                        final android.app.AlertDialog alert = builder.create();
//                        alert.show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "error");
//                        Toast.makeText(PulsaActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();

                params.put("poin", nama_paket);


                return params;
            }
        };
        queue.add(postRequest);


        //

    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
        if(!login){
            lg();
        }
        saldo();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action)
                {
                    case RoosterConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);

                        return;

                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();

                        break;
                }


            }
        };
        IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver,filter);
        IntentFilter filter1 = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        registerReceiver(mBroadcastReceiver,filter1);



    }

    public void message(String body) {

        String[] data = body.split("\\-");
        if (data.length >= 2) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSubPanel.this);
            builder.setMessage(data[1])
                    .setCancelable(false)
                    .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            // ui = 0;

                        }
                    })
            ;
            final android.app.AlertDialog alert = builder.create();
            alert.show();
//        }
        }
    }
    public void conv(){
        Dialog dialog = new Dialog(MainSubPanel.this);
        dialog.setContentView(R.layout.conveter_layout);

        coba1 = (EditText) dialog.findViewById(R.id.nominalstxt);
        coba2 = (TextView) dialog.findViewById(R.id.hasilconverttxt);
        coba3 = (Spinner) dialog.findViewById(R.id.matauangsspin);
        coba4 = (Button) dialog.findViewById(R.id.convertbtn);
        coba5 = (Button) dialog.findViewById(R.id.transfertbtn);


        coba3.setAdapter(adapter);

        coba3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                matauang = String.valueOf(item);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.setTitle("Menu Converter");
        dialog.setCancelable(true);
        dialog.show();

        coba4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cek_convert(coba1.getText().toString());
                coba5.setVisibility(View.VISIBLE);
            }
        });
        coba5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSubPanel.this, TransferActivity.class);
                intent.putExtra("poin", coba2.getText().toString());
//                        MainSubPanel.this.finish();
                startActivity(intent);
            }
        });

        coba2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void onStop() {
        super.onStop();

//        if(mBroadcastReceiver.isOrderedBroadcast()) {
        unregisterReceiver(mBroadcastReceiver);
//        }
    }

    public void saldo(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
                        if(response.equals("false")) {

                            txtpoin.setText("0 Poin");
                        }else{
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    poin = jsonArray.getJSONObject(i).getDouble("poin");
                                    txtpoin.setText(poin+"Poin");
                                }
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
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(MainSubPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        if(loadingawal.isShowing()){
            loadingawal.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_panel ,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                logout();
                break;
            case R.id.editprofil:
                Intent st = new Intent(MainSubPanel.this, SettingActivity.class);
                st.putExtra("ket", "edit");
                startActivity(st);
                break;
            case R.id.editpass:
                TrxPass();
                break;
            case R.id.cs:
                Intent cs = new Intent(MainSubPanel.this, CustomerCareActivity.class);
                startActivity(cs);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void logout(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSubPanel.this);

        builder.setMessage("Are you sure logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        lg();
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

    public void lg(){
//        Intent intent = new Intent(MainSubPanel.this, MainActivity.class);
//        finish();
//        startActivity(intent);
        Intent intent = new Intent(MainSubPanel.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        try {
            disableService();
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e) {
            Log.e("unregisterReceiver",e.toString());
        }

        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, false);
        editor.putString(LogConfig.PASS_SESSION, "");
        editor.putString(LogConfig.ID_IMEI_SESSION, "");
        editor.putString(LogConfig.EMAIL_SESSION, "");
        editor.putString(LogConfig.USERNAME_SESSION, "");
        editor.putString(LogConfig.NAMAACC_SESSION, "");
        editor.putString(LogConfig.STAT_REG, "");
        editor.apply();
    }

    private void disableService() {
        Intent intent = new Intent(getApplicationContext(),RoosterConnectionService.class);
        stopService(intent);
    }

    public void edittrxpass(final String lama, final String baru){
        RequestQueue queue = Volley.newRequestQueue(MainSubPanel.this);
        b = new BPJSActivity();
        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_trxpass,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){
                            TrxPass();
                            Toast.makeText(MainSubPanel.this, "Wrong Data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainSubPanel.this, "Success", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(MainSubPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    public void TrxPass(){
        dialog = new Dialog(MainSubPanel.this);
        dialog.setContentView(R.layout.layout_trxpass);

        final EditText passtrxlama = (EditText) dialog.findViewById(R.id.passtrxlama);
        final EditText passtrxbaru = (EditText) dialog.findViewById(R.id.passtrxbaru);

        Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

        btntrxpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passtrxlama.getText().toString().equals("") || passtrxbaru.getText().toString().equals("")){
                    Toast.makeText(MainSubPanel.this, "Data must be filled",Toast.LENGTH_SHORT).show();
                }else{
                    edittrxpass(passtrxlama.getText().toString(), passtrxbaru.getText().toString());
                }
            }
        });

        dialog.setTitle("Edit Trx Password");
        dialog.setCancelable(true);
        dialog.show();
    }


}
