package com.radiostream.talitakum.activities;

import static com.google.android.exoplayer2.C.UTF8_NAME;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.radiostream.talitakum.R;
import com.radiostream.talitakum.models.ItemPrivacy;
import com.radiostream.talitakum.utilities.Constant;
import com.radiostream.talitakum.utilities.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPrivacyPolicy extends AppCompatActivity {
    String privacy_policy;
    ProgressBar progressBar;
    WebView wv_privacy_policy;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_privacy_policy);
        setupToolbar();
        this.wv_privacy_policy = (WebView) findViewById(R.id.privacy_policy);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (Tools.isNetworkActive(this)) {
            new MyTask().execute(new String[]{"http://alhastream.my.id/panel/Talitakum//api.php?privacy_policy"});
            return;
        }
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();
    }

    public void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle((int) R.string.about_app_privacy_policy);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        private MyTask() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            ActivityPrivacyPolicy.this.progressBar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... strArr) {
            return Tools.getJSONString(strArr[0]);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            ActivityPrivacyPolicy.this.progressBar.setVisibility(View.GONE);
            if (str == null || str.length() == 0) {
                Toast.makeText(ActivityPrivacyPolicy.this.getApplicationContext(), ActivityPrivacyPolicy.this.getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONArray jSONArray = new JSONObject(str).getJSONArray("result");
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    ActivityPrivacyPolicy.this.privacy_policy = jSONObject.getString("privacy_policy");
                    Constant.itemPrivacy = new ItemPrivacy(ActivityPrivacyPolicy.this.privacy_policy);
                }
                ActivityPrivacyPolicy.this.wv_privacy_policy.setBackgroundColor(Color.parseColor("#ffffff"));
                ActivityPrivacyPolicy.this.wv_privacy_policy.setFocusableInTouchMode(false);
                ActivityPrivacyPolicy.this.wv_privacy_policy.setFocusable(false);
                ActivityPrivacyPolicy.this.wv_privacy_policy.getSettings().setDefaultTextEncodingName(UTF8_NAME);
                ActivityPrivacyPolicy.this.wv_privacy_policy.getSettings().setDefaultFontSize(ActivityPrivacyPolicy.this.getResources().getInteger(R.integer.font_size));
                String str2 = ActivityPrivacyPolicy.this.privacy_policy;
                ActivityPrivacyPolicy.this.wv_privacy_policy.loadDataWithBaseURL((String) null, "<html><head><style type=\"text/css\">body{color: #525252;}</style></head><body>" + str2 + "</body></html>", "text/html; charset=UTF-8", "utf-8", (String) null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }
}
