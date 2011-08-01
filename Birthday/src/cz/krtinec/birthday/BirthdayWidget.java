/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */

package cz.krtinec.birthday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.Event;
import cz.krtinec.birthday.widgets.BirthdayWidget2x2;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

/**
 * This is just for backward compatibility.
 * @author krtek
 *
 */
public class BirthdayWidget extends AppWidgetProvider {
    private int NOTIFY_CODE = 1;
    private int WIDGET_CODE = 0;

    protected int getListSize() {
        return 2;
    }

    protected int getLayout() {
        return R.layout.widget2x1;
    }

    protected Class getWidgetClass() {
        return BirthdayWidget.class;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Utils.startAlarm(context);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        if (Utils.WIDGET_UPDATE.equals(intent.getAction())) {
            Log.d("UpdateService", "Service started...");
            AppWidgetManager manager = AppWidgetManager.getInstance(ctx);
            List<Event> list = BirthdayProvider.getInstance().upcomingBirthday(ctx);
            list = list.subList(0, getListSize());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            if (prefs.getBoolean("notifications.enabled", true)) {
                alertOnBirthday(ctx, list);
            }

            RemoteViews views = new RemoteViews("cz.krtinec.birthday", getLayout());
            updateViews(ctx, views, list);
            Intent i = new Intent(ctx.getApplicationContext(), Birthday.class);
            views.setOnClickPendingIntent(R.id.layout, PendingIntent.getActivity(ctx, WIDGET_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT));
            list = null;
            manager.updateAppWidget(new ComponentName(ctx, getWidgetClass()), views);
        }
    }


    public RemoteViews updateViews(Context ctx, RemoteViews views, List<Event> list) {
        if (list.size() > 0) {
            Event contact = list.get(0);
            views.setTextViewText(R.id.first_name, contact.getDisplayName());
            views.setTextViewText(R.id.first_date, contact.getDisplayDate(ctx));
            replaceIconWithPhoto(ctx, views, contact, R.id.first_icon);
        }
        if (list.size() > 1) {
            Event contact = list.get(1);
            views.setTextViewText(R.id.second_name, contact.getDisplayName());
            views.setTextViewText(R.id.second_date, contact.getDisplayDate(ctx));
            replaceIconWithPhoto(ctx, views, contact, R.id.second_icon);
        }
        if (list.size() > 2) {
            Event contact = list.get(2);
            views.setTextViewText(R.id.third_name, contact.getDisplayName());
            views.setTextViewText(R.id.third_date, contact.getDisplayDate(ctx));
            replaceIconWithPhoto(ctx, views, contact, R.id.third_icon);
        }
        if (list.size() > 3) {
            Event contact = list.get(3);
            views.setTextViewText(R.id.fourth_name, contact.getDisplayName());
            views.setTextViewText(R.id.fourth_date, contact.getDisplayDate(ctx));
            replaceIconWithPhoto(ctx, views, contact, R.id.fourth_icon);
        }
        return views;
    }

    private void alertOnBirthday(Context ctx, List<Event> list) {
        for (Event c:list) {
            if (hasBirthdayToday(c)) {
                //should fire alarm
                Calendar now = Calendar.getInstance();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                int hourToAlert = Integer.valueOf(prefs.getString("notifications.time", "8"));
                if (isTimeToNotify(now, hourToAlert)) {
                    fireBirthdayAlert(ctx, c, now.getTimeInMillis());
                }
            }
        }
    }

    public static boolean hasBirthdayToday(Event c) {
        return c.getDaysToEvent() == 0;
    }


    protected void replaceIconWithPhoto(Context ctx, RemoteViews views, Event contact, int viewId) {
        InputStream is = BirthdayProvider.openPhoto(ctx, contact.getId());
        if (is != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                views.setImageViewBitmap(viewId, bitmap);
            } else {
                views.setImageViewResource(viewId, R.drawable.icon);
            }
            try {
                is.close();
            } catch (IOException e) {
            }
        } else {
            views.setImageViewResource(viewId, R.drawable.icon);
        }
    }

    public static boolean isTimeToNotify(Calendar now, int hourToAlert) {
        return now.get(Calendar.HOUR_OF_DAY) == hourToAlert;
    }

    public static String formatMessage(Context ctx, Event e) {
        String notificationFormat = ctx.getString(R.string.notification_pattern);
        return MessageFormat.format(notificationFormat, e.getDisplayName(), Utils.getEventLabel(ctx, e));
    }

    public static String formatLabel(Context ctx, Event e) {
        String label = MessageFormat.format(ctx.getString(R.string.notification_alert), Utils.getEventLabel(ctx, e));
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }

    void fireBirthdayAlert(Context ctx, Event c, Long when) {
        String label = formatLabel(ctx, c);
        Notification n = new Notification(R.drawable.icon, label, when);
        n.flags = n.flags | Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(ctx, Birthday.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, NOTIFY_CODE, i, PendingIntent.FLAG_CANCEL_CURRENT);

        n.setLatestEventInfo(ctx, label, formatMessage(ctx, c), pendingIntent);

        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify("Birthday", (int)c.getId(), n);
    }
}
