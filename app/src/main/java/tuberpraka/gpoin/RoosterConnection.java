package tuberpraka.gpoin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.chat.Chat;
//import org.jivesoftware.smack.chat.ChatManager;
//import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class RoosterConnection implements ConnectionListener{

    private static final String TAG = "RoosterConnection";
//    private Gson gson;
    private  Context mApplicationContext;
    String mUsername= "";
    String mPassword="";
    String cekreg ="";
    String email ="";
    String nama ="";
    String id_imei="";
    String mServiceName="nobita.harmonyb12.com";
    private static final String DOMAIN = "nobita.harmonyb12.com";
//    private XMPPTCPConnection mConnection;
    private static final int PORT = 5222;
     XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;
    private RequestQueue queue;

    public XMPPTCPConnection getmConnection() {
        return mConnection;
    }

    public void setmConnection(XMPPTCPConnection mConnection) {
        this.mConnection = mConnection;
    }

    public static enum ConnectionState
    {
        CONNECTED ,AUTHENTICATED, CONNECTING ,DISCONNECTING ,DISCONNECTED;
    }

    public static enum LoggedInState
    {
        LOGGED_IN , LOGGED_OUT;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
    public String getnama() {
        return nama;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getemail() {
        return email;
    }

    public void setnama(String nama) {
        this.nama = nama;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getCekreg() {
        return cekreg;
    }

    public void setCekreg(String cekreg) {
        this.cekreg = cekreg;
    }

    public String getId_imei() {
        return id_imei;
    }

    public void setId_imei(String id_imei) {
        this.id_imei = id_imei;
    }

    public RoosterConnection(Context context, String username, String pass, String cek, String nama, String email, String id_imei)
    {
        Log.e(TAG,"RoosterConnection Constructor called.");
        mApplicationContext = context.getApplicationContext();
//
////
////        Log.e(TAG, "Connecting " + mUsername+","+mPassword+","+cekreg);
        this.setmUsername( username);
        this.setmPassword(pass);
        this.setCekreg(cek);
        this.setemail(email);
        this.setnama(nama);
        this.setId_imei(id_imei);
        queue = Volley.newRequestQueue(mApplicationContext);
//SessionClass s = new SessionClass();
//        s.cek = cek;
//        s.password = pass;
//        s.username = username;
//        mUsername = "turbo168";
//        mPassword = "1";
//        cekreg="1";
//        SharedPreferences preferences;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            preferences = context.getSharedPreferences(LogConfig.SESSION_NAME, 0 | Context.MODE_MULTI_PROCESS);
//        } else {
//            preferences = context.getSharedPreferences(LogConfig.SESSION_NAME, 0);
//        }
////        SharedPreferences preferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
//        mUsername = preferences.getString(LogConfig.
//                USERNAME_SESSION, "0");
//        mPassword = preferences.getString(LogConfig.PASS_SESSION,"0");
//        cekreg = preferences.getString(LogConfig.STAT_REG,"0");
    }


    public boolean connect() throws IOException,XMPPException,SmackException
    {
        boolean a =false;
        Log.e(TAG, "Connecting to server " + id_imei);
        DomainBareJid serviceName = JidCreate.domainBareFrom(DOMAIN);
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(this.getId_imei(), this.getmPassword());
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
configBuilder.setDebuggerEnabled(true);


        configBuilder.setResource("Android");
        configBuilder.setXmppDomain(serviceName);
//        configBuilder.setHost(mServiceName);
        configBuilder.setPort(PORT);
//        configBuilder.setSendPresence(false);
//        configBuilder.setRosterLoadedAtLogin(true);
//        configBuilder.setResource("otomax");

        mConnection = new XMPPTCPConnection(configBuilder.build());
        mConnection.addConnectionListener(this);

//        Log.e(TAG, SASLAuthentication.isSaslMechanismRegistered().toString()+"sasl");
        try {
            mConnection.connect();
//            mConnection.login();

//            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
//            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
//            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            if(cekreg !=null && cekreg.equalsIgnoreCase("1")) {
                mConnection.login(Localpart.from(this.getId_imei()), this.getmPassword(), null);

            }else  if(cekreg !=null && cekreg.equalsIgnoreCase("2")) {
                AccountManager accountManager = AccountManager.getInstance(mConnection);


                Map<String, String> map = new HashMap<String, String>();
                map.put("username", this.getId_imei());
                map.put("name", this.getnama());
                map.put("password", this.getmPassword());
                map.put("email", this.getemail());
                map.put("creationDate", "" + System.currentTimeMillis() / 1000L);

                    if (accountManager.supportsAccountCreation()) {
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(Localpart.from(id_imei), mPassword, map);

                        a=true;
                        Log.e(TAG, "berhasil1");
                    }
                if(a){
                    mConnection.login(Localpart.from(this.getId_imei()), this.getmPassword(), null);
                }
            }

            SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LogConfig.STAT_REG, "1");
            editor.apply();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();
        setupUiThreadBroadCastMessageReceiver();
//        messageListener = new MyMessageListener();
        this.setmConnection(mConnection);
        return mConnection.isAuthenticated();

    }


    public void changePassword(String password) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, XmppStringprepException {
//        AccountManager accountManager = new AccountManager(mConnection);

        DomainBareJid serviceName = JidCreate.domainBareFrom(DOMAIN);
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(this.getId_imei(), this.getmPassword());
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);


        configBuilder.setResource("Android");
        configBuilder.setXmppDomain(serviceName);
//        configBuilder.setHost(mServiceName);
        configBuilder.setPort(PORT);
        mConnection = new XMPPTCPConnection(configBuilder.build());
        mConnection.addConnectionListener(this);

//        try {


                AccountManager accountManager = AccountManager.getInstance(mConnection);
                try {
                    accountManager.changePassword(password); // Change password
                   Log.e(TAG,"ganti pass");

                } catch (XMPPException e1) {
                    System.out.println(e1.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            SharedPreferences sharedPreferences = mApplicationContext.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LogConfig.STAT_REG, "1");
            editor.apply();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();
        setupUiThreadBroadCastMessageReceiver();
//        messageListener = new MyMessageListener();
//        this.setmConnection(mConnection);

    }


    public void disconnect()
    {
        Log.e(TAG,"Disconnecting from server "+ mServiceName);
        if (mConnection != null)
        {
            mConnection.disconnect();
        }

        mConnection = null;


    }


    @Override
    public void connected(XMPPConnection connection) {
        RoosterConnectionService.sConnectionState= ConnectionState.CONNECTED;
//        Toast.makeText(.this, response.toString() , Toast.LENGTH_SHORT).show();
        Log.e(TAG,"Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        RoosterConnectionService.sConnectionState= ConnectionState.AUTHENTICATED;
        Log.e(TAG,"Authenticated Successfully");
//        Toast.makeText(mApplicationContext, "Authenticated Successfully" , To ast.LENGTH_SHORT).show();
    }




    @Override

    public void connectionClosed() {
        RoosterConnectionService.sConnectionState= ConnectionState.DISCONNECTED;
        Log.e(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        RoosterConnectionService.sConnectionState= ConnectionState.DISCONNECTED;
        Log.e(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.e(TAG,"ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.e(TAG,"ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.e(TAG,"ReconnectionFailed()");

    }

    private void setupUiThreadBroadCastMessageReceiver()
    {
        try {
//            Toast.makeText(getAppli, "R",Toast.LENGTH_SHORT).show();
            final ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);
            Jid jid = JidCreate.from(this.getId_imei()+"@nobita.harmonyb12.com");
            Log.e(TAG, "Connecting " + this.getId_imei()+","+mPassword+","+cekreg);
            Chat newChat = chatmanager.createChat((EntityJid) jid,new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
//                    Toast.makeText(mApplicationContext, "R"+message.getBody(),Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"message a :"+  message.getBody());
                    String from = String.valueOf(message.getFrom());
                    Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
                    intent.setPackage(mApplicationContext.getPackageName());
                    intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, from);
                    intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,message.getBody().toString());
                    mApplicationContext.sendBroadcast(intent);
                    message(message.getBody());
                }
            });
            chatmanager.addChatListener(
                    new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally)
                        {
                            if (!createdLocally)
                                chat.addMessageListener(new ChatMessageListener() {
                                    @Override
                                    public void processMessage(Chat chat, Message message) {
                                        Log.e(TAG,"message i:"+  message.getBody());
                                        String from = String.valueOf(message.getFrom());
                                        Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
                                        intent.setPackage(mApplicationContext.getPackageName());
                                        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, from);
                                        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,message.getBody().toString());
                                        mApplicationContext.sendBroadcast(intent);
                                        message(message.getBody());
//                                        Toast.makeText(mApplicationContext, "R"+message.getBody(),Toast.LENGTH_SHORT).show();
                                    }
                                });;
                        }
                    });
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"BroadcastMessage");
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check if the Intents purpose is to send the message.
                String action = intent.getAction();
                if( action.equals(RoosterConnectionService.SEND_MESSAGE))
                {
                    Log.e(TAG,"BroadcastMessage");
                    //SENDS THE ACTUAL MESSAGE TO THE SERVER
                    sendMessage(intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY),
                            intent.getStringExtra(RoosterConnectionService.BUNDLE_TO));
                }else{

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(RoosterConnectionService.SEND_MESSAGE);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver,filter);


    }

    private void sendMessage ( String body ,String toJid)
    {
        try {
            Jid jid = JidCreate.from(toJid+"");
            Log.e(TAG,"Sending message to :"+ toJid);
            Chat chat = ChatManager.getInstanceFor(mConnection).
            createChat(jid.asEntityBareJidIfPossible());
            chat.sendMessage(body);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }



    }
//    @Override
//    public void processMessage(Chat chat, Message message) {
////        Toast.makeText(mApplicationContext, "R",Toast.LENGTH_SHORT).show();
//        Log.e(TAG,"message.getBody() :"+message.getBody());
//        Log.e(TAG,"message.getFrom() :"+message.getFrom());
//
//        String from = String.valueOf(message.getFrom());
//        String contactJid="";
////        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
////        if ( from.contains("/"))
////        {
////            contactJid = from.split("/")[0];
////            Log.d(TAG,"The real jid is :" +contactJid);
////        }else
////        {
////            contactJid=from;
////        }
//
//        //Bundle up the intent and send the broadcast.
//
//
//
//
//        Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
//        intent.setPackage(mApplicationContext.getPackageName());
//        intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID,from);
//        intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,message.toString());
//        mApplicationContext.sendBroadcast(intent);
//        Log.e(TAG,"Received message from :"+contactJid+" broadcast sent.");
//
//
//    }

    public void message(String body){
//        if(loadingbeli.isShowing()){
//            loadingbeli.dismiss();

//        }
        String status="";
        String kode="";
        if(body.toUpperCase().contains("Format salah")||body.toUpperCase().contains("GAGAL")){
            status="2";
        }else if(body.toUpperCase().contains("masih dalam proses") || body.toUpperCase().contains("akan segera diproses")){
            status="3";
        }else if(body.toUpperCase().contains("SUKSES")){
            status="4";
        }else{
            status="5";
        }

//        if(body.toUpperCase().contains("cek".toUpperCase())){
//            body = body.replace("ID:TEST00001","|");
//        }
String alasan="";
        String[] data = body.split("\\-");
//        if(data.length >=3) {
//            for (int i = 0; i < data.length - 1; i++) {
//                alasan = alasan + data[i];
//            }
//        }else{
            alasan = body;
//        }
if(body.toUpperCase().contains("cek".toUpperCase())){
    if(status.equals("4")){
        String[] datasukses = body.split(" ");
        if(body.toUpperCase().contains("pln".toUpperCase())) {
            String ket1[] = body.split("\\.");
            String ket2 = ket1[3];

            providerbpjs(alasan, status, data[0]);
        }else if(body.toUpperCase().contains("PDAM".toUpperCase()) || body.toUpperCase().contains("pln".toUpperCase())){

        }else{
            providerbpjs(alasan, status, data[0]);
        }
        Log.e("cek pesan",datasukses[2]);
    }else {
//        if (data.length >= 2) {
            providerbpjs(alasan, status, data[0]);
//        } else {
//            providerbpjs(body, status, data[0]);
//        }
    }
}else if(body.toUpperCase().contains("byr".toUpperCase())) {

    if (data.length >= 2) {
        providerppob(alasan, status, data[0]);
    }else{
        providerppob(alasan, status, data[0]);
    }
} else {
//pulsa
    if (data.length >= 2) {
        provider(alasan, status, data[0]);
    } else if (data.length <= 1) {
        provider(alasan, status, data[0]);
    }
}
//Toast.makeText(mApplicationContext, data[0], Toast.LENGTH_SHORT).show();
        onNotifShow(body);
    }


    public void provider(final String body, final String status, final String kode){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_simpan_alasan_byr_pulsa,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response provider"+body+","+status+","+kode, response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString() );
//                        Toast.makeText(PulsaActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("alasan", body);
                params.put("status", status);
                params.put("id_imei", id_imei);
                params.put("kode", kode);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void providerppob(final String body, final String status, final String kode){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_simpan_alasan_byr_ppob,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response providerppob", response);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString() );
//                        Toast.makeText(PulsaActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("alasan", body);
                params.put("status", status);
                params.put("id_imei", id_imei);
                params.put("kode", kode);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void providerbpjs(final String body, final String status, final String kode){

        StringRequest postRequest = new StringRequest(Request.Method.POST, LogConfig.url_simpan_alasan_cek,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // menampilkan respone
                        Log.e("Response providerbpjs", response);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString() );
//                        Toast.makeText(PulsaActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {         // Menambahkan parameters post
                Map<String, String>  params = new HashMap<String, String>();
                params.put("alasan", body);
                params.put("status", status);
                params.put("id_imei", id_imei);
                params.put("kode", kode);
                return params;
            }
        };
        queue.add(postRequest);
    }
    public void onNotifShow(String message) {
        Intent intent = new Intent(mApplicationContext, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mApplicationContext.getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mApplicationContext)
                .setAutoCancel(true)
                .setContentTitle("G-POIN Notif")
                .setContentText(message)
                .setSmallIcon(R.drawable.logo_gpoin_png)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {1000,1000})
                .setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager manager = (NotificationManager) mApplicationContext.getSystemService(mApplicationContext.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }


}



