package com.radiostream.talitakum.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.radiostream.talitakum.R;
import com.radiostream.talitakum.services.metadata.Metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Tools {
    public static int BACKGROUND_IMAGE_ID = 2131165279;
    private static boolean DISPLAY_DEBUG = true;
    private static ArrayList<EventListener> listeners;
    private Context _context;
    private SharedPref sharedPref;

    public interface EventListener {
        void onAudioSessionId(Integer num);

        void onEvent(String str);

        void onMetaDataReceived(Metadata metadata, Bitmap bitmap);
    }

    public Tools(Context context) {
        this._context = context;
        this.sharedPref = new SharedPref(context);
    }

    public static String getDataFromUrl(String str) {
        android.util.Log.e("INFO", "Requesting: " + str);
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Your Single Radio");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200 && (responseCode == 302 || responseCode == 301 || responseCode == 303)) {
                String headerField = httpURLConnection.getHeaderField("Location");
                String headerField2 = httpURLConnection.getHeaderField("Set-Cookie");
                HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(headerField).openConnection();
                httpURLConnection2.setRequestProperty("Cookie", headerField2);
                httpURLConnection2.setRequestProperty("User-Agent", "Your Single Radio");
                httpURLConnection2.setRequestMethod("GET");
                httpURLConnection2.setDoInput(true);
                PrintStream printStream = System.out;
                printStream.println("Redirect to URL : " + headerField);
                httpURLConnection = httpURLConnection2;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static JSONObject getJSONObjectFromUrl(String str) {
        try {
            return new JSONObject(getDataFromUrl(str));
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
            Log.printStackTrace(e);
            return null;
        }
    }

    public static void noConnection(Activity activity, String str) {
        String str2;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (isOnline(activity)) {
            if (str == null || !DISPLAY_DEBUG) {
                str2 = "";
            } else {
                str2 = "\n\n" + str;
            }
            builder.setMessage(activity.getResources().getString(R.string.dialog_connection_description) + str2);
            builder.setPositiveButton(activity.getResources().getString(R.string.ok), (DialogInterface.OnClickListener) null);
            builder.setTitle(activity.getResources().getString(R.string.dialog_connection_title));
        } else {
            builder.setMessage(activity.getResources().getString(R.string.dialog_internet_description));
            builder.setPositiveButton(activity.getResources().getString(R.string.ok), (DialogInterface.OnClickListener) null);
            builder.setTitle(activity.getResources().getString(R.string.dialog_internet_title));
        }
        if (!activity.isFinishing()) {
            builder.show();
        }
    }

    public static void noConnection(Activity activity) {
        noConnection(activity, (String) null);
    }

    public static boolean isOnline(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isOnlineShowDialog(Activity activity) {
        if (isOnline(activity)) {
            return true;
        }
        noConnection(activity);
        return false;
    }

    public static boolean isNetworkActive(Activity activity) {
        NetworkInfo[] allNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager == null || (allNetworkInfo = connectivityManager.getAllNetworkInfo()) == null)) {
            for (NetworkInfo state : allNetworkInfo) {
                if (state.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
        if (r6 != null) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
        r6.disconnect();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0044, code lost:
        if (r6 != null) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        return r0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getJSONString(java.lang.String r6) {
        /*
            r0 = 0
            java.net.URL r1 = new java.net.URL     // Catch:{ Exception -> 0x003f, all -> 0x003a }
            r1.<init>(r6)     // Catch:{ Exception -> 0x003f, all -> 0x003a }
            java.net.URLConnection r6 = r1.openConnection()     // Catch:{ Exception -> 0x003f, all -> 0x003a }
            java.net.HttpURLConnection r6 = (java.net.HttpURLConnection) r6     // Catch:{ Exception -> 0x003f, all -> 0x003a }
            int r1 = r6.getResponseCode()     // Catch:{ Exception -> 0x0038 }
            r2 = 200(0xc8, float:2.8E-43)
            if (r1 != r2) goto L_0x0032
            java.io.InputStream r1 = r6.getInputStream()     // Catch:{ Exception -> 0x0038 }
            java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0038 }
            r2.<init>()     // Catch:{ Exception -> 0x0038 }
        L_0x001d:
            int r3 = r1.read()     // Catch:{ Exception -> 0x0038 }
            r4 = -1
            if (r3 == r4) goto L_0x0028
            r2.write(r3)     // Catch:{ Exception -> 0x0038 }
            goto L_0x001d
        L_0x0028:
            byte[] r1 = r2.toByteArray()     // Catch:{ Exception -> 0x0038 }
            java.lang.String r2 = new java.lang.String     // Catch:{ Exception -> 0x0038 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0038 }
            r0 = r2
        L_0x0032:
            if (r6 == 0) goto L_0x0047
        L_0x0034:
            r6.disconnect()
            goto L_0x0047
        L_0x0038:
            r1 = move-exception
            goto L_0x0041
        L_0x003a:
            r6 = move-exception
            r5 = r0
            r0 = r6
            r6 = r5
            goto L_0x0049
        L_0x003f:
            r1 = move-exception
            r6 = r0
        L_0x0041:
            r1.printStackTrace()     // Catch:{ all -> 0x0048 }
            if (r6 == 0) goto L_0x0047
            goto L_0x0034
        L_0x0047:
            return r0
        L_0x0048:
            r0 = move-exception
        L_0x0049:
            if (r6 == 0) goto L_0x004e
            r6.disconnect()
        L_0x004e:
            goto L_0x0050
        L_0x004f:
            throw r0
        L_0x0050:
            goto L_0x004f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.radiostream.talitakum.utilities.Tools.getJSONString(java.lang.String):java.lang.String");
    }

    public static String getDataFromURL(String str) {
        android.util.Log.v("INFO", "Requesting: " + str);
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Your Single Radio");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
            }
        } catch (IOException unused) {
        }
        return stringBuffer.toString();
    }

    public static void registerAsListener(EventListener eventListener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(eventListener);
    }

    public static void unregisterAsListener(EventListener eventListener) {
        listeners.remove(eventListener);
    }

    public static void onEvent(String str) {
        ArrayList<EventListener> arrayList = listeners;
        if (arrayList != null) {
            Iterator<EventListener> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().onEvent(str);
            }
        }
    }

    public static void onAudioSessionId(Integer num) {
        ArrayList<EventListener> arrayList = listeners;
        if (arrayList != null) {
            Iterator<EventListener> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().onAudioSessionId(num);
            }
        }
    }

    public static void onMetaDataReceived(Metadata metadata, Bitmap bitmap) {
        ArrayList<EventListener> arrayList = listeners;
        if (arrayList != null) {
            Iterator<EventListener> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().onMetaDataReceived(metadata, bitmap);
            }
        }
    }

    public long convertToMilliSeconds(String str) {
        Pattern pattern;
        if (str.contains("\\:")) {
            pattern = Pattern.compile("(\\d+):(\\d+)");
        } else {
            pattern = Pattern.compile("(\\d+).(\\d+)");
        }
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            return 0;
        }
        return (((long) Integer.parseInt(matcher.group(1))) * 60 * 60 * 1000) + ((long) (Integer.parseInt(matcher.group(2)) * 60 * 1000));
    }
}
