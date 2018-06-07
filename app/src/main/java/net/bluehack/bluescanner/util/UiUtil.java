package net.bluehack.bluescanner.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class UiUtil {

    private static Pattern INVALID_KEY_REGEX = Pattern.compile("[\\[\\]\\.#$]");

    public static String getDateFormat() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }

    public static boolean isNotValidKeyString(String pathString) {
        return INVALID_KEY_REGEX.matcher(pathString).find();
    }

}
