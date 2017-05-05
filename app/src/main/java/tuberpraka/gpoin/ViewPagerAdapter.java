package tuberpraka.gpoin;

/**
 * Created by mimin on 1/31/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Admin on 11-12-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context =context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment i = new Fragment();
        switch (position){
            case 0:
                return new HistoryFragment();
            case 1:
                return new CcFragment();
            case 2:
//                return new HistoryLinearActivity();
            case 3:
//                return new HelpActivity();
            case 4:
//                pvlogout();
                break;
//                return new LogoutFragment();
//                return new TabFragment();
        }
        return i;    // Which Fragment should be dislpayed by the viewpager for the given position
        // In my case we are showing up only one fragment in all the three tabs so we are
        // not worrying about the position and just returning the TabFragment
    }

    @Override
    public int getCount() {
        return 4;           // As there are only 3 Tabs
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Transaksi";
            case 1:
                return "History";
            case 2:
                return "Help";
            case 3:
                return "Logout";
        }
        return "Default Text";
    }

    public void pvlogout(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Anda Yakin Mau Logout ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        SharedPreferences preferences = context.getSharedPreferences(LogConfig.SESSION_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putBoolean(LogConfig.LOGGEDIN_SHARED_PREF, false);
                        editor.putString(LogConfig.ID_SESSION, "");
                        editor.putString(LogConfig.USERNAME_SESSION, "");
                        editor.putString(LogConfig.PASS_SESSION, "");


                        editor.commit();

//                        Intent intent = new Intent(context.getApplicationContext(), HomeActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("EXIT", true);
//                        context. startActivity(intent);

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();


                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        // txthasil.setText("Anda Login di device yang berbeda");
    }

}
