package tuberpraka.gpoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogConfig {
    public static final String SESSION_NAME = "myloginapp";
    public static final String PASS_SESSION = "pass";
    public static final String USERNAME_SESSION = "username";
    public static final String EMAIL_SESSION = "email";
    public static final String NAMA_SESSION = "nama";
    public static final String NAMAACC_SESSION = "namaacc";
    public static final String ID_IMEI_SESSION = "id_imei";
    public static final String SESSION_TIME = "time_s";
    public static final String ID_SESSION = "id_member";
    public static final String LOGGEDIN_SHARED_PREF = "loggin";
    public static final String STAT_REG = "cek";
    public static final String LOGO = "logo";
    public static final String ID_BISNIS = "bisnis";
    public static final String ALIAS_MENU = "menu";
    String hasil;

    public static final String url_beli= "http://112.78.37.121/apig/gmember/controller_transaksi/beli";
    public static final String url_convert= "http://112.78.37.121/apig/gmember/controller_transaksi/convert";
    public static final String url_convert_transaksi= "http://112.78.37.121/apig/gmember/controller_transaksi/convert_transaksi";
    public static final String url_cek_trx= "http://112.78.37.121/apig/gmember/controller_transaksi/cek_trxpass";
    public static final String url_cek= "http://112.78.37.121/apig/gmember/controller_transaksi/insert_cek";

    public static final String url_paket_pdam = "http://112.78.37.121/apig/gmember/controller_pdam/paket";
    public static final String url_paket_pulsa = "http://112.78.37.121/apig/gmember/controller_pulsa/paket";
    public static final String url_poin_awal = "http://112.78.37.121/apig/gmember/controller_membernya/poin_awal";
    public static final String url_aktif = "http://112.78.37.121/apig/gmember/controller_membernya/aktif";

    public static final String url_paket_ppob= "http://112.78.37.121/apig/gmember/controller_ppob/paket";
    public static final String url_paket_pln= "http://112.78.37.121/apig/gmember/controller_pulsa_listrik/paket";
    public static final String url_paket_vg= "http://112.78.37.121/apig/gmember/controller_vg/paket";
    public static final String url_prov_vg= "http://112.78.37.121/apig/gmember/controller_vg/provider";

    public static final String url_simpan_alasan_cek= "http://112.78.37.121/apig/gmember/controller_transaksi/simpan_alasan_cek";
    public static final String url_simpan_alasan_byr_pulsa= "http://112.78.37.121/apig/gmember/controller_transaksi/simpan_alasan_byr";
    public static final String url_simpan_alasan_byr_ppob= "http://112.78.37.121/apig/gmember/controller_transaksi/simpan_alasan_byr_ppob";


    public static final String provinsi= "http://112.78.37.121/apig/gmember/controller_membernya/provinsi";
    public static final String kota= "http://112.78.37.121/apig/gmember/controller_membernya/city";
    public static final String profil= "http://112.78.37.121/apig/gmember/controller_membernya/profil";
    public static final String save_profil= "http://112.78.37.121/apig/gmember/controller_membernya/save_profil";

    public static final String url_pass= "http://112.78.37.121/apig/gmember/controller_membernya/update_pass";
    public static final String url_trxpass= "http://112.78.37.121/apig/gmember/controller_membernya/update_trx";

    public static final String url_transfer= "http://112.78.37.121/apig/gmember/controller_transfer/transfer";
    public static final String url_login = "http://112.78.37.121/apig/gmember/controller_membernya/login";

    public static final String url_cek_status = "http://112.78.37.121/apig/gmember/controller_topup/cek";

    public static final String url_selectall_topup = "http://112.78.37.121/apig/gmember/controller_topup/sellect_all_gpoin";
    public static final String url_selectall_history = "http://112.78.37.121/apig/gmember/controller_topup/sellect_all_history";
    public static final String url_search_topup = "http://112.78.37.121/apig/gmember/controller_topup/search_gpoin";
    public static final String url_search_month= "http://112.78.37.121/apig/gmember/controller_topup/search_gpoin_month";

    public static final String url_convert_topup = "http://112.78.37.121/apig/gmember/controller_topup/convert_beli";
    public static final String url_beli_topup = "http://112.78.37.121/apig/gmember/controller_topup/beli";
    public static final String url_bank_topup = "http://112.78.37.121/apig/gmember/controller_topup/bank";
    public static final String url_konfirmasi_topup = "http://112.78.37.121/apig/gmember/controller_topup/konfirmasi";

    public static final String url_list_cs = "http://112.78.37.121/apig/gmember/controller_cs/hasil";
    public static final String url_gambar = "http://112.78.37.121/apig/gmember/controller_topup/upload";
    public static final String url_register = "http://112.78.37.121/apig/gmember/controller_membernya/register";
    public static final String UPLOAD_URL = "http://112.78.37.121/apig/gmember/controller_transaksi/upload";
    public static final String url_cekdb= "http://112.78.37.121/apig/gmember/controller_transaksi/cekstat";


    public static final String url_hasil= "http://112.78.37.121/apig/gmember/controller_chat/hasil";
    public static final String url_search= "http://112.78.37.121/apig/gmember/controller_chat/search";


    public static final String url_bank_member = "http://112.78.37.121/apig/gmember/controller_topup/bank_member";
    public static final String url_metode = "http://112.78.37.121/apig/gmember/controller_topup/motede_payment";
    public static final String url_cek_tombol = "http://112.78.37.121/apig/gmember/controller_chat/cek_tombol";
    public static final String url_persen = "http://112.78.37.121/apig/gmember/controller_transfer/persen_ppn";


    public static final String url_forgot = "http://112.78.37.121/apig/gmember/controller_membernya/forgot_pass";
    public static final String url_ppob_cek= "http://112.78.37.121/apig/gmember/controller_transaksi/ppob_cek";
    public static final String url_beli_ppob= "http://112.78.37.121/apig/gmember/controller_transaksi/beli_setelah_cek";

    public static final String url_news= "http://112.78.37.121/apig/gmember/controller_cs/news";
    public static final String url_cek_fee= "http://112.78.37.121/apig/gmember/controller_ppob/cek_fee";
    public static final String url_cek_versi= "http://112.78.37.121/apig/gmember/controller_cs/versi";
    static final String URL_PRINT_MUTASI = "http://112.78.37.121/apig/gmember/controller_topup/search_gpoinpdf";
    static final String url_paket_voucher = "http://112.78.37.121/apig/gmember/controller_voucher/paket";
    static final String url_jenis_voucher = "http://112.78.37.121/apig/gmember/controller_voucher/jenis";
    static final String url_beli_voucher = "http://112.78.37.121/apig/gmember/controller_voucher/beli";
    static final String url_cek_trx_voucher = "http://112.78.37.121/apig/gmember/controller_voucher/cek_trxpass";
    public static final String url_cek_kode= "http://112.78.37.121/apig/gmember/controller_membernya/cek_kode";
    public static final String url_insert_kode= "http://112.78.37.121/apig/gmember/controller_membernya/insert_kode";
    static final String url_history_voucher = "http://112.78.37.121/apig/gmember/controller_voucher/history";
    public static final String url_bisnis= "http://112.78.37.121/apig/gmember/controller_chat/bisnis";


    //coms


//    public static final String url_beli= "https://gpoin2u.com/gmember/controller_transaksi/beli";
//    public static final String url_convert= "https://gpoin2u.com/gmember/controller_transaksi/convert";
//    public static final String url_convert_transaksi= "https://gpoin2u.com/gmember/controller_transaksi/convert_transaksi";
//    public static final String url_cek_trx= "https://gpoin2u.com/gmember/controller_transaksi/cek_trxpass";
//    public static final String url_cek= "https://gpoin2u.com/gmember/controller_transaksi/insert_cek";
//
//    public static final String url_paket_pdam = "https://gpoin2u.com/gmember/controller_pdam/paket";
//    public static final String url_paket_pulsa = "https://gpoin2u.com/gmember/controller_pulsa/paket";
//    public static final String url_poin_awal = "https://gpoin2u.com/gmember/controller_membernya/poin_awal";
//    public static final String url_aktif = "https://gpoin2u.com/gmember/controller_membernya/aktif";
//
//    public static final String url_paket_ppob= "https://gpoin2u.com/gmember/controller_ppob/paket";
//    public static final String url_paket_pln= "https://gpoin2u.com/gmember/controller_pulsa_listrik/paket";
//    public static final String url_paket_vg= "https://gpoin2u.com/gmember/controller_vg/paket";
//    public static final String url_prov_vg= "https://gpoin2u.com/gmember/controller_vg/provider";
//
//    public static final String url_simpan_alasan_cek= "https://gpoin2u.com/gmember/controller_transaksi/simpan_alasan_cek";
//    public static final String url_simpan_alasan_byr_pulsa= "https://gpoin2u.com/gmember/controller_transaksi/simpan_alasan_byr";
//    public static final String url_simpan_alasan_byr_ppob= "https://gpoin2u.com/gmember/controller_transaksi/simpan_alasan_byr_ppob";
//
//
//    public static final String provinsi= "https://gpoin2u.com/gmember/controller_membernya/provinsi";
//    public static final String kota= "https://gpoin2u.com/gmember/controller_membernya/city";
//    public static final String profil= "https://gpoin2u.com/gmember/controller_membernya/profil";
//    public static final String save_profil= "https://gpoin2u.com/gmember/controller_membernya/save_profil";
//
//    public static final String url_pass= "https://gpoin2u.com/gmember/controller_membernya/update_pass";
//    public static final String url_trxpass= "https://gpoin2u.com/gmember/controller_membernya/update_trx";
//
//    public static final String url_transfer= "https://gpoin2u.com/gmember/controller_transfer/transfer";
//    public static final String url_login = "https://gpoin2u.com/gmember/controller_membernya/login";
//
//    public static final String url_cek_status = "https://gpoin2u.com/gmember/controller_topup/cek";
//
//    public static final String url_selectall_topup = "https://gpoin2u.com/gmember/controller_topup/sellect_all_gpoin";
//    public static final String url_selectall_history = "https://gpoin2u.com/gmember/controller_topup/sellect_all_history";
//    public static final String url_search_topup = "https://gpoin2u.com/member/controller_topup/search_gpoin";
//    public static final String url_search_month= "https://gpoin2u.com/gmember/controller_topup/search_gpoin_month";
//
//    public static final String url_convert_topup = "https://gpoin2u.com/gmember/controller_topup/convert_beli";
//    public static final String url_beli_topup = "https://gpoin2u.com/gmember/controller_topup/beli";
//    public static final String url_bank_topup = "https://gpoin2u.com/gmember/controller_topup/bank";
//    public static final String url_konfirmasi_topup = "https://gpoin2u.com/gmember/controller_topup/konfirmasi";
//
//    public static final String url_list_cs = "https://gpoin2u.com/gmember/controller_cs/hasil";
//    public static final String url_gambar = "https://gpoin2u.com/gmember/controller_topup/upload";
//    public static final String url_register = "https://gpoin2u.com/gmember/controller_membernya/register";
//    public static final String UPLOAD_URL = "https://gpoin2u.com/gmember/controller_transaksi/upload";
//    public static final String url_cekdb= "https://gpoin2u.com/gmember/controller_transaksi/cekstat";
//
//
//    public static final String url_hasil= "https://gpoin2u.com/gmember/controller_chat/hasil";
//    public static final String url_search= "https://gpoin2u.com/gmember/controller_chat/search";
//
//
//    public static final String url_bank_member = "https://gpoin2u.com/gmember/controller_topup/bank_member";
//    public static final String url_metode = "https://gpoin2u.com/gmember/controller_topup/motede_payment";
//    public static final String url_cek_tombol = "https://gpoin2u.com/gmember/controller_chat/cek_tombol";
//    public static final String url_persen = "https://gpoin2u.com/gmember/controller_transfer/persen_ppn";
//
//
//    public static final String url_forgot = "https://gpoin2u.com/gmember/controller_membernya/forgot_pass";
//    public static final String url_ppob_cek= "https://gpoin2u.com/gmember/controller_transaksi/ppob_cek";
//    public static final String url_beli_ppob= "https://gpoin2u.com/gmember/controller_transaksi/beli_setelah_cek";
//
//    public static final String url_news= "https://gpoin2u.com/gmember/controller_cs/news";
//    public static final String url_cek_fee= "https://gpoin2u.com/gmember/controller_ppob/cek_fee";
//    public static final String url_cek_versi= "https://gpoin2u.com/gmember/controller_cs/versi";
//    static final String URL_PRINT_MUTASI = "https://gpoin2u.com/gmember/controller_topup/search_gpoinpdf";
//    static final String url_paket_voucher = "https://gpoin2u.com/gmember/controller_voucher/paket";
//    static final String url_jenis_voucher = "https://gpoin2u.com/gmember/controller_voucher/jenis";
//    static final String url_beli_voucher = "https://gpoin2u.com/gmember/controller_voucher/beli";
//    static final String url_cek_trx_voucher = "https://gpoin2u.com/gmember/controller_voucher/cek_trxpass";
//    public static final String url_cek_kode= "https://gpoin2u.com/gmember/controller_membernya/cek_kode";
//    public static final String url_insert_kode= "https://gpoin2u.com/gmember/controller_membernya/insert_kode";
//    static final String url_history_voucher = "https://gpoin2u.com/gmember/controller_voucher/history";



    public boolean session(Context context){
        boolean hasil = false;
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = s.format(new Date());

        SharedPreferences sharedPreferences = context.getSharedPreferences(LogConfig.SESSION_NAME, context.MODE_PRIVATE);
        String time_s =sharedPreferences.getString(LogConfig.SESSION_TIME,"0");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.e("time", time_s+","+format);
        if(Double.parseDouble(time_s)+3000 <= Double.parseDouble(format)){

            editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, false);
            editor.apply();

            hasil = false;
        }else{
            editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, true);
            editor.putString(LogConfig.SESSION_TIME, format);
            editor.apply();
            hasil = true;

        }
        return hasil;
    }



}

