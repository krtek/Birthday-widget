package cz.krtinec.birthday;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.krtinec.birthday.dto.DateIntegrity;

import android.content.Context;
import android.preference.PreferenceManager;

public class DateFormatter {
	private static DateFormatter instance = null;
	private DateTimeFormatter longFormat;
	private DateTimeFormatter shortFormat;
	
	private DateFormatter(Context ctx) {
		int fIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ctx).getString("date_format", "1"));
		longFormat = DateTimeFormat.forPattern(ctx.getResources().getStringArray(R.array.long_format_values)[fIndex]);
		shortFormat = DateTimeFormat.forPattern(ctx.getResources().getStringArray(R.array.short_format_values)[fIndex]); 
	}
	
	public static DateFormatter getInstance(Context ctx) {
		if (instance == null) {
			instance = new DateFormatter(ctx);
		}
		
		return instance;
	}
	
	public String format(LocalDate date, DateIntegrity integrity) {
		if (date == null) {
			return "--";
		}
		
		DateTimeFormatter format = integrity == DateIntegrity.FULL ? 
			 longFormat : shortFormat;
			
		return format.print(date);
	}
	
	public static void reset() {
		instance = null;
	}
}
