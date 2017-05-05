package tuberpraka.gpoin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UploadImageApacheHttp {

    public static final String TAG = "Upload Image Apache";
    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    public void doFileUpload(final String url, final Bitmap bmp, final Handler handler){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG, "Starting Upload...");

                nameValuePairs.add(new BasicNameValuePair("image", convertBitmapToString(bmp)));

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Log.e(TAG, "doFileUpload Response : " + responseStr);
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    Log.e(TAG,"Error in http connection " + e.toString());
                    handler.sendEmptyMessage(0);
                }
            }
        });
        t.start();

    }

    public String convertBitmapToString(Bitmap bmp){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
////        String encodedImage = Base64.encodeToString(byteArrayImage, Base64a.DEFAULT);
//        bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
//        byte[] byte_arr = stream.toByteArray();
//        String imageStr = Base64.encodeBytes(byte_arr);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte b [] = baos.toByteArray();
        String base64String = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e(TAG,"Error in http connection " + base64String);
        return base64String;
    }

    public void kirim(String selectedImagePath2) {
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        String image_str =  Base64.encodeToString(byte_arr, Base64.DEFAULT);
        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("image", image_str));
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://112.78.37.121/apig/gmember/controller_transaksi/upload");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);

                    new Runnable() {

                        @Override
                        public void run() {
//                            Toast.makeText(MainActivity.this, "Gambar berhasil diunggah ", Toast.LENGTH_LONG).show();
                        }
                    };
                }catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        t.start();
    }
}
