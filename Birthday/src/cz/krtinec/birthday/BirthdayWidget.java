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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.Event;

import java.io.IOException;
import java.io.InputStream;
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
        Utils.startWidgetUpdateAlarm(context);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        if (Utils.WIDGET_UPDATE.equals(intent.getAction())) {
            Log.d("Birthday", "Service started...");
            AppWidgetManager manager = AppWidgetManager.getInstance(ctx);
            List<Event> list = BirthdayProvider.getInstance().upcomingBirthday(ctx);
            int max = getListSize();
            if (max > list.size()) {
                max = list.size();
            }
            list = list.subList(0, max);

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
            Event event = list.get(0);
            setContactName(views, event, R.id.first_name);
            setContactDate(ctx, views, event, R.id.first_date);
            replaceIconWithPhoto(ctx, views, event, R.id.first_icon);
        }
        if (list.size() > 1) {
            Event event = list.get(1);
            setContactName(views, event, R.id.second_name);
            setContactDate(ctx, views, event, R.id.second_date);
            replaceIconWithPhoto(ctx, views, event, R.id.second_icon);
        }
        if (list.size() > 2) {
            Event event = list.get(2);
            setContactName(views, event, R.id.third_name);
            setContactDate(ctx, views, event, R.id.third_date);
            replaceIconWithPhoto(ctx, views, event, R.id.third_icon);
        }
        if (list.size() > 3) {
            Event event = list.get(3);
            setContactName(views, event, R.id.fourth_name);
            setContactDate(ctx, views, event, R.id.fourth_date);
            replaceIconWithPhoto(ctx, views, event, R.id.fourth_icon);
        }
        return views;
    }


    protected void replaceIconWithPhoto(Context ctx, RemoteViews views, Event contact, int viewId) {
        InputStream is = BirthdayProvider.openPhoto(ctx, contact.getContactId());
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
        views.setViewVisibility(viewId, View.VISIBLE);
    }

    private void setContactName(RemoteViews views, Event event, int viewId) {
        SpannableString caption = new SpannableString(event.getDisplayName());
        addSpan(caption, event.getDaysToEvent());
        views.setTextViewText(viewId, caption);
    }

    private void setContactDate(Context ctx, RemoteViews views, Event event, int viewId) {
        SpannableString caption = new SpannableString(event.getDisplayDate(ctx) + " (+" + event.getDaysToEvent() + ")");
        addSpan(caption, event.getDaysToEvent());
        views.setTextViewText(viewId, caption);
    }
    
    private void addSpan(SpannableString caption, int daysToEvent) {
        if (daysToEvent < 6) {
            caption.setSpan(new StyleSpan(Typeface.BOLD), 0, caption.length(), 0);
        }

        if (daysToEvent < 2) {
            caption.setSpan(new ForegroundColorSpan(Color.RED), 0, caption.length(), 0);
        }
    }

}
