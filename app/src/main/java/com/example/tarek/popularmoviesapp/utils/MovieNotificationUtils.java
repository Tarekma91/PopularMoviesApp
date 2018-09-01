/*

Copyright 2018 tarekmabdallah91@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package com.example.tarek.popularmoviesapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.example.tarek.popularmoviesapp.MainActivity;
import com.example.tarek.popularmoviesapp.R;
import com.example.tarek.popularmoviesapp.sync.MovieReminderTasks;
import com.example.tarek.popularmoviesapp.sync.MoviesIntentService;

public class MovieNotificationUtils {

    private static final int MOVIE_REMINDER_NOTIFICATION_ID = 1138;
    private static final int MOVIE_FAVOURED_PENDING_INTENT_ID = 3417;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 555;
    private static final String MOVIE_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public static void setNotification (Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MOVIE_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MOVIE_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.icon_film_reel)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.movies_updated_title_notification))
                .setContentText(context.getString(R.string.movies_updated_text_notification))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.movies_updated_text_notification)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        if (notificationManager != null) {
            notificationManager.notify(MOVIE_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private static NotificationCompat.Action ignoreReminderAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, MoviesIntentService.class);
        ignoreReminderIntent.setAction(MovieReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.ok_msg),
                ignoreReminderPendingIntent);
    }

    private static PendingIntent contentIntent(Context context){
        Intent openMainActivity = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context,
                MOVIE_FAVOURED_PENDING_INTENT_ID
                ,openMainActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        return BitmapFactory.decodeResource(context.getResources() , R.drawable.icon_film_reel);
    }
}
