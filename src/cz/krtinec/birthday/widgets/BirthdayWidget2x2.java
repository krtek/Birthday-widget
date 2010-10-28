package cz.krtinec.birthday.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class BirthdayWidget2x2 extends AppWidgetProvider {
	 public void onUpdate(Context context, AppWidgetManager appWidgetManager,
		        int[] appWidgetIds) {
	        // To prevent any ANR timeouts, we perform the update in a service
	        context.startService(new Intent(context, UpdateService2x2.class));
	 }
}
