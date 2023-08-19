package com.radiostream.talitakum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.radiostream.talitakum.R;
import com.radiostream.talitakum.utilities.GPDR;

public class ActivitySplash extends AppCompatActivity {
    private InterstitialAd interstitialAd;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_splash);
        new CountDownTimer(1000, 1000) {
            public void onTick(long j) {
            }

            public void onFinish() {
                ActivitySplash.this.startActivity(new Intent(ActivitySplash.this.getBaseContext(), MainActivity.class));
                ActivitySplash.this.finish();
            }
        }.start();
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        this.interstitialAd = new InterstitialAd(getApplicationContext());
        this.interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
        this.interstitialAd.loadAd(GPDR.getAdRequest(this));
        this.interstitialAd.setAdListener(new AdListener() {
            public void onAdClosed() {
            }
        });
    }
}
