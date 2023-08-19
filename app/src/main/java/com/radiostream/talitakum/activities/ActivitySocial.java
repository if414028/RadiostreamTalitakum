package com.radiostream.talitakum.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.radiostream.talitakum.R;
import com.radiostream.talitakum.fragments.FragmentFacebook;
import com.radiostream.talitakum.fragments.FragmentTwitter;

public class ActivitySocial extends AppCompatActivity {
    public static int int_items = 2;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private AdView adView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.tab_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle((int) R.string.drawer_social);
        }
        loadAdMobBannerAd();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            public void run() {
                ActivitySocial.tabLayout.setupWithViewPager(ActivitySocial.viewPager);
            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            if (i == 0) {
                return new FragmentFacebook();
            }
            if (i != 1) {
                return null;
            }
            return new FragmentTwitter();
        }

        public int getCount() {
            return ActivitySocial.int_items;
        }

        public CharSequence getPageTitle(int i) {
            if (i == 0) {
                return ActivitySocial.this.getResources().getString(R.string.social_tab1);
            }
            if (i != 1) {
                return null;
            }
            return ActivitySocial.this.getResources().getString(R.string.social_tab2);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }

    private void loadAdMobBannerAd() {
        Log.d("Log", "Admob Banner is Disabled");
    }
}
