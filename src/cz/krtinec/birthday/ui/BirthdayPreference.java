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

package cz.krtinec.birthday.ui;

import cz.krtinec.birthday.DateFormatter;
import cz.krtinec.birthday.R;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;

public class BirthdayPreference extends PreferenceActivity {
	int widgetId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		DateFormatter.reset();
		addPreferencesFromResource(R.xml.preferences);
		setResult(RESULT_CANCELED);
		 Intent intent = getIntent();
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	        	widgetId = extras.getInt(
	                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	        }
		
		setContentView(R.layout.prefs_layout);
		if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
		
			findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
					setResult(RESULT_OK, resultValue);
					finish();
				}
			});
		} else {
			//called from application
			findViewById(R.id.save_button).setVisibility(Button.GONE);
		}
		
	}

}
