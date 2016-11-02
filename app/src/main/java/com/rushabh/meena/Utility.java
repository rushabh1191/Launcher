package com.rushabh.meena;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by rushabh on 02/11/16.
 */

public class Utility {

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
