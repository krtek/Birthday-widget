package cz.krtinec.birthday;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.krtinec.birthday.dto.DateIntegrity;

import android.content.Context;
import android.preference.PreferenceManager;

public class DateFormatter {
	private static DateFormatter instance = null;
	private SimpleDateFormat longFormat;
	private SimpleDateFormat shortFormat;
	
	private DateFormatter(Context ctx) {
		int fIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ctx).getString("date_format", "1"));
		longFormat = new SimpleDateFormat(ctx.getResources().getStringArray(R.array.long_format_values)[fIndex]);
		shortFormat = new SimpleDateFormat(ctx.getResources().getStringArray(R.array.short_format_values)[fIndex]); 
	}
	
	public static DateFormatter getInstance(Context ctx) {
		if (instance == null) {
			instance = new DateFormatter(ctx);
		}
		
		return instance;
	}
	
	public String format(Date date, DateIntegrity integrity) {
		if (date == null) {
			return "--";
		}
		
		SimpleDateFormat format = integrity == DateIntegrity.FULL ? 
			 longFormat : shortFormat;
			
		return format.format(date);
	}
	
	public static void reset() {
		instance = null;
	}
}
