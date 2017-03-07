package com.hotmail.alekseev.k.sannacodetesttask;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static int dpAsPixels(Context context, int sizeInDp) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (sizeInDp * scale + 0.5f);
    }

    /**
     * Saves current user google id to private {@link SharedPreferences}.
     * @param context is application context.
     * @param userGoogleId is google id of current user.
     */
    public static void saveCurrentUserGoogleId(Context context, String userGoogleId) {
        SharedPreferences preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConst.EXTRA_GOOGLE_ID, userGoogleId);
        editor.apply();
    }

    /**
     * Gets saved google id from private {@link SharedPreferences}.
     * @param context is application context.
     * @return returns google id of current user.
     */
    public static String getCurrentUserGoogleId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        return preferences.getString(AppConst.EXTRA_GOOGLE_ID, null);
    }
}
