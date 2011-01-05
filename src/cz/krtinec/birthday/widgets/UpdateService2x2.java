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

package cz.krtinec.birthday.widgets;


import cz.krtinec.birthday.R;
import cz.krtinec.birthday.dto.Event;
import android.content.ComponentName;
import android.widget.RemoteViews;

public class UpdateService2x2 extends UpdateService {
	
	int getLayout() {
		return R.layout.widget2x2;
	}

	@Override
	public ComponentName getComponentName() {
		return new ComponentName(this, BirthdayWidget2x2.class);
	}

	@Override
	public RemoteViews updateViews() {
		RemoteViews views = new RemoteViews(this.getPackageName(), getLayout());		
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
		if (list.size() > 2) {
			Event contact = list.get(2);
			views.setTextViewText(R.id.third_name, contact.getDisplayName());
			views.setTextViewText(R.id.third_date, contact.getDisplayDate(this));
			replaceIconWithPhoto(views, contact, R.id.third_icon);
		} else {
			views.setTextViewText(R.id.third_name, getText(R.string.no_name_found));
		}
		if (list.size() > 3) {
			Event contact = list.get(3);
			views.setTextViewText(R.id.fourth_name, contact.getDisplayName());
			views.setTextViewText(R.id.fourth_date, contact.getDisplayDate(this));
			replaceIconWithPhoto(views, contact, R.id.fourth_icon);
		} else {
			views.setTextViewText(R.id.fourth_name, getText(R.string.no_name_found));
		}

		return views;
	}

}
