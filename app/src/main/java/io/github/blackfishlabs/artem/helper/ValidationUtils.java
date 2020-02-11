package io.github.blackfishlabs.artem.helper;

import android.util.Patterns;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.regex.Pattern;

import timber.log.Timber;

public class ValidationUtils {

    private ValidationUtils() {/* No constructor */}

    private static final PhoneNumberUtil sPhoneValidator = PhoneNumberUtil.getInstance();

    private static final Pattern POSTAL_CODE = Pattern.compile("\\d{5}-?\\d{3}");

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String number) {
        try {
            Phonenumber.PhoneNumber phoneNumber = sPhoneValidator.parse(number, Constants.BR_REGION_CODE);
            return sPhoneValidator.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            Timber.e(e, "Error validating phone number");
            return false;
        }
    }

    public static boolean isValidPostalCode(String postalCode) {
        return POSTAL_CODE.matcher(postalCode).matches();
    }
}
