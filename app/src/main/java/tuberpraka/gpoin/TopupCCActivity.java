package tuberpraka.gpoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import tuberpraka.gpoin.Model.NicePay;

public class TopupCCActivity extends AppCompatActivity {

    public final static String MESSAGE_KEY = "tuberpraka.gpoin.meesage_key";
    public final static String POIN_KEY = "tuberpraka.gpoin.pass_key";

    public static final String iMid = "BMRITEST01";
    public static final String encodeKey = "33F49GnCMS1mFYlGXisbUDzVf2ATWCl9k3R++d5hDd3Frmuos/XLx8XhXpe+LDYAbpGKZYSwtlyyLOtS/8aD7A==";
    public static String url = "https://www.nicepay.co.id/nicepay/api/onePassToken.do";
    public static String charge = "http://192.168.1.69/nicepay/PHP_Nicepay_Direct/charge.php";
    public static String storeCC= "http://192.168.1.69/apig/gmember/Controller_topup/storeCC";
    public static String refnumber = "http://192.168.1.69/apig/gmember/Controller_topup/refnumber";
    public final static String harga= "15000";
    EditText editText, editText2, editText3, editText4, editText5, editText6;
    Button button2;
    AlertDialog.Builder dialog;
AlertDialog dialogx;
    LayoutInflater inflater;
    View dialogView;
    WebView bayar;
    SharedPreferences sharedPreferences;
    String id_imei, email, ref;
    String referenceNo;
    boolean xx = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        Log.d("DARI INTENT", amt);
        sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        email = sharedPreferences.getString(LogConfig.EMAIL_SESSION, "0");
//
//
//        referenceNo = refnumber;

        Log.d("emailnya", email);
//        Log.d("tanggal hari ini", timeStamp);
        setContentView(R.layout.activity_topup_cc);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        button2 = (Button) findViewById(R.id.button2);



        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                input();
                string();
                referensi();


            }
        });

    }


    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void string() {
        Intent intent = getIntent();
        String message = intent.getStringExtra(MESSAGE_KEY);
        String passtrx = intent.getStringExtra(POIN_KEY);

        Integer poin = Integer.parseInt(message);
        Integer biaya = Integer.parseInt(harga);
        Integer amt = poin*biaya;

        Log.d("Poin", String.valueOf(poin));
        Log.d("PASSTRX", passtrx);
        Log.d("mencoba",String.valueOf(amt));

        String name = editText.getText().toString();
        String card = editText2.getText().toString();
        String cvv = editText3.getText().toString();
        String yymm = editText4.getText().toString();
//        String referenceNo = "12345678";

        Log.d("AMT", String.valueOf(amt));

        String merchantToken =iMid+referenceNo+amt+encodeKey;
        MessageDigest digest=null;
        String hash;
        hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(merchantToken.getBytes());

            hash = bytesToHexString(digest.digest());

            Log.i("Eamorr", "result is " + hash);
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

//        String sementara = "?jsonData={\"iMid\":\""+iMid+"\",\"referenceNo\":\""+referenceNo+"\",\"amt\":\""+amt+"\",\"cardNo\":\""+card+"\",\"cardExpYymm\":\""+yymm+"\",\"merchantToken\":\""+hash+"}";

//        Log.d("Sementara", sementara);


//        String merchantToken = sha256();

        RequestQueue queue = Volley.newRequestQueue(this);
//
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("merchantToken", encodeKey);
        jsonParams.put("cardExpYymm",yymm);
        jsonParams.put("cardNo",card);
        jsonParams.put("amt", "10");
        jsonParams.put("referenceNo",referenceNo);
        jsonParams.put("iMID", iMid);

        StringRequest URL = new StringRequest(Request.Method.GET, url + "?jsonData={\"iMid\":\""+iMid+"\",\"referenceNo\":\""+referenceNo+"\",\"amt\":\""+amt+"\",\"cardNo\":\""+card+"\",\"cardExpYymm\":\""+yymm+"\",\"merchantToken\":\""+hash+"\"}", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("URL", url);
//
//                Toast.makeText(TopupCCActivity.this, response,Toast.LENGTH_LONG).show();

                String url = response.replace("(", "");
                String hasil = url.replace(")", "");
                Log.d("AAA", hasil);

                try{
                    JSONObject obj = new JSONObject(hasil);
                    NicePay json = new NicePay();
                    json.setCardToken(obj.getString("cardToken"));
                    Log.d("cardToken", obj.getString("cardToken"));
                    DialogForm(obj.getString("cardToken"),"");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(URL);


    }

    public void DialogForm(final String cardToken, String button){
        dialog = new AlertDialog.Builder(TopupCCActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.webview,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        Log.d("One Pass TOken", cardToken);
//        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("BAYAR");

        dialog.setPositiveButton("save",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
//                sendCharge(cardToken);
                sendToServer();
                dialog.dismiss();
            }

        });

        dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

                dialog.dismiss();

            }
        });

        bayar = (WebView) dialogView.findViewById(R.id.secure);
        WebSettings sembarang = bayar.getSettings();
        sembarang.setJavaScriptEnabled(true);
        sembarang.setAllowFileAccess(true);
        sembarang.setAppCacheEnabled(true);
        sembarang.setLoadWithOverviewMode(true);
        sembarang.setJavaScriptCanOpenWindowsAutomatically(true);
        bayar.getUrl();
        bayar.setWebViewClient(new GeoWebViewClient());
        bayar.setWebViewClient(new NoErrorWebViewClient());
        bayar.setWebViewClient(new WebViewClient(){
            @Override

            public void onPageFinished(WebView view, String data) {
                super.onPageFinished(view, data);
                //   Toast.makeText(MainActivity.this, "Finish " , Toast.LENGTH_SHORT).show();
               /* prg.setVisibility(View.GONE);
                img.setVisibility(View.GONE);*/

//                Toast.makeText(TopupCCActivity.this, "Pagefinish"+data,Toast.LENGTH_LONG).show();
                Log.d("URLNYA", data);
                if (data.contains("0000")){
                    Toast.makeText(TopupCCActivity.this, "ISO"+data,Toast.LENGTH_LONG).show();
                    sendToServer();
                    dialogx.dismiss();
                }

                }


            });

        bayar.loadUrl("https://www.nicepay.co.id/nicepay/api/secureVeRequest.do?country=360&callbackUrl=http://192.168.1.69/nicepay/PHP_Nicepay_Direct/3dsecure.php&onePassToken="+cardToken);
//        bayar.loadUrl("https://demo.semestaweb.tv");
        dialogx = dialog.create();
        dialogx.show();
//        dialogx.dismiss();


//        ((AlertDialog)dialogx).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

    }

    public void sendCharge(String cardToken){
        Intent intent = getIntent();
        String message = intent.getStringExtra(MESSAGE_KEY);
        String passtrx = intent.getStringExtra(POIN_KEY);

        Integer poin = Integer.parseInt(message);
        Integer biaya = Integer.parseInt(harga);
        Integer amt = poin*biaya;

        final String name = editText.getText().toString();
        final String card = editText2.getText().toString();
        final String cvv = editText3.getText().toString();
        final String yymm = editText4.getText().toString();
//        final String referenceNo = "12345678";
        final String total = String.valueOf(amt);
        final String onepass = cardToken;

        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("charge", charge);
//


        StringRequest URL = new StringRequest(Request.Method.POST, charge, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("data", response.toString());
                Log.d("manga", "naruto");
//                sendToServer();
//
                Toast.makeText(TopupCCActivity.this, response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error gan", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("billingNm", name);
                jsonParams.put("cardExpYymm",yymm);
                jsonParams.put("cardNo",card);
                jsonParams.put("cardCvv", cvv);
                jsonParams.put("payMethod", "01");
                jsonParams.put("amt", total);
                jsonParams.put("resultMsg", "");
                jsonParams.put("resultCd", "");
                jsonParams.put("onePassToken", onepass);
                jsonParams.put("referenceNo", referenceNo);

                return jsonParams;
            }

        };
        URL.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
//        RequestHandler.getInstance(this).addToRequestQueue(URL);
        queue.add(URL);


    }

    public void sendToServer(){
        Intent intent = getIntent();
        String message = intent.getStringExtra(MESSAGE_KEY);
        String passtrx = intent.getStringExtra(POIN_KEY);

        Integer poin = Integer.parseInt(message);
        final Integer biaya = Integer.parseInt(harga);
        final Integer amt = poin*biaya;

        final String name = editText.getText().toString();
        final String point = String.valueOf(poin);
        final String id_imei = "23";
        final String total = String.valueOf(amt);
        final String alamat = editText6.getText().toString();


        Log.d("data poin", point);
        Log.d("REferensi no", referenceNo);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest URL = new StringRequest(Request.Method.POST, storeCC, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d("SUKSES", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error gan", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("poin", point);
                jsonParams.put("id_imei",id_imei);
                jsonParams.put("billingNm", name);
                jsonParams.put("harga", String.valueOf(amt));
                jsonParams.put("alamat", alamat);
                jsonParams.put("referenceno", referenceNo);

                return jsonParams;

            }

        };
//        RequestHandler.getInstance(this).addToRequestQueue(URL);
        queue.add(URL);
    }

    public class GeoWebViewClient extends WebViewClient {
        String aaa = "https://facebook.com";
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            Log.d("URL => ", aaa);    // current URL
            view.loadUrl(aaa);
            return true;
        }



    }

    public class NoErrorWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //  Log.e(String.valueOf(errorCode), description);

            // API level 5: WebViewClient.ERROR_HOST_LOOKUP
            //jika terjadi eror di webview
            //   if (errorCode == -2) {
            String summary = "<html><body style='background: black;'><p style='color: red;'>Silahkan cek koneksi internet anda.</p></body></html>";       view.loadData(summary, "text/html", null);
            AlertDialog.Builder errorDialog = new AlertDialog.Builder(TopupCCActivity.this);
            errorDialog.setTitle("Koneksi Error");
            errorDialog.setMessage("Silahkan cek koneksi internet anda");
            errorDialog.setNeutralButton("keluar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent exit = new Intent(Intent.ACTION_MAIN);
                            exit.addCategory(Intent.CATEGORY_HOME);
                            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            TopupCCActivity.this.finish();
                            startActivity(exit);
                        }
                    });
            AlertDialog errorAlert = errorDialog.create();
            errorAlert.show();
            return;
            // }

            // Default behaviour
        }
    }

    public void referensi(){
        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, refnumber, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        String hasil = response.toString();
                        Log.d("Response", response.toString());

                        try {
                            referenceNo = response.getString("invoice");
                            Log.d("refnumber", referenceNo);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "respon");
                    }
                }
        );
        //perintah request
        queue.add(getRequest);
    }


}
