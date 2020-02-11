package io.github.blackfishlabs.artem.helper;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;

public class FormattingUtils {

    private FormattingUtils() {/* No constructor */}

    private static final PhoneNumberUtil sPhoneFormatter = PhoneNumberUtil.getInstance();
    private static final NumberFormat sCurrencyFormatter = NumberFormat.getCurrencyInstance(Constants.PT_BR_DEFAULT_LOCALE);
    private static final NumberFormat sNumberFormatter = NumberFormat.getNumberInstance(Constants.PT_BR_DEFAULT_LOCALE);
    private static final DateTimeFormatter sDateTimeFormatter = DateTimeFormat.forPattern(DateUtils.DATE_TIME_FORMAT);
    private static final NumberFormat percentFormatter = NumberFormat.getPercentInstance(Constants.PT_BR_DEFAULT_LOCALE);
    private static final DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern(DateUtils.DATE_FORMAT);

    static {
        sCurrencyFormatter.setMinimumFractionDigits(NumberUtils.CURRENCY_SCALE);
        sCurrencyFormatter.setMaximumFractionDigits(NumberUtils.CURRENCY_SCALE);
        percentFormatter.setMinimumFractionDigits(NumberUtils.PERCENT_SCALE);
        percentFormatter.setMaximumFractionDigits(NumberUtils.PERCENT_SCALE);
    }

    public static String formatPhoneNumber(String phoneNumberText) {
        try {
            Phonenumber.PhoneNumber phoneNumber = sPhoneFormatter.parse(phoneNumberText, Constants.BR_REGION_CODE);
            return sPhoneFormatter.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return phoneNumberText;
        }
    }

    public static String formatAsCurrency(double value) {
        return sCurrencyFormatter.format(value);
    }

    public static String formatAsNumber(double value) {
        return sNumberFormatter.format(value);
    }

    public static String formatAsDateTime(long dateTimeInMillis) {
        return sDateTimeFormatter.print(dateTimeInMillis);
    }

    public static String formatAsDate(LocalDate localDate) {
        return sDateFormatter.print(DateUtils.dateToMillis(localDate));
    }

    public static String formatAsDate(DateTime dateTime) {
        return sDateFormatter.print(dateTime.getMillis());
    }

    /**
     * Formata um valor numérico que não esteja como porcentual em um valor representativo de porcentagem.
     * <p>
     * Este método não é apropriado para valores que estejam como porcentual, por exemplo: 0.45.
     *
     * @param notInPercentValue valor numérico
     * @return valor representativo de porcentagem
     */
    public static String formatAsPercent(double notInPercentValue) {
        return percentFormatter.format(notInPercentValue / 100);
    }
}
