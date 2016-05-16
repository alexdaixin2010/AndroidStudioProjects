package com.foodymon.businessapp.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.foodymon.businessapp.datastructure.StoreStaff;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by alexdai on 4/8/16.
 */
public class Utils {

    private static final String COMPAT_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final char COLON = ':';
    private static final char ISO8601_TZ_INFO_IS_JUST_Z = 'Z';
    private static final int ISO8601_COLON_INDEX = 26;


    public static boolean isValidUser(@Nullable StoreStaff user) {
        if(user != null) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNullOrEmpty(Collection<?> array) {
        return array == null || array.size() == 0;
    }

    public static String paserDateTime(String dateString){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            return dateString;
        }

        DateFormat newFormat = new SimpleDateFormat("HH:mm:ss dd MMM");
        return newFormat.format(date);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isAppIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
            .equalsIgnoreCase(context.getPackageName().toString())) {
            return true;
        }
        return false;
    }

    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public static Date converterISO8601StringToDate(String iso8601string) throws ParseException {
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
    }


    public static String StringJoin(String delimiter, String[] elements) {
        String separator = "";
        StringBuilder builder = new StringBuilder();
        for(String s :elements ) {
            builder.append(s);
            builder.append(separator);
            separator = delimiter+ " ";
        }
        return builder.toString();
    }

    /**
     * convert rgb color to int , for example 0xFFFFFFFF
     */
    public static int getRGBColor(int color) {
        return Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
    }


}
