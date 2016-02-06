package com.deadswine.library.view.compass.Utilities;

import android.content.Context;

/**
 * Created by Adam Fręśko - Deadswine Studio on 2016-01-27.
 */
public class UtilitiesView {


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    public static int dpToPx(Context context, final float dp) {

        int tmp =(int) (dp * context.getResources().getDisplayMetrics().density);

        context = null;

        return tmp;
    }

}
