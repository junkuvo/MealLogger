package junkuvo.apps.meallogger.util;

import android.content.Context;

public class PriceUtil {
    private static final String TAG = PriceUtil.class.getSimpleName();
    private final PriceUtil self = this;

    private Context mContext;

    public void retrievePriceSum(){

    }

    public static long parsePriceToLong(String price, String prefix){
        return Long.parseLong(price.replace(prefix,"").replaceAll(",",""));
    }

    public static String parseLongToPrice(long price, String prefix){
        return prefix + String.format("%,d", price);
    }
}
