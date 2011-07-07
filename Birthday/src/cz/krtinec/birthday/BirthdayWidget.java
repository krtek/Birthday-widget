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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import cz.krtinec.birthday.dto.Event;

/**
 * This is just for backward compatibility.
 * @author krtek
 *
 */
public class BirthdayWidget extends AppWidgetProvider {
	
	 public void onUpdate(Context context, AppWidgetManager appWidgetManager,
		        int[] appWidgetIds) {
	        // To prevent any ANR timeouts, we perform the update in a service
	        context.startService(new Intent(context, UpdateService.class));
	 }	
	
	
	public static class UpdateService extends cz.krtinec.birthday.widgets.UpdateService {
		
		public ComponentName getComponentName() {
			return new ComponentName(this, BirthdayWidget.class);
		}
		
		public RemoteViews updateViews() {
			RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget2x1);			
			if (list.size() > 0) {
				Event contact = list.get(0);
				views.setTextViewText(R.id.first_name, contact.getDisplayName());
				views.setTextViewText(R.id.first_date, contact.getDisplayDate(this));	
				replaceIconWithPhoto(views, contact, R.id.first_icon);					
			} else {
				views.setTextViewText(R.id.first_name, getText(R.string.no_name_found));				
			}
			if (list.size() > 1) {
				Event contact = list.get(1);
				views.setTextViewText(R.id.second_name, contact.getDisplayName());
				views.setTextViewText(R.id.second_date, contact.getDisplayDate(this));
				replaceIconWithPhoto(views, contact, R.id.second_icon);
			} else {
				views.setTextViewText(R.id.second_name, getText(R.string.no_name_found));
			}
			return views;
		}	
	}
}
