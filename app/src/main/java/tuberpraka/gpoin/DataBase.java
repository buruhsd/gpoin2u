package tuberpraka.gpoin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mimin on 1/27/17.
 */

public class DataBase  extends Service{
//    public static final String NEW_MESSAGE = "com.gpoin.gpoin.newmessage";

    private BroadcastReceiver uiThreadMessageReceiver;
    private RequestQueue queue;
    String TAG ="database";
    int counter = 0;

    static final int UPDATE_INTERVAL = 5000;//5detik

    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(this, "The Service Started", Toast.LENGTH_LONG).show();
        queue = Volley.newRequestQueue(getApplicationContext());
        doSomethingRepeatedly();
        return START_STICKY;

    }

    @Override

    public void onDestroy() {

        super.onDestroy();

//        Toast.makeText(this, "The Service Destroyed", Toast.LENGTH_LONG).show();

    }

    public void cekstat() {
        Log.e(TAG, "masuk");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cekdb,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){

                        }else{
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    Intent in = new Intent(RoosterConnectionService.NEW_MESSAGE);
                                    in.setPackage(getApplicationContext().getPackageName());
                                    in.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, jsonArray.getJSONObject(i).getString("alasan"));
//Toast.makeText(getApplicationContext(), jsonArray.getJSONObject(i).getString("alasan"), Toast.LENGTH_SHORT ).show();
                                    getApplicationContext().sendBroadcast(in);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(), "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {         // Menambahkan parameters post
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);

                params.put("id_imei", sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0"));

                return params;
            }
        };
        queue.add(postRequest);


    }
    private void doSomethingRepeatedly() {

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                cekstat();
                Log.e(TAG, "cek data");

            }

        }, 0, UPDATE_INTERVAL);

    }



}
