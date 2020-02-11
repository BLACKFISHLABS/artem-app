package io.github.blackfishlabs.artem.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    public PrefUtils() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    public static void storeAtelierKey(Context context, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("ATELIER_KEY", key);
        editor.apply();
    }

    public static String getAtelierKey(Context context) {
        return getSharedPreferences(context).getString("ATELIER_KEY", null);
    }

    public static void storeMonthsRecoverValueInvestment(Context context, int months) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("MONTHS_RECOVER_INVESTMENT_KEY", months);
        editor.apply();
    }

    public static int getMonthsRecoverValueInvestment(Context context) {
        return getSharedPreferences(context).getInt("MONTHS_RECOVER_INVESTMENT_KEY", 0);
    }
}
