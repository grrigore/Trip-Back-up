package com.grrigore.tripback_up.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteTripContract {
    public static final String AUTHORITY = "com.grrigore.tripback_up.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavouriteTripEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_TRIP = "Trip";

        public static final String KEY_ID = "id";
        public static final String KEY_TRIP_ID = "tripId";
        public static final String KEY_TITLE = "title";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_DATE = "date";
    }
}
