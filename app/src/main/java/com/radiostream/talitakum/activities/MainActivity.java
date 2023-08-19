package com.radiostream.talitakum.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;
import com.radiostream.talitakum.R;
import com.radiostream.talitakum.fragments.FragmentRadioAdminPanel;
import com.radiostream.talitakum.utilities.GPDR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int COLLAPSING_TOOLBAR = 0;
    private static final String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    public static String FRAGMENT_CLASS = "transation_target";
    public static String FRAGMENT_DATA = "transaction_data";
    private static final String SELECTED_TAG = "selected_index";
    private static int selectedIndex;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private AdView adView;
    private DrawerLayout drawerLayout;
    private InterstitialAd interstitialAd;
    private NavigationView navigationView;
    private OnBackClickListener onBackClickListener;

    public interface OnBackClickListener {
        boolean onBackClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadInterstitialAd();
        this.navigationView = (NavigationView) findViewById(R.id.navigation_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (savedInstanceState != null) {
            this.navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }
        selectedIndex = 0;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentRadioAdminPanel(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
        loadAdMobBannerAd();
        GPDR.updateConsentStatus(this);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(SELECTED_TAG, selectedIndex);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_about:
                startActivity(new Intent(getApplicationContext(), ActivityAbout.class));
                showInterstitialAd();
                break;
            case R.id.drawer_home:
                this.drawerLayout.closeDrawer((int) GravityCompat.START);
                break;
            case R.id.drawer_more:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.play_more_apps))));
                showInterstitialAd();
                break;
            case R.id.drawer_privacy:
                startActivity(new Intent(getApplicationContext(), ActivityPrivacyPolicy.class));
                showInterstitialAd();
                break;
            case R.id.drawer_rate:
                String packageName = getPackageName();
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                } catch (ActivityNotFoundException unused) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
                showInterstitialAd();
                break;
            case R.id.drawer_social:
                startActivity(new Intent(getApplicationContext(), ActivitySocial.class));
                showInterstitialAd();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setupNavigationDrawer(Toolbar toolbar) {
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener2) {
        this.onBackClickListener = onBackClickListener2;
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout2 = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout2.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout2.closeDrawer((int) GravityCompat.START);
            return;
        }
        OnBackClickListener onBackClickListener2 = this.onBackClickListener;
        if (onBackClickListener2 == null || !onBackClickListener2.onBackClick()) {
            super.onBackPressed();
        }
    }

    private void loadAdMobBannerAd() {
        this.adView = (AdView) findViewById(R.id.adView);
        this.adView.setVisibility(View.INVISIBLE);
        this.adView.setVisibility(View.GONE);
        Log.d("Log", "Admob Banner is Disabled");
    }

    private void loadInterstitialAd() {
        Log.d("INFO", "AdMob Interstitial is Disabled");
    }

    private void showInterstitialAd() {
        Log.d("INFO", "AdMob Interstitial is Disabled");
    }

}