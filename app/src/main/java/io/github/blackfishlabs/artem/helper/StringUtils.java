package io.github.blackfishlabs.artem.helper;

import java.text.Normalizer;

import static java.text.Normalizer.Form.NFD;

public class StringUtils {

    private StringUtils() {/* No instances */}

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNullOrEmpty(Object str) {
        return str == null || str.toString().isEmpty();
    }

    public static String removeAccents(String str) {
        if (isNullOrEmpty(str)) {
            throw new NullPointerException("str is null or empty");
        }

        return Normalizer.normalize(str, NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
