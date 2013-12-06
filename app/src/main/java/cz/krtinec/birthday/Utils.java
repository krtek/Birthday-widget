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

import java.text.MessageFormat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import cz.krtinec.birthday.dto.*;
import cz.krtinec.birthday.ui.BirthdayPreference;
import org.joda.time.DateTime;

public class Utils {
    public static final String WIDGET_UPDATE = "cz.krtinec.birthday.WIDGET_UPDATE";

    public static String getCongrats(Context ctx, Event event) {
        String template = getTemplate(ctx, event);
        return MessageFormat.format(template, event.getDisplayName(), getEventLabel(ctx, event));
    }


    private static String getTemplate(Context ctx, Event event) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (event instanceof BirthdayEvent) {
            return prefs.getString(Birthday.TEMPLATE_KEY, ctx.getString(R.string.congrats_pattern));
        } else if (event instanceof AnniversaryEvent) {
            return prefs.getString(Birthday.TEMPLATE_KEY_ANNIVERSARY, ctx.getString(R.string.congrats_pattern));
        } else if (event instanceof OtherEvent) {
            //TODO Solve other events ?
            return prefs.getString(Birthday.TEMPLATE_KEY_OTHER, ctx.getString(R.string.congrats_pattern));
        } else {
            return prefs.getString(Birthday.TEMPLATE_KEY_CUSTOM, ctx.getString(R.string.congrats_pattern));
        }
    }

    public static String getEventLabel(Context ctx, Event event) {
        if (event instanceof BirthdayEvent) {
            return ctx.getString(R.string.birthday);
        } else if (event instanceof AnniversaryEvent) {
            return ctx.getString(R.string.anniversary);
        } else if (event instanceof CustomEvent) {
            return ((CustomEvent) event).getLabel();
        } else {
            //TODO Solve other events ?
            return ctx.getString(R.string.birthday);
        }
    }

    public static String getEventLabel(Context ctx, EditableEvent evt) {
        switch (evt.type) {
            case BIRTHDAY: {
                return ctx.getString(R.string.birthday);
            }
            case ANNIVERSARY: {
                return ctx.getString(R.string.anniversary);
            }
            case CUSTOM: {
                return evt.label;
            }
            case OTHER: {
                return ctx.getString(R.string.other);
            }
        }
        return ctx.getString(R.string.birthday);
    }

    public static void startWidgetUpdateAlarm(Context ctx) {
        Intent intent = new Intent(WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public static void startNotificationAlarm(Context ctx, long time) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                ctx, 0, new Intent(BirthdayApplication.BIRTHDAY_ALARM), PendingIntent.FLAG_CANCEL_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, alarmIntent);
        Toast.makeText(ctx, R.string.notifications_enabled, 1000);
        //am.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }

    public static void cancelNotificationAlarm(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                ctx, 0, new Intent(BirthdayApplication.BIRTHDAY_ALARM), PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(alarmIntent);
    }


    public static long calculateNotifTime(Long nowMillis, int hourToNotify) {
        DateTime now = new DateTime(nowMillis);
        DateTime timeToNotify = new DateTime(nowMillis);
        timeToNotify = timeToNotify.withHourOfDay(hourToNotify);
        if (timeToNotify.isBefore(now)) {
            timeToNotify = timeToNotify.plusDays(1);
        }
        return timeToNotify.getMillis();
    }

    public static void setOrCancelNotificationsAlarm(Context context, SharedPreferences prefs) {
        if (prefs.getBoolean(BirthdayApplication.NOTIFICATIONS_ENABLED, true)) {
            int hourToNotify = Integer.valueOf(prefs.getString(BirthdayApplication.NOTIFICATIONS_TIME, "8"));
            long time = calculateNotifTime(System.currentTimeMillis(), hourToNotify);
            startNotificationAlarm(context, time);
            Log.i("Birthday", "Started pending alarm at :" + new DateTime(time));
        } else {
            cancelNotificationAlarm(context);
            Log.i("Birthday", "Cancelled pending alarm.");
        }
    }
}
