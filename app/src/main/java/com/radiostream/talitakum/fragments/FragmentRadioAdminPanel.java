package com.radiostream.talitakum.fragments;

import static com.google.android.exoplayer2.C.ENCODING_PCM_MU_LAW;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.radiostream.talitakum.R;
import com.radiostream.talitakum.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.claucookie.miniequalizerlibrary.EqualizerView;

import com.radiostream.talitakum.models.ItemRadio;
import com.radiostream.talitakum.services.RadioManager;
import com.radiostream.talitakum.services.metadata.Metadata;
import com.radiostream.talitakum.utilities.CollapseControllingFragment;
import com.radiostream.talitakum.utilities.Constant;
import com.radiostream.talitakum.utilities.PermissionsFragment;
import com.radiostream.talitakum.utilities.SharedPref;
import com.radiostream.talitakum.utilities.SleepTimeReceiver;
import com.radiostream.talitakum.utilities.Tools;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

@SuppressLint({"RestrictedApi"})
public class FragmentRadioAdminPanel extends Fragment implements View.OnClickListener, PermissionsFragment, CollapseControllingFragment, Tools.EventListener {
    /* access modifiers changed from: private */
    public Activity activity;
    private RoundedImageView albumArtView;
    /* access modifiers changed from: private */
    public FloatingActionButton buttonPlayPause;

    int counter = 1;
    EqualizerView equalizerView;
    Handler handler = new Handler();
    private ImageButton img_timer;
    private ImageButton img_volume_bar;
    private InterstitialAd interstitialAd;
    private MainActivity mainActivity;
    TextView nowPlaying;
    TextView nowPlayingTitle;
    /* access modifiers changed from: private */
    public ProgressBar progressBar;
    private RadioManager radioManager;
    /* access modifiers changed from: private */
    public String radio_name;
    /* access modifiers changed from: private */
    public String radio_url;
    private RelativeLayout relativeLayout;
    SharedPref sharedPref;
    private Toolbar toolbar;
    Tools tools;

    public void onAudioSessionId(Integer num) {
    }

    public boolean supportsCollapse() {
        return false;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity = (MainActivity) context;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.fragment_radio, viewGroup, false);
        this.toolbar = (Toolbar) this.relativeLayout.findViewById(R.id.toolbar);
        this.nowPlayingTitle = (TextView) this.relativeLayout.findViewById(R.id.now_playing_title);
        this.nowPlaying = (TextView) this.relativeLayout.findViewById(R.id.now_playing);
        setupToolbar();
        setHasOptionsMenu(true);
        this.sharedPref = new SharedPref(getActivity());
        this.sharedPref.setCheckSleepTime();
        this.tools = new Tools(getActivity());
        initializeUIElements();
        this.albumArtView.setVisibility(View.VISIBLE);
        this.albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        loadInterstitialAd();
        onBackPressed();
        return this.relativeLayout;
    }

    private void setupToolbar() {
        this.toolbar.setTitle((CharSequence) getString(R.string.app_name));
        this.mainActivity.setSupportActionBar(this.toolbar);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mainActivity.setupNavigationDrawer(this.toolbar);
        this.activity = getActivity();
        if (Tools.isNetworkActive(getActivity())) {
            new MyTask().execute(new String[]{"http://alhastream.my.id/panel/Talitakum//api.php"});
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();
        }
        Tools.isOnlineShowDialog(this.activity);
        this.radioManager = RadioManager.with();
        this.progressBar.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            public void run() {
                FragmentRadioAdminPanel.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        FragmentRadioAdminPanel.this.progressBar.setVisibility(View.INVISIBLE);
                        FragmentRadioAdminPanel.this.updateButtons();
                    }
                });
            }
        });
        if (isPlaying()) {
            onAudioSessionId(Integer.valueOf(RadioManager.getService().getAudioSessionId()));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0028  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x003d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEvent(java.lang.String r6) {
        /*
            r5 = this;
            int r0 = r6.hashCode()
            r1 = -1435314966(0xffffffffaa72d4ea, float:-2.1567787E-13)
            r2 = 0
            java.lang.String r3 = "PlaybackStatus_LOADING"
            r4 = 1
            if (r0 == r1) goto L_0x001d
            r1 = -906175178(0xffffffffc9fcdd36, float:-2071462.8)
            if (r0 == r1) goto L_0x0013
            goto L_0x0025
        L_0x0013:
            java.lang.String r0 = "PlaybackStatus_ERROR"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0025
            r0 = 1
            goto L_0x0026
        L_0x001d:
            boolean r0 = r6.equals(r3)
            if (r0 == 0) goto L_0x0025
            r0 = 0
            goto L_0x0026
        L_0x0025:
            r0 = -1
        L_0x0026:
            if (r0 == 0) goto L_0x0032
            if (r0 == r4) goto L_0x002b
            goto L_0x0037
        L_0x002b:
            r0 = 2131689563(0x7f0f005b, float:1.9008145E38)
            r5.makeSnackBar(r0)
            goto L_0x0037
        L_0x0032:
            android.widget.ProgressBar r0 = r5.progressBar
            r0.setVisibility(r2)
        L_0x0037:
            boolean r6 = r6.equals(r3)
            if (r6 != 0) goto L_0x0043
            android.widget.ProgressBar r6 = r5.progressBar
            r0 = 4
            r6.setVisibility(r0)
        L_0x0043:
            r5.updateButtons()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.radiostream.talitakum.fragments.FragmentRadioAdminPanel.onEvent(java.lang.String):void");
    }

    public void onStart() {
        super.onStart();
        Tools.registerAsListener(this);
    }

    public void onStop() {
        Tools.unregisterAsListener(this);
        super.onStop();
    }

    public void onDestroy() {
        if (!this.radioManager.isPlaying()) {
            this.radioManager.unbind(getContext());
        }
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        updateButtons();
        this.radioManager.bind(getContext());
        if (isPlaying()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    FragmentRadioAdminPanel.this.startResume();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            FragmentRadioAdminPanel.this.startResume();
                        }
                    }, 10);
                }
            }, 10);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        private MyTask() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            FragmentRadioAdminPanel.this.buttonPlayPause.setVisibility(View.INVISIBLE);
            FragmentRadioAdminPanel.this.nowPlaying.setText(R.string.loading_progress);
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... strArr) {
            return Tools.getJSONString(strArr[0]);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            FragmentRadioAdminPanel.this.buttonPlayPause.setVisibility(View.VISIBLE);
            if (str == null || str.length() == 0) {
                Toast.makeText(FragmentRadioAdminPanel.this.getActivity(), FragmentRadioAdminPanel.this.getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONArray jSONArray = new JSONObject(str).getJSONArray("result");
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String unused = FragmentRadioAdminPanel.this.radio_name = jSONObject.getString("radio_name");
                    String unused2 = FragmentRadioAdminPanel.this.radio_url = jSONObject.getString("radio_url");
                    Constant.itemRadio = new ItemRadio(FragmentRadioAdminPanel.this.radio_name, FragmentRadioAdminPanel.this.radio_url);
                }
                FragmentRadioAdminPanel.this.nowPlayingTitle.setText(R.string.now_playing);
                FragmentRadioAdminPanel.this.nowPlaying.setText(FragmentRadioAdminPanel.this.radio_name);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        FragmentRadioAdminPanel.this.buttonPlayPause.performClick();
                    }
                }, 1000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeUIElements() {
        this.progressBar = (ProgressBar) this.relativeLayout.findViewById(R.id.progressBar);
        this.progressBar.setMax(100);
        this.progressBar.setVisibility(View.VISIBLE);
        this.equalizerView = (EqualizerView) this.relativeLayout.findViewById(R.id.equalizer_view);
        this.albumArtView = (RoundedImageView) this.relativeLayout.findViewById(R.id.albumArt);
        this.albumArtView.setCornerRadius(30.0f);
        this.albumArtView.setBorderWidth(8.0f);
        this.albumArtView.setOval(true);
        this.img_volume_bar = (ImageButton) this.relativeLayout.findViewById(R.id.img_volume);
        this.img_volume_bar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentRadioAdminPanel.this.changeVolume();
            }
        });
        this.img_timer = (ImageButton) this.relativeLayout.findViewById(R.id.img_timer);
        this.img_timer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (FragmentRadioAdminPanel.this.sharedPref.getIsSleepTimeOn().booleanValue()) {
                    FragmentRadioAdminPanel.this.openTimeDialog();
                } else {
                    FragmentRadioAdminPanel.this.openTimeSelectDialog();
                }
            }
        });
        this.buttonPlayPause = (FloatingActionButton) this.relativeLayout.findViewById(R.id.btn_play_pause);
        this.buttonPlayPause.setOnClickListener(this);
        this.equalizerView.stopBars();
        updateButtons();
    }

    public void updateButtons() {
        String str;
        if (!isPlaying() && this.progressBar.getVisibility() != View.VISIBLE) {
            this.buttonPlayPause.setImageResource(R.drawable.ic_play_white);
            this.relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);
            updateMediaInfoFromBackground((String) null, (Bitmap) null);
        } else if (RadioManager.getService() == null || (str = this.radio_url) == null || str.equals(RadioManager.getService().getStreamUrl())) {
            this.buttonPlayPause.setImageResource(R.drawable.ic_pause_white);
            this.relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);
        } else {
            this.buttonPlayPause.setImageResource(R.drawable.ic_play_white);
            this.relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.VISIBLE);
        }
        if (isPlaying()) {
            this.equalizerView.animateBars();
        } else {
            this.equalizerView.stopBars();
        }
    }

    public void onClick(View view) {
        requestStoragePermission();
    }

    /* access modifiers changed from: private */
    public void startStopPlaying() {
        this.radioManager.playOrPause(this.radio_url);
        updateButtons();
    }

    /* access modifiers changed from: private */
    public void startResume() {
        this.radioManager.playResume(this.radio_url);
        updateButtons();
    }

    /* access modifiers changed from: private */
    public void stopService() {
        this.radioManager.stopServices();
        Tools.unregisterAsListener(this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.share) {
            return super.onOptionsItemSelected(menuItem);
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
        intent.putExtra("android.intent.extra.TEXT", getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share"));
        return true;
    }

    public void updateMediaInfoFromBackground(String str, Bitmap bitmap) {
        if (str != null) {
            this.nowPlaying.setText(str);
        }
        if (str != null && this.nowPlayingTitle.getVisibility() == View.GONE) {
            this.nowPlayingTitle.setVisibility(View.VISIBLE);
            this.nowPlaying.setVisibility(View.VISIBLE);
        } else if (str == null) {
            this.nowPlayingTitle.setVisibility(View.VISIBLE);
            this.nowPlayingTitle.setText(R.string.now_playing);
            this.nowPlaying.setVisibility(View.VISIBLE);
            this.nowPlaying.setText(this.radio_name);
        }
        if (bitmap != null) {
            this.albumArtView.setImageBitmap(bitmap);
        } else {
            this.albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        }
    }

    public String[] requiredPermissions() {
        return new String[]{"android.permission.READ_PHONE_STATE"};
    }

    public void onMetaDataReceived(Metadata metadata, Bitmap bitmap) {
        String str;
        if (metadata == null || metadata.getArtist() == null) {
            str = null;
        } else {
            str = metadata.getArtist() + " - " + metadata.getSong();
        }
        updateMediaInfoFromBackground(str, bitmap);
    }

    /* access modifiers changed from: private */
    public boolean isPlaying() {
        return (this.radioManager == null || RadioManager.getService() == null || !RadioManager.getService().isPlaying()) ? false : true;
    }

    /* access modifiers changed from: private */
    public void makeSnackBar(int i) {
        Snackbar make = Snackbar.make((View) this.buttonPlayPause, i, -1);
        make.show();
        ((TextView) make.getView().findViewById(R.id.snackbar_text)).setTextColor(getResources().getColor(R.color.white));
    }

    public void onBackPressed() {
        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            public boolean onBackClick() {
                FragmentRadioAdminPanel.this.exitDialog();
                return true;
            }
        });
    }

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon((int) R.mipmap.ic_launcher);
        builder.setTitle((int) R.string.app_name);
        builder.setMessage((CharSequence) getResources().getString(R.string.message));
        builder.setPositiveButton((CharSequence) getResources().getString(R.string.quit), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentRadioAdminPanel.this.stopService();
                FragmentRadioAdminPanel.this.getActivity().finish();
            }
        });
        builder.setNegativeButton((CharSequence) getResources().getString(R.string.minimize), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentRadioAdminPanel.this.minimizeApp();
            }
        });
        builder.setNeutralButton((CharSequence) getResources().getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    public void minimizeApp() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void requestStoragePermission() {
        Dexter.withActivity(getActivity()).withPermissions("android.permission.READ_PHONE_STATE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    if (FragmentRadioAdminPanel.this.isPlaying()) {
                        FragmentRadioAdminPanel.this.startStopPlaying();
                    } else if (FragmentRadioAdminPanel.this.radio_url != null) {
                        FragmentRadioAdminPanel.this.startStopPlaying();
                        FragmentRadioAdminPanel.this.showInterstitialAd();
                        if (((AudioManager) FragmentRadioAdminPanel.this.activity.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3) < 2) {
                            FragmentRadioAdminPanel.this.makeSnackBar(R.string.volume_low);
                        }
                    } else {
                        FragmentRadioAdminPanel.this.makeSnackBar(R.string.error_retry_later);
                    }
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    FragmentRadioAdminPanel.this.showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }

        }).withErrorListener(new PermissionRequestErrorListener() {
            public void onError(DexterError dexterError) {
                FragmentActivity activity = FragmentRadioAdminPanel.this.getActivity();
                Toast.makeText(activity, "Error occurred! " + dexterError.toString(), Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    /* access modifiers changed from: private */
    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((CharSequence) "Need Permissions");
        builder.setMessage((CharSequence) "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton((CharSequence) "GOTO SETTINGS", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                FragmentRadioAdminPanel.this.openSettings();
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /* access modifiers changed from: private */
    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), (String) null));
        startActivityForResult(intent, 101);
    }

    /* access modifiers changed from: private */
    public void changeVolume() {
        RelativePopupWindow relativePopupWindow = new RelativePopupWindow((Context) getActivity());
        View inflate = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lyt_volume, (ViewGroup) null);
        ((ImageView) inflate.findViewById(R.id.img_volume_max)).setColorFilter(-16777216);
        ((ImageView) inflate.findViewById(R.id.img_volume_min)).setColorFilter(-16777216);
        VerticalSeekBar verticalSeekBar = (VerticalSeekBar) inflate.findViewById(R.id.seek_bar_volume);
        verticalSeekBar.getThumb().setColorFilter(this.sharedPref.getFirstColor(), PorterDuff.Mode.SRC_IN);
        verticalSeekBar.getProgressDrawable().setColorFilter(this.sharedPref.getSecondColor(), PorterDuff.Mode.SRC_IN);
        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        verticalSeekBar.setMax(audioManager.getStreamMaxVolume(3));
        verticalSeekBar.setProgress(audioManager.getStreamVolume(3));
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                audioManager.setStreamVolume(3, i, 0);
            }
        });
        relativePopupWindow.setFocusable(true);
        relativePopupWindow.setWidth(-2);
        relativePopupWindow.setHeight(-2);
        relativePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        relativePopupWindow.setContentView(inflate);
        relativePopupWindow.showOnAnchor(this.img_volume_bar, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

    public void openTimeSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((CharSequence) getString(R.string.sleep_time));
        View inflate = getLayoutInflater().inflate(R.layout.lyt_dialog_select_time, (ViewGroup) null);
        builder.setView(inflate);
        final TextView textView = (TextView) inflate.findViewById(R.id.txt_minutes);
        textView.setText("1 " + getString(R.string.min));
        final IndicatorSeekBar build = IndicatorSeekBar.with(getActivity()).min(1.0f).max(120.0f).progress(1.0f).thumbColor(this.sharedPref.getSecondColor()).indicatorColor(this.sharedPref.getFirstColor()).trackProgressColor(this.sharedPref.getFirstColor()).build();
        build.setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                textView.setText(seekParams.progress + " " + FragmentRadioAdminPanel.this.getString(R.string.min));
            }
        });
        ((FrameLayout) inflate.findViewById(R.id.frameLayout)).addView(build);
        builder.setPositiveButton((CharSequence) getString(R.string.set), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String valueOf = String.valueOf(build.getProgress() / 60);
                String valueOf2 = String.valueOf(build.getProgress() % 60);
                if (valueOf.length() == 1) {
                    valueOf = "0" + valueOf;
                }
                if (valueOf2.length() == 1) {
                    valueOf2 = "0" + valueOf2;
                }
                long convertToMilliSeconds = FragmentRadioAdminPanel.this.tools.convertToMilliSeconds(valueOf + ":" + valueOf2) + System.currentTimeMillis();
                int nextInt = new Random().nextInt(100);
                FragmentRadioAdminPanel.this.sharedPref.setSleepTime(true, convertToMilliSeconds, nextInt);
                PendingIntent broadcast = PendingIntent.getBroadcast(FragmentRadioAdminPanel.this.getActivity(), nextInt, new Intent(FragmentRadioAdminPanel.this.getActivity(), SleepTimeReceiver.class), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) FragmentRadioAdminPanel.this.getActivity().getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, convertToMilliSeconds, broadcast);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, convertToMilliSeconds, broadcast);
                }
            }
        });
        builder.setNegativeButton((CharSequence) getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public void openTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((CharSequence) getString(R.string.sleep_time));
        View inflate = getLayoutInflater().inflate(R.layout.lyt_dialog_time, (ViewGroup) null);
        builder.setView(inflate);
        builder.setNegativeButton((CharSequence) getString(R.string.cancel), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton((CharSequence) getString(R.string.stop), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PendingIntent broadcast = PendingIntent.getBroadcast(FragmentRadioAdminPanel.this.getActivity(), FragmentRadioAdminPanel.this.sharedPref.getSleepID(), new Intent(FragmentRadioAdminPanel.this.getActivity(), SleepTimeReceiver.class), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                broadcast.cancel();
                ((AlarmManager) FragmentRadioAdminPanel.this.getActivity().getSystemService(Context.ALARM_SERVICE)).cancel(broadcast);
                FragmentRadioAdminPanel.this.sharedPref.setSleepTime(false, 0, 0);
            }
        });
        updateTimer(inflate.findViewById(R.id.txt_time), this.sharedPref.getSleepTime());
        builder.show();
    }

    /* access modifiers changed from: private */
    public void updateTimer(final TextView textView, long j) {
        long currentTimeMillis = j - System.currentTimeMillis();
        if (currentTimeMillis > 0) {
            textView.setText(String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toHours(currentTimeMillis)), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis) % TimeUnit.HOURS.toMinutes(1)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) % TimeUnit.MINUTES.toSeconds(1))}));
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    if (FragmentRadioAdminPanel.this.sharedPref.getIsSleepTimeOn().booleanValue()) {
                        FragmentRadioAdminPanel fragmentRadioAdminPanel = FragmentRadioAdminPanel.this;
                        fragmentRadioAdminPanel.updateTimer(textView, fragmentRadioAdminPanel.sharedPref.getSleepTime());
                    }
                }
            }, 1000);
        }
    }

    private void loadInterstitialAd() {
        Log.d("INFO", "AdMob Interstitial is Disabled");
    }

    /* access modifiers changed from: private */
    public void showInterstitialAd() {
        Log.d("INFO", "AdMob Interstitial is Disabled");
    }
}
