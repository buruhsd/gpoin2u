package tuberpraka.gpoin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jivesoftware.smack.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadGambarActivity extends AppCompatActivity {

    EditText trxpass, txtnorek, txtatasnama, jml_tf, txtket;
    TextView txttgl, id_poin;
    Spinner bank, metode;
    Button btnkirim;
    private RequestQueue queue;
    int serverResponseCode = 0;
    LogConfig lg;
    HttpURLConnection connection = null;
    public static final String TAG = "Upload Image";
    Dialog dialog;
    BPJSActivity b;
    String kalamat ="";
    String paket, cabang, norek, atasnama, jmltf, tgl;

    // I am using my local server for uploading image, you should replace it with your server address
    int hour, minute, mYear,mMonth, mDay;

    private String[] arrMonth = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    static final int DATE_DIALOG_ID = 1;

    public static final String UPLOAD_KEY = "upload_image";

    private int PICK_IMAGE_REQUEST = 100;

    private Bitmap bitmap;

    private Uri filePath;
    ProgressDialog loadingawal;
    private String selectedFilePath="", id ="", passtrx="", id_imei="", password="", url="", ket="";
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gambar);

        trxpass = (EditText) findViewById(R.id.txtpass);
        txtnorek = (EditText)findViewById(R.id.txtnorek);
        txtatasnama = (EditText) findViewById(R.id.txtatasnama);
        jml_tf = (EditText) findViewById(R.id.jml_tf);
        txttgl = (TextView) findViewById(R.id.txttgl);
        txtket = (EditText) findViewById(R.id.txtket);
        bank = (Spinner) findViewById(R.id.bank_member);
        metode = (Spinner) findViewById(R.id.metode_member);
        btnkirim = (Button) findViewById(R.id.btnkirim);
        id_poin = (TextView)findViewById(R.id.id_poin);
        lg = new LogConfig();

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        Intent intent = getIntent();
        id =intent.getStringExtra("id");
        id_poin.setText("* Transfer Nominal : "+intent.getStringExtra("poin"));
        b = new BPJSActivity();
        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);

        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION, "0");
        password = sharedPreferences.getString(LogConfig.PASS_SESSION, "0");
        queue = Volley.newRequestQueue(this);



        ambildatabank();

        bank.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        Object item = parent.getItemAtPosition(pos);
                        paket = String.valueOf(item);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        metode.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        Object item = parent.getItemAtPosition(pos);
                        cabang = String.valueOf(item);


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        btnkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lg.session(getApplicationContext())) {

                    if(trxpass.getText().toString().trim().equals("")
                        ||txtnorek.getText().toString().trim().equals("")
                        || txtatasnama.getText().toString().trim().equals("")
                        || jml_tf.getText().toString().trim().equals("")
                        || txttgl.getText().toString().trim().equals("")
                        || txtket.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "You must be complite the data", Toast.LENGTH_SHORT).show();
                }else {
                    passtrx = trxpass.getText().toString();
                    ket = txtket.getText().toString();
                    norek = txtnorek.getText().toString();
                    atasnama = txtatasnama.getText().toString();
                    jmltf = jml_tf.getText().toString();
                    tgl = txttgl.getText().toString();
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UploadGambarActivity.this);

                    builder.setMessage( "Your data :\n"
                            +"Bank Account : "+paket+"\n"
                            +"Payment Method : "+cabang+"\n"
                            +"Acc number : "+norek+"\n"
                            +"Acc name : "+atasnama+"\n"
                            +"Transfer Nominal : "+jmltf+"\n"
                            +"Transfer Date : "+tgl+"\n"
                           )
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    // ui = 0;

                                    String body= null;
                                    try {
                                        loadingawal = ProgressDialog.show(UploadGambarActivity.this, "Process...", "Wait...", false, false);
                                        simpan();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


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



//                    uploadFile(selectedFilePath);
//                    simpan_gambar();
//                    uploadImage();
                }
                }else{
                    finish();
                }
            }
        });

        txttgl.setOnTouchListener(new View.OnTouchListener() {

            @Override

            public boolean onTouch(View arg0, MotionEvent arg1) {

                showDialog(DATE_DIALOG_ID);

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

              String  datefrom = mYear + "-" + String.format("%2s", mMonth + 1).replace(' ', '0') + "-" + String.format("%2s", mDay).replace(' ', '0');
                txttgl.setText(datefrom);



        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            selectedFilePath = ImageFilePath.getPath(UploadGambarActivity.this, filePath);
//            selectedFilePath = filePath.getPath();
            Log.e(TAG, " File path : " + selectedFilePath);
            Toast.makeText(getApplicationContext(),selectedFilePath, Toast.LENGTH_SHORT ).show();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Log.e("Bitmap", bitmap.toString());
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if ( cursor.moveToFirst( ) ) {
            int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
            result = cursor.getString( column_index );
        }
        cursor.close( );
        if(result == null) {
            result = "Not found";
        }
        return result;
    }



    private void uploadImage() {
        Log.e(TAG, "uploadImage ");
        UploadImageApacheHttp uploadTask = new UploadImageApacheHttp();

//        uploadTask.doFileUpload(UPLOAD_URL, bitmap, handler);
        url = uploadTask.convertBitmapToString(bitmap);
        simpan_gambar();
//        uploadTask.kirim(selectedFilePath);

    }

    Handler handler = handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "Handler " + msg.what);
            if (msg.what == 1) {
//                Toast.makeText(getApplicationContext(),b.md5(passtrx)+","+id, Toast.LENGTH_SHORT ).show();
                simpan();
//                txtket.setText();
            } else {
                Toast.makeText(getApplicationContext(),"Upload Error", Toast.LENGTH_SHORT ).show();
//                txtket.setText("Upload Error");
            }
        }

    };


    public void simpan(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_konfirmasi_topup,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.d("Response", response);
//                        Toast.makeText(MainActivity.this, response.toString() , Toast.LENGTH_SHORT).show();
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);

//                            if(jo.getString("sukses").equals("false")){
//                        if(jo.getString("sukses").equals("false")){
                            Toast.makeText(UploadGambarActivity.this, jo.getString("catatan") , Toast.LENGTH_SHORT).show();
                            UploadGambarActivity.this.finish();
//                        }else {
//
//                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "error");
                        Toast.makeText(UploadGambarActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("passtrx", b.md5(passtrx));
                params.put("id_imei", id_imei);
                params.put("id", id);
                params.put("url", kalamat);
                params.put("ket", ket);
                params.put("id_bank_member", paket);
                params.put("id_metode", cabang);
                params.put("no_rek_member", norek);
                params.put("atas_nama_member", atasnama);
                params.put("tgl_transfer", tgl);
                params.put("jml_transfer", jmltf);



                return params;
            }
        };
        queue.add(postRequest);
    }

    public void simpan_gambar(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_gambar,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response", response);
                        kalamat = response;
                        simpan();
                        if(loadingawal.isShowing()){
                                        loadingawal.dismiss();
                                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(UploadGambarActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                        if(loadingawal.isShowing()){
                            loadingawal.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("gambar", url);

                return params;
            }
        };
        queue.add(postRequest);
    }
    public void ambildatabank() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_bank_member,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject jo = new JSONObject(response);
//                            if(jo.getString("sukses").equals("false")){
                            if(jo.getString("sukses").equals("false")){
                                Toast.makeText(UploadGambarActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {
                                JSONArray jsonArr = new JSONArray(jo.getString("catatan"));
                                categories = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                                String prov = "";
                                for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                    categories.add(jsonArr.getJSONObject(i).getString("bank"));

                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(UploadGambarActivity.this, android.R.layout.simple_spinner_item, categories);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                bank.setAdapter(spinnerAdapter);

                                ambildatametode();
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
                        Toast.makeText(UploadGambarActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
//                params.put("username", username);
//                params.put("password", md5(pass));
//                params.put("nohp", nohps);
//                params.put("harga", String.valueOf(harga));
//                params.put("trxpass", md5(trxpass));

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void ambildatametode() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_metode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        try {
                            JSONObject jo = new JSONObject(response);
//                            if(jo.getString("sukses").equals("false")){
                            if(jo.getString("sukses").equals("false")){
                                Toast.makeText(UploadGambarActivity.this, jo.getString("catatan"), Toast.LENGTH_LONG).show();
                            }else {
                                JSONArray jsonArr = new JSONArray(jo.getString("catatan"));
                                categories = new ArrayList<String>();
//                            imgprov.setVisibility(View.VISIBLE);
                                String prov = "";
                                for (int i = 0; i < jsonArr.length(); i++) {
//                                prov = jsonArr.getJSONObject(i).getString("nama");
                                    categories.add(jsonArr.getJSONObject(i).getString("metode"));

                                }

                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(UploadGambarActivity.this, android.R.layout.simple_spinner_item, categories);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                metode.setAdapter(spinnerAdapter);
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
                        Toast.makeText(UploadGambarActivity.this, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_imei", id_imei);
//                params.put("username", username);
//                params.put("password", md5(pass));
//                params.put("nohp", nohps);
//                params.put("harga", String.valueOf(harga));
//                params.put("trxpass", md5(trxpass));

                return params;
            }
        };
        queue.add(postRequest);
    }
}
