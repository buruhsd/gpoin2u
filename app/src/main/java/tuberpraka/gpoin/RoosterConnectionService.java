package tuberpraka.gpoin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import java.io.IOException;

public class RoosterConnectionService extends Service {
    private static final String TAG ="RoosterService";
    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private BroadcastReceiver mBroadcastReceiver;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.
        public static final String SEND_MESSAGE = "com.gpoin.gpoin.sendmessage";
    public static final String UI_AUTHENTICATED = "com.gpoin.gpoin.uiauthenticated";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";
    public static final String NEW_MESSAGE = "com.gpoin.gpoin.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";
    public static RoosterConnection.ConnectionState sConnectionState;
    public static RoosterConnection.LoggedInState sLoggedInState;
    String mUsername="", mPassword="",cekreg="1",nama="", email="", id_imei;
Intent intent;
    //the background thread.
    private RoosterConnection mConnection;

    public RoosterConnectionService() {
Log.e(TAG,"rc");
    }

    public static RoosterConnection.ConnectionState getState()
    {
        if (sConnectionState == null)
        {
            return RoosterConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static RoosterConnection.LoggedInState getLoggedInState()
    {
        if (sLoggedInState == null)
        {
            return RoosterConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate()");
    }

    private void initConnection()
    {
//        Toast.makeText(getApplicationContext(), mUsername+"k",Toast.LENGTH_SHORT).show();
Log.e(TAG, "initconnection");
        try
        {
            Boolean keadaan = mConnection.connect();
            mConnection.reconnectionSuccessful();
            intent = new Intent("status");
            intent.putExtra("status_login",keadaan );
            sendBroadcast(intent);

//            Toast.makeText(getApplicationContext(), keadaan+"l",Toast.LENGTH_SHORT).show();

        }catch (IOException |SmackException |XMPPException e)
        {
            Toast.makeText(getApplicationContext(),"Something went wrong with your connection",Toast.LENGTH_SHORT).show();
            Log.e(TAG,e.toString());

            //Stop the service all together.
//            stopSelf();
        }

    }
    public void start()
    {

        SharedPreferences sharedPreferences = getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(LogConfig.USERNAME_SESSION,"0");
        id_imei = sharedPreferences.getString(LogConfig.ID_IMEI_SESSION,"0");
        mPassword = sharedPreferences.getString(LogConfig.PASS_SESSION,"0");
        cekreg = sharedPreferences.getString(LogConfig.STAT_REG,"0");
        nama =sharedPreferences.getString(LogConfig.NAMA_SESSION,"0");
        email = sharedPreferences.getString(LogConfig.EMAIL_SESSION,"0");
        Log.e(TAG,"initConnection()");
//        Toast.makeText(getApplicationContext(), id_imei+"k",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), "Service Start() function called",Toast.LENGTH_SHORT).show();
        Log.e(TAG," Service Start() function called.");
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        if( mConnection == null)
                        {
                            mConnection = new RoosterConnection(getApplicationContext(),mUsername, mPassword, cekreg, nama, email, id_imei);
                            initConnection();
                              }
                        else{

                        }
                        initConnection();


                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }


    }

    public void stop()
    {
        Log.e(TAG,"stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if( mConnection != null)
                {
                    mConnection.disconnect();
                }

            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver);
        stop();
    }


}
