package tuberpraka.gpoin;

import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MerchantActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    AppBarLayout abl;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        abl = (AppBarLayout)findViewById(R.id.abl);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);


        final TabLayout.Tab transfer = tabLayout.newTab();
        final TabLayout.Tab history = tabLayout.newTab();
        final TabLayout.Tab help = tabLayout.newTab();
        final TabLayout.Tab logout = tabLayout.newTab();
        transfer.setText("Transaksi");
        history.setText("History");
        help.setText("Help");
        logout.setText("Logout");


        tabLayout.addTab(transfer, 0);
        tabLayout.addTab(history, 1);
        tabLayout.addTab(help, 2);
        tabLayout.addTab(logout, 3);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tabSelected)
            {
                if(tabSelected.getPosition() == 3){
//                    pvlogout();
                }else {
                    viewPager.setCurrentItem(tabSelected.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tabSelected){}

            @Override
            public void onTabReselected(TabLayout.Tab tabSelected){

            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorChoose));
//        tabLayout.


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
