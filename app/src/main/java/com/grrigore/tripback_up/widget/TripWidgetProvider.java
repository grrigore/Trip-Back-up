package com.grrigore.tripback_up.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.grrigore.tripback_up.R;
import com.grrigore.tripback_up.utils.Constants;

public class TripWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String tripTitle = sharedPreferences.getString(Constants.TRIP_CLICKED_TITLE, null);
        String tripDescription = sharedPreferences.getString(Constants.TRIP_CLICKED_DESCRIPTION, null);
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

    public static void updateAppWidget(Context applicationContext, AppWidgetManager appWidgetManager, int widgetId, String title, String description) {
        RemoteViews remoteViews = new RemoteViews(applicationContext.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.tvTitle, title);
        remoteViews.setTextViewText(R.id.tvDescrition, description);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}
