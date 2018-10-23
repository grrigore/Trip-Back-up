package com.grrigore.tripback_up.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.grrigore.tripback_up.R;
import com.grrigore.tripback_up.utils.SharedPrefs;

import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_DESCRIPTION;
import static com.grrigore.tripback_up.utils.Constants.TRIP_CLICKED_TITLE;

//todo new widget design

public class TripWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context applicationContext, AppWidgetManager appWidgetManager, int widgetId, String title, String description) {
        RemoteViews remoteViews = new RemoteViews(applicationContext.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.tvTitle, title);
        remoteViews.setTextViewText(R.id.tvDescription, description);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        String tripTitle = SharedPrefs.getInstance(context).getStringValue(TRIP_CLICKED_TITLE, null);
        String tripDescription = SharedPrefs.getInstance(context).getStringValue(TRIP_CLICKED_DESCRIPTION, null);
        for (int id : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id, tripTitle, tripDescription);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
