package net.bluehack.bluescanner.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UiUtil {

    public static String getDateFormat() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }
}
