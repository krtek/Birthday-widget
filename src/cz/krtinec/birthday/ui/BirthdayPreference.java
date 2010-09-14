package cz.krtinec.birthday.ui;

import cz.krtinec.birthday.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BirthdayPreference extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		addPreferencesFromResource(R.xml.preferences);		
	}

}
