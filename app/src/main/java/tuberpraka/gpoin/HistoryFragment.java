package tuberpraka.gpoin;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HistoryFragment extends Fragment {

    ListCsAdapter adapter;
    ArrayList<csClass> data;
    private RequestQueue queue;
    String username ="", id_imei="";
    SharedPreferences sharedPreferences;
    TextView  txtcs;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_customer_care, container, false);
        txtcs = (TextView)view.findViewById(R.id.txtcs);
//        tltopup = (TableLayout) findViewById(R.id.rcs);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        sharedPreferences = getActivity().getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        username =sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0");
//        id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");

        username ="mimin";
        id_imei ="23";
        queue = Volley.newRequestQueue(getActivity());

        hasil();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_customer_care, container, false);
    }



    public void hasil(){


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_list_cs,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){

                            Toast.makeText(getContext(), "Sorry, no data found" , Toast.LENGTH_SHORT).show();
                        }else{
                            JSONObject jo = null;
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                data = new ArrayList<>();
//                                    Toast.makeText(ChatActivity.this, jsonArray.length()+"" , Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    txtcs.setText(txtcs.getText().toString()+jsonArray.getJSONObject(i).getString("jenis")+": "+jsonArray.getJSONObject(i).getString("nomor")+"\n\n");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.toString() , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(),"Invalid Connection" , Toast.LENGTH_SHORT).show();
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
