package com.grrigore.tripback_up.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Bundle;

import com.grrigore.tripback_up.R;
import com.grrigore.tripback_up.data.SharedPrefs;
import com.grrigore.tripback_up.widget.TripWidgetProvider;

import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_DESCRIPTION;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_TITLE;

public class WidgetUtils {
    public static void addWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Bundle bundle = new Bundle();
        int widgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        String tripTitle = SharedPrefs.getInstance(context).getStringValue(TRIP_CLICKED_TITLE, null);
        String tripDescription = SharedPrefs.getInstance(context).getStringValue(TRIP_CLICKED_DESCRIPTION, null);
        TripWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId, tripTitle, tripDescription);

        ToastUtil.showToast(context.getString(R.string.wiget_set_for) + tripTitle + context.getString(R.string.exclamation_mark), context);
    }
}
