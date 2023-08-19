package com.radiostream.talitakum.utilities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.radiostream.talitakum.R;

import com.google.ads.consent.ConsentForm;

import java.net.MalformedURLException;
import java.net.URL;

public class GPDR {
    public static AdRequest getAdRequest(Activity activity) {
        return new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, getBundleAd(activity)).build();
    }

    public static Bundle getBundleAd(Activity activity) {
        Bundle bundle = new Bundle();
        if (ConsentInformation.getInstance(activity).getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
            bundle.putString("npa", "1");
        }
        return bundle;
    }

    public static void updateConsentStatus(final Activity activity) {
        ConsentInformation.getInstance(activity).requestConsentInfoUpdate(new String[]{activity.getString(R.string.admob_publisher_id)}, new ConsentInfoUpdateListener() {
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    new GDPRForm(activity).displayConsentForm();
                }
            }

            public void onFailedToUpdateConsentInfo(String str) {
                Log.e("GDPR", str);
            }
        });
    }

    private static class GDPRForm {
        private Activity activity;
        /* access modifiers changed from: private */
        public ConsentForm consentForm;

        private GDPRForm(Activity activity2) {
            this.activity = activity2;
        }

        /* access modifiers changed from: private */
        public void displayConsentForm() {
            Activity activity2 = this.activity;
            ConsentForm.Builder builder = new ConsentForm.Builder(activity2, getUrlPrivacyPolicy(activity2));
            builder.withPersonalizedAdsOption();
            builder.withNonPersonalizedAdsOption();
            builder.withListener(new ConsentFormListener() {
                public void onConsentFormOpened() {
                }

                public void onConsentFormLoaded() {
                    GDPRForm.this.consentForm.show();
                }

                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean bool) {
                    Log.e("GDPR", "Status : " + consentStatus);
                }

                public void onConsentFormError(String str) {
                    Log.e("GDPR", str);
                }
            });
            this.consentForm = builder.build();
            this.consentForm.load();
        }

        private URL getUrlPrivacyPolicy(Activity activity2) {
            try {
                return new URL(activity2.getString(R.string.privacy_policy_url));
            } catch (MalformedURLException e) {
                Log.e("GDPR", e.getMessage());
                return null;
            }
        }
    }
}
