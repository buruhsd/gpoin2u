package tuberpraka.gpoin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class MenuPanelAdapter extends RecyclerView.Adapter<MenuPanelAdapter.MenuPanelViewHolder>{

    private Context context;
    private ArrayList<Menu> data;
    private LayoutInflater inflater;
    MainActivity m;
    BPJSActivity b;
    Dialog dialog;
    Boolean hasil;
 String matauang = "";
    ArrayAdapter<CharSequence> adapter;

    EditText coba1 ;
     TextView coba2 ;
     Spinner coba3 ;
     Button coba4 ;
     Button coba5;
    double hslpoin=0;


    MenuPanelAdapter(Context context, ArrayList<Menu> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MenuPanelAdapter.MenuPanelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_menu, parent, false);
        return new MenuPanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MenuPanelAdapter.MenuPanelViewHolder holder, int position) {
        m = new MainActivity();
        if (m.cek_status(context)) {
            String kl = data.get(position).menu;
            holder.textView.setText(data.get(position).menu);
            holder.txtket.setText(data.get(position).ket);

            switch (kl){
                case "G-PAY":
                    holder.img.setImageResource(R.drawable.g_pay);
                    break;

                case "Pulsa Pra Bayar":
                    holder.img.setImageResource(R.drawable.g_pay);
                    break;

                case "Pulsa Pasca Bayar":
                    holder.img.setImageResource(R.drawable.pulsaa);
                    break;

                case "Pulsa Data (Internet)":
                    holder.img.setImageResource(R.drawable.pulsainternet);
                    break;

                case "PLN Pra Bayar":
                    holder.img.setImageResource(R.drawable.pln_pra_bayar);
                    break;

                case "PLN Pasca Bayar":
                    holder.img.setImageResource(R.drawable.pln_pra_bayar);
                    break;

                case "PDAM":
                    holder.img.setImageResource(R.drawable.pdam);
                    break;

                case "G-MULTI FINANCE":
                    holder.img.setImageResource(R.drawable.g_multi);
                    break;

                case "G-VOUCHER GAME":
                    holder.img.setImageResource(R.drawable.game_voucher);
                    break;

                case "G-TICKETING":
                    holder.img.setImageResource(R.drawable.ticket);
                    break;

                case "G-HOTEL":
                    holder.img.setImageResource(R.drawable.hotel);
                    break;

                case "G-VOUCHER":
                    holder.img.setImageResource(R.drawable.g_voucher);
                    break;

                case "DEPOSIT/TRANSFER":
                    holder.img.setImageResource(R.drawable.g_deposit);
                    break;

                case "Transfer GPOIN":
                    holder.img.setImageResource(R.drawable.transfer);
                    break;

                case "Deposit GPOIN":
                    holder.img.setImageResource(R.drawable.deposit);
                    break;

                case "Cicilan / Angsuran":
                    holder.img.setImageResource(R.drawable.angsuran);
                    break;

                case "TV Kabel":
                    holder.img.setImageResource(R.drawable.tvkabel);
                    break;

                case "Mobile Balance Money":
                    holder.img.setImageResource(R.drawable.pulsainternet);
                    break;

                case "BPJS":
                    holder.img.setImageResource(R.drawable.bpjs);
                    break;

                case "Convert GPOIN":
                    holder.img.setImageResource(R.drawable.convert);
                    break;

                case "INBOX":
                    holder.img.setImageResource(R.drawable.inbox);
                    break;

                case "Mutasi":
                    holder.img.setImageResource(R.drawable.mutasi);
                    break;

            }

            holder.rcv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogConfig lg = new LogConfig();
                    if(lg.session(context)) {
                        cektombol(data.get(holder.getAdapterPosition()).menu);
                    }else {
                        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        context.startActivity(intent);
                    }
                }
            });
        }else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are not connected internet. Please, connect first")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();



                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MenuPanelViewHolder extends RecyclerView.ViewHolder {
        TextView textView, txtket;
        ImageView img;
        LinearLayout rcv;

        MenuPanelViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.txv_row);
            txtket = (TextView) itemView.findViewById(R.id.txt_ket);
            img = (ImageView) itemView.findViewById(R.id.menu_img);
            rcv = (LinearLayout) itemView.findViewById(R.id.ln1);

        }
    }

    public void Pass(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_pass);
        final EditText  passlama, passbaru;
        Button btnpass;

        passlama = (EditText) dialog.findViewById(R.id.passlama);
        passbaru = (EditText) dialog.findViewById(R.id.passbaru);

        btnpass = (Button) dialog.findViewById(R.id.btnepass);

        btnpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passlama.getText().toString().equals("") || passbaru.getText().toString().equals("")){
                    Toast.makeText(context, "Data must be filled",Toast.LENGTH_SHORT).show();
                }else{
                    editpass(passlama.getText().toString(), passbaru.getText().toString());
                }
            }
        });
        dialog.setTitle("Edit Password");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void editpass(final String lama, final String baru) {
        RequestQueue queue = Volley.newRequestQueue(context);

        b = new BPJSActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        RoosterConnection r = new RoosterConnection(context,sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0"),
                sharedPreferences.getString(LogConfig.PASS_SESSION,"0"), "1",
                sharedPreferences.getString(LogConfig.NAMA_SESSION,"0"),
                sharedPreferences.getString(LogConfig.EMAIL_SESSION,"0"), id_imei);
//
            Log.e("changepass", "change");
        try {
            r.changePassword(baru);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }


        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_pass,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){
                            Pass();
                            Toast.makeText(context, "Wrong Data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Invalid Connection" , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lama", b.md5(lama));
                params.put("baru", b.md5(baru));
                params.put("id_imei", id_imei);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void TrxPass(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_trxpass);

        final EditText passtrxlama = (EditText) dialog.findViewById(R.id.passtrxlama);
        final EditText passtrxbaru = (EditText) dialog.findViewById(R.id.passtrxbaru);

        Button btntrxpass = (Button) dialog.findViewById(R.id.btnetrx);

        btntrxpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passtrxlama.getText().toString().equals("") || passtrxbaru.getText().toString().equals("")){
                    Toast.makeText(context, "Data must be filled",Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(context);
        b = new BPJSActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_trxpass,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if(response.equals("false")){
                            TrxPass();
                            Toast.makeText(context, "Wrong Data", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(context);
        b = new BPJSActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_poin_awal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
//                        Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
                        String point = "";
                        if(response.equals("false")) {

                            point ="0 Poin";
                        }else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                            point = jsonArray.getJSONObject(i).getString("poin");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

                                    builder.setMessage( "Your point :"+point
                                    )
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                    // ui = 0;

                                                    dialog.cancel();

                                                }
                                            })

                                    ;
                                    final android.app.AlertDialog alert = builder.create();
                                    alert.show();





                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
//    public Boolean cek_tombol(String response) {
//
////        return hs;
//    }
    public void cektombol(final String menu){
        RequestQueue queue = Volley.newRequestQueue(context);
       hasil = false;
//        b = new BPJSActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        final String id_imei =sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_cek_tombol,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
//                        hasil = response;
                        JSONObject jo = null;
                        hasil = false;
                        try {
                            jo = new JSONObject(response);
                            if (jo.getString("sukses").equals("false")) {
                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                                builder.setMessage(jo.getString("catatan"))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                            }
                                        })
                                ;
                                final android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }else{

                                switch (menu) {
                                    case "Pulsa Pra Bayar":
                                        Intent i = new Intent(context, PulsaActivity.class);

                                        context.startActivity(i);
                                        break;

                                    case "TV Kabel":
                                        Intent tvk = new Intent(context, PPOBActivity.class);
                                        tvk.putExtra("ket", "TV Kabel");
                                        context.startActivity(tvk);
                                        break;
                                    case "BPJS":
                                        Intent bpjs = new Intent(context, BPJSActivity.class);
//                        MainSubPanel.this.finish();
                                        context.startActivity(bpjs);
                                        break;
                                    case "Cicilan / Angsuran":
                                        Intent finance = new Intent(context, PPOBActivity.class);
                                        finance.putExtra("ket", "Cicilan / Angsuran");
                                        context.startActivity(finance);
                                        break;

                                    case "Pulsa Pasca Bayar":
                                        Intent ppb = new Intent(context, PPOBActivity.class);
                                        ppb.putExtra("ket", "Pulsa Pasca Bayar");
                                        context.startActivity(ppb);
                                        break;
                                    case "PDAM":
                                        Intent pdam = new Intent(context, PDAMActivity.class);
//                        MainSubPanel.this.finish();
                                        context.startActivity(pdam);
                                        break;

                                    case "Pulsa Data (Internet)":
                                        Intent inet = new Intent(context, PulsaActivity.class);
//                        MainSubPanel.this.finish();
                                        context.startActivity(inet);
                                        break;


                                    case "PLN Pra Bayar":
                                        Intent pln = new Intent(context, PlnActivity.class);

//                        MainSubPanel.this.finish();
                                        context.startActivity(pln);
                                        break;

                                    case "PLN Pasca Bayar":
                                        Intent pc = new Intent(context, PPOBActivity.class);
                                        pc.putExtra("ket", "PLN Pasca Bayar");
                                        context.startActivity(pc);
                                        break;

                                    case "G-VOUCHER GAME":
                                        Intent vg = new Intent(context, VGActivity.class);
//                        MainSubPanel.this.finish();
                                        context.startActivity(vg);
                                        break;
                                    case "Deposit GPOIN":
                                        Intent gt = new Intent(context, TopupActivity.class);
                                        context.startActivity(gt);
                                        break;
                                    case "Transfer GPOIN":
                                        Intent gtt = new Intent(context, TransferActivity.class);
                                        context.startActivity(gtt);
                                        break;
                                    case "INBOX":
                                        Intent ct = new Intent(context, ChatActivity.class);
                                        context.startActivity(ct);
                                        break;
                                    case "Profil":
                                        Intent st = new Intent(context, SettingActivity.class);
                                        st.putExtra("ket", "profil");
                                        context.startActivity(st);
                                        break;
                                    case "Edit Profil":
                                        Intent ep = new Intent(context, SettingActivity.class);
                                        ep.putExtra("ket", "edit");
                                        context.startActivity(ep);
                                        break;
                                    case "Edit Password":
                                        Pass();
                                        break;
                                    case "Edit Trx Password":
                                        TrxPass();
                                        break;
                                    case "Mutasi":
                                        Intent gp = new Intent(context, HistoryTabelActivity.class);
                                        context.startActivity(gp);
                                        break;
                                    case "Buy G POIN":
                                        Intent tp = new Intent(context, TopupActivity.class);
                                        context.startActivity(tp);
                                        break;

                                    case "Customer Service":
                                        Intent cs = new Intent(context, CustomerCareActivity.class);
                                        context.startActivity(cs);
                                        break;

                                    case "G-TICKETING":
                                        Intent gti = new Intent(context, ComingSoonActivity.class);
                                        context.startActivity(gti);
                                        break;
                                    case "G-HOTEL":
                                        Intent gho = new Intent(context, ComingSoonActivity.class);
                                        context.startActivity(gho);
                                        break;
                                    case "Balance Info":
                                        saldo();
                                        break;

                                    case "Convert GPOIN":
                                        conv();
                                        break;

                                    case "G-VOUCHER":
                                        Intent gv = new Intent(context, VoucherActivity.class);
                                        context.startActivity(gv);
                                        break;

                                    default:
                                        Intent intent = new Intent(context, MainSubPanel.class);
                                        intent.putExtra("menu",menu);
                                        context.startActivity(intent);
//                        Toast.makeText(context, data.get(holder.getAdapterPosition()).menu,Toast.LENGTH_SHORT).show();
                                        break;
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
                        Toast.makeText(context, "Invalid Connection" , Toast.LENGTH_SHORT).show();
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
        Log.e("Response log", String.valueOf(hasil));

    }

    public void conv(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.conveter_layout);


        coba1 = (EditText) dialog.findViewById(R.id.nominalstxt);
        coba2 = (TextView) dialog.findViewById(R.id.hasilconverttxt);
        coba3 = (Spinner) dialog.findViewById(R.id.matauangsspin);
        coba4 = (Button) dialog.findViewById(R.id.convertbtn);
        coba5 = (Button) dialog.findViewById(R.id.transfertbtn);

        adapter = ArrayAdapter.createFromResource(context,
                R.array.matauang, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coba3.setAdapter(adapter);

        coba3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                matauang= String.valueOf(item);
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
                Intent intent = new Intent(context, TransferActivity.class);
                intent.putExtra("poin", coba2.getText().toString());
//                        MainSubPanel.this.finish();
                context.startActivity(intent);
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

        coba2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public  void cek_convert(final String nama_paket){
        RequestQueue queue = Volley.newRequestQueue(context);

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


    }

}
