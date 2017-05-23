package com.ai.cwf.libexceptioncapture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created at 陈 on 2017/5/23.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class TimeUtils {
    private static SimpleDateFormat defaultSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    public static String getSimpleDate() {
        return defaultSimpleDateFormat.format(new Date());
    }
}
