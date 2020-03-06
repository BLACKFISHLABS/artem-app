package io.github.blackfishlabs.artem.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import io.github.blackfishlabs.artem.R;

public class AndroidUtils {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private AndroidUtils() {
    }

    public static int dpToPx(final Context context, float dp) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(px);
    }

    public static float pxToDp(final Context context, float px) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return px * displayMetrics.density;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static void focusThenShowKeyboard(@NonNull final Context context, @NonNull final View view) {
        if (view.isShown()) {
            if (view.requestFocus()) {
                showKeyboard(context, view);
            }
        }
    }

    public static void showKeyboard(@NonNull final Context context, final View view) {
        view.postDelayed(() -> {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view.findFocus(), 0);
        }, 50);
    }

    public static void hideKeyboard(@NonNull Context context, View currentFocus) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static Typeface getFont(final Context context) {
        return ResourcesCompat.getFont(context, R.font.comfortaa);
    }

}
