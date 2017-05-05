package tuberpraka.gpoin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class MainPanel extends AppCompatActivity {

    RecyclerView recyclerView;
    MenuPanelAdapter adapter;
    TextView id_username, txtnews, nama;
    TextView txtpoin, txtback;
    ImageView imgg;
    BroadcastReceiver broadcastReceiver;
    ProgressDialog loadingawal;
    MainActivity m;
    SharedPreferences sharedPreferences;
    private RequestQueue queue;
    String id_imei="", email="", kode_rhs="", trxpass="", alias_menu="";
    double poin=0;
    BPJSActivity b;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
m = new MainActivity();
b = new BPJSActivity();
        if (m.cek_status(this)) {
            ActionBar actionBar = getSupportActionBar();
            sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
            id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

            if (actionBar != null) {
                actionBar.setCustomView(R.layout.custom_action_bar);

                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowCustomEnabled(true);
                ImageView ia = (ImageView)findViewById(R.id.logo);
                ImageView ib = (ImageView)findViewById(R.id.actionBarLogo);

                Glide.with(MainPanel.this)
                        .load( sharedPreferences.getString(LogConfig.LOGO,"0"))
                        .into(ia);

                if( sharedPreferences.getString(LogConfig.ID_BISNIS,"0").equals("4")){
                    ib.setVisibility(View.GONE);
                }
            }
           setContentView(R.layout.activity_main_panel);
            queue = Volley.newRequestQueue(this);
//            sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//            id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
            alias_menu=sharedPreferences.getString(LogConfig.ALIAS_MENU,"0");
            recyclerView = (RecyclerView) findViewById(R.id.recycleView);

            txtnews = (TextView)findViewById(R.id.id_news);
            txtpoin =(TextView)findViewById(R.id.menupoin);
            imgg = (ImageView)findViewById(R.id.imgin);

            id_username = (TextView)findViewById(R.id.id_username);
            nama =(TextView)findViewById(R.id.nama);

            nama.setText(sharedPreferences.getString(LogConfig.NAMAACC_SESSION,"0"));

            id_username.setText(sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"));

//                ArrayList categories = new ArrayList<String>();
//                for(int i = 0; i< BindMenu.getData().size();i++){
//                    categories.add(alias_menu+" "+BindMenu.getData().get(i).menu);
//                }

                adapter =  new MenuPanelAdapter(this, BindMenu.getData());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

            cek_kode();
            news();
            saldo();

            try {
                Bitmap bitmap = encodeAsBitmap(id_username.getText().toString());
                imgg.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }

        }else{
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

                            MainPanel.this.finish();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }



    }
    @Override
    protected void onResume() {
        super.onResume();
        Boolean login=sharedPreferences.getBoolean(LogConfig.LOGGEDIN_SHARED_PREF,true);
                  if(!login){
                      lg();
                  }
        saldo();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Boolean status = intent.getExtras().getBoolean("status_login");
                    if(!status){
//                        Toast.makeText(getApplicationContext(), "Akun Anda Bermasalah,", Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(MainPanel.this, MainActivity.class);
//                        MainPanel.this.finish();
//                        startActivity(i);


                    }else{
//                        Toast.makeText(getApplicationContext(), "gagal,", Toast.LENGTH_SHORT).show();
                    }
//                    if(loadingawal.isShowing()) {
//                        loadingawal.dismiss();
//                    }
                }
            };
        }else{
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
//                String k;
//                Toast.makeText(getApplicationContext(), "msuk", Toast.LENGTH_SHORT).show();
                    String action = intent.getAction();
                    switch (action)
                    {
                        case RoosterConnectionService.NEW_MESSAGE:
//                        k = RoosterConnectionService.NEW_MESSAGE;
                            String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                            String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);
//                            message(body);

                            return;

                        case RoosterConnectionService.UI_AUTHENTICATED:
//                            Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG,"Got a broadcast to show the main app window");
                            //Show the main app window
//                        showProgress(false);
//                        Intent i2 = new Intent(mContext,ContactListActivity.class);
//                        startActivity(i2);
//                        finish();

                            break;
                    }


                }
            };

        }
        registerReceiver(broadcastReceiver, new IntentFilter("status"));
        IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        registerReceiver(broadcastReceiver,filter);
        IntentFilter filter1 = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        registerReceiver(broadcastReceiver,filter1);
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
                super.onBackPressed();
                break;
            case R.id.editprofil:
                Intent st = new Intent(MainPanel.this, SettingActivity.class);
                st.putExtra("ket", "profil");
                startActivity(st);
                break;
            case R.id.editpass:
                TrxPass();
                break;
            case R.id.cs:
                Intent cs = new Intent(MainPanel.this, CustomerCareActivity.class);
                startActivity(cs);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void disableService() {
        Intent intent = new Intent(getApplicationContext(),RoosterConnectionService.class);
        stopService(intent);
    }


    @Override
    public void onBackPressed() {
        logout();
    }

    public void logout(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainPanel.this);

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

    public void onStop(){
        super.onStop();
        if(broadcastReceiver!=null) {
            try {
                unregisterReceiver(broadcastReceiver);
            }catch (IllegalArgumentException e) {
                Log.e("unregisterReceiver",e.toString());
            }
        }
    }
//    public void onPause() {
//        super.onPause();
//
//        unregisterReceiver(broadcastReceiver);
//    }

    public void lg(){
        Intent intent = new Intent(MainPanel.this, MainActivity.class);
       finish();
        startActivity(intent);

        try {
            disableService();
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e) {
            Log.e("unregisterReceiver",e.toString());
        }

        SharedPreferences sharedPreferences = MainPanel.this.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
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

    public void news(){
//        Toast.makeText(MainPanel.this, "news" , Toast.LENGTH_SHORT).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_news,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone

                        if(response.equals("false")){
                            txtnews.setText(" ");
                        }else {
                            txtnews.setText("*"+response);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString() );
                        Toast.makeText(MainPanel.this, error.toString() , Toast.LENGTH_SHORT).show();
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

    public void TrxPass(){
        dialog = new Dialog(MainPanel.this);
        dialog.setContentView(R.layout.layout_trxpass);

        final EditText passtrxlama = (EditText) dialog.findViewById(R.id.passtrxlama);
        final EditText passtrxbaru = (EditText) dialog.findViewById(R.id.passtrxbaru);

        Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

        btntrxpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passtrxlama.getText().toString().equals("") || passtrxbaru.getText().toString().equals("")){
                    Toast.makeText(MainPanel.this, "Data must be filled",Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(MainPanel.this);
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
                            Toast.makeText(MainPanel.this, "Wrong Data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainPanel.this, "Success", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 100, 100, null);

        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
//bitmap.setPixels(pixels, offset, stride, x, y,width, height);

        return bitmap;
    }

    public void cek_kode(){
        loadingawal = ProgressDialog.show(MainPanel.this, "Proses...", "Wait...", false, false);

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_kode,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
if(response.equals("true")){
    if(loadingawal.isShowing()){
        loadingawal.dismiss();
    }
}else{
    dialog = new Dialog(MainPanel.this);
    dialog.setContentView(R.layout.layout_kode);

    final EditText txtemail = (EditText) dialog.findViewById(R.id.txtemail);
    final EditText kode = (EditText) dialog.findViewById(R.id.passtrxbaru);
    final EditText passtrxa = (EditText) dialog.findViewById(R.id.passtrx);

    Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

    btntrxpass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(txtemail.getText().toString().equals("") || kode.getText().toString().equals("") || passtrxa.getText().toString().equals("")){
                Toast.makeText(MainPanel.this, "Data must be filled",Toast.LENGTH_SHORT).show();
            }else{
                email = txtemail.getText().toString();
                kode_rhs = kode.getText().toString();
                trxpass = passtrxa.getText().toString();
                Log.e("Response passtrx", trxpass);
                loadingawal = ProgressDialog.show(MainPanel.this, "Proses...", "Wait...", false, false);
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
                        Toast.makeText(MainPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainPanel.this);

                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
MainPanel.this.finish();
                                            }
                                        })

                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainPanel.this);

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
                        Toast.makeText(MainPanel.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                Log.e("Response passtrx a", trxpassa);
                params.put("trxpass", b.md5(trxpassa));
                params.put("id_imei", id_imei);
                params.put("email", email);
                params.put("kode", kode_rhs);
                return params;
            }
        };
        queue.add(postRequest);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage = new ImageView(MainPanel.this);

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
