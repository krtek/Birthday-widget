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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.Event;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

/**
 * User: lukas.marek@gmail.com
 * Date: 01.08.11
 * Time: 18:01
 */
public class NotificationSender extends BroadcastReceiver {
    private static int NOTIFY_CODE = 1;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        List<Event> list = BirthdayProvider.getInstance().upcomingBirthday(ctx);
        alertOnBirthday(ctx, list);
    }

    private void alertOnBirthday(Context ctx, List<Event> list) {
        for (Event c:list) {
            if (hasBirthdayToday(c)) {
                //should fire alarm
                fireBirthdayAlert(ctx, c, System.currentTimeMillis());
            }
        }
    }

    public static boolean hasBirthdayToday(Event c) {
        return c.getDaysToEvent() == 0;
    }

    public static String formatMessage(Context ctx, Event e) {
        String notificationFormat = ctx.getString(R.string.notification_pattern);
        return MessageFormat.format(notificationFormat, e.getDisplayName(), Utils.getEventLabel(ctx, e));
    }

    public static String formatLabel(Context ctx, Event e) {
        String label = MessageFormat.format(ctx.getString(R.string.notification_alert), Utils.getEventLabel(ctx, e));
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }

    static void fireBirthdayAlert(Context ctx, Event c, Long when) {
        String label = formatLabel(ctx, c);
        Notification n = new Notification(R.drawable.icon, label, when);
        n.flags = n.flags | Notification.FLAG_AUTO_CANCEL;
        n.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
        Intent i = new Intent(ctx, Birthday.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, NOTIFY_CODE, i, PendingIntent.FLAG_CANCEL_CURRENT);

        n.setLatestEventInfo(ctx, label, formatMessage(ctx, c), pendingIntent);

        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify("Birthday", (int)c.getId(), n);
    }
}
