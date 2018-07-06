package com.grrigore.tripback_up.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
