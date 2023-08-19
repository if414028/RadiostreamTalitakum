package com.radiostream.talitakum.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.radiostream.talitakum.R;
import com.radiostream.talitakum.adapters.AdapterAbout;

import java.util.ArrayList;
import java.util.List;

public class ActivityAbout extends AppCompatActivity {
    private AdView adView;
    AdapterAbout adapterAbout;
    RecyclerView recyclerView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle((int) R.string.drawer_about);
        }
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapterAbout = new AdapterAbout(getDataInformation(), this);
        this.recyclerView.setAdapter(this.adapterAbout);
        loadAdMobBannerAd();
    }

    private List<Data> getDataInformation() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Data(R.drawable.ic_other_appname, getResources().getString(R.string.about_app_name), getResources().getString(R.string.app_name)));
        arrayList.add(new Data(R.drawable.ic_other_build, getResources().getString(R.string.about_app_version), getResources().getString(R.string.sub_about_app_version)));
        arrayList.add(new Data(R.drawable.ic_other_email, getResources().getString(R.string.about_app_email), getResources().getString(R.string.app_email)));
        arrayList.add(new Data(R.drawable.ic_other_copyright, getResources().getString(R.string.about_app_copyright), getResources().getString(R.string.sub_about_app_copyright)));
        arrayList.add(new Data(R.drawable.ic_other_rate, getResources().getString(R.string.about_app_rate), getResources().getString(R.string.sub_about_app_rate)));
        arrayList.add(new Data(R.drawable.ic_other_more, getResources().getString(R.string.about_app_more), getResources().getString(R.string.sub_about_app_more)));
        return arrayList;
    }

    public class Data {
        private int image;
        private String sub_title;
        private String title;

        public int getImage() {
            return this.image;
        }

        public String getTitle() {
            return this.title;
        }

        public String getSub_title() {
            return this.sub_title;
        }

        public Data(int i, String str, String str2) {
            this.image = i;
            this.title = str;
            this.sub_title = str2;
        }
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
