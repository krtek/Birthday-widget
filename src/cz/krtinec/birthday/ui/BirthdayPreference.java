package cz.krtinec.birthday.ui;

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
