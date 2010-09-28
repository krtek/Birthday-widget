package cz.krtinec.birthday.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cz.krtinec.birthday.R;

import android.content.Context;
import android.preference.PreferenceManager;


public class BContact implements Comparable<BContact> {
	
	public static final DateFormat SHORT_FORMAT = new SimpleDateFormat("MMdd");
	protected static final Calendar TODAY = Calendar.getInstance();

	protected String displayName;
	protected long id;
	protected String lookupKey;
	protected String photoId;
	protected Date bDay;
	protected DateIntegrity integrity;
	protected String bDaySort;
	protected String pivot = SHORT_FORMAT.format(TODAY.getTime());
	protected boolean nextYear;
	
	static Calendar tempCalendar = new GregorianCalendar();
	
	private Integer age;
	private Integer daysToBirthday;
	
	
	public BContact(String displayName, long id, Date bDay, String lookupKey, String photoId, DateIntegrity integrity) {
		this.displayName = displayName;
		this.id = id;
		this.lookupKey = lookupKey;
		this.photoId = photoId;			
		this.bDay = bDay;		
		this.integrity = integrity;
		
		if (this.bDay != null) {
			tempCalendar.setTime(bDay);
			bDaySort = SHORT_FORMAT.format(this.bDay);
		} else {
			bDaySort = "0000";
		}
		
		nextYear = bDaySort.compareTo(pivot) < 0;	
		
		if (DateIntegrity.FULL == this.integrity) {
			age = TODAY.get(Calendar.YEAR) - tempCalendar.get(Calendar.YEAR);	
			age = nextYear ? age + 1: age;
		} else {
			age = null;
		}
			
		if (this.bDay != null) {
			//due to leap years
			tempCalendar.set(Calendar.YEAR, TODAY.get(Calendar.YEAR));	
				
			daysToBirthday = tempCalendar.get(Calendar.DAY_OF_YEAR) - TODAY.get(Calendar.DAY_OF_YEAR);
			if (nextYear) {
				daysToBirthday = daysToBirthday + 365;
			}
		}
	}

	public Integer getAge() {
		return age;
	}
	
	public int getDaysToBirthday() {
		return daysToBirthday;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public long getId() {
		return id;
	}
	
	public String getPhotoId() {
		return photoId;
	}
	
	public String getLookupKey() {
		return lookupKey;
	}
	
	public DateIntegrity getIntegrity() {
		return this.integrity;
	}
	
	@Override
	public String toString() {
		return displayName + ":" + bDay == null ? "null" : bDay.toGMTString();
	}
	
	@Override
	public int compareTo(BContact another) {
		if (this.nextYear && !another.nextYear) {
			return 1;
		} else if (!this.nextYear && another.nextYear) {
			return -1;
		}
		
		int bCompare = this.bDaySort.compareTo(another.bDaySort);
		if (bCompare == 0) {
			return this.displayName.compareTo(another.displayName);
		} else {
			return bCompare;
		}
	}
	
	public String getDisplayDate(Context ctx) {
		if (this.bDay == null) {
			return "--";
		}
		int fIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ctx).getString("date_format", "1"));
		String format = integrity == DateIntegrity.FULL ? 
			ctx.getResources().getStringArray(R.array.long_format_values)[fIndex] :
				ctx.getResources().getStringArray(R.array.short_format_values)[fIndex];
			
		return new SimpleDateFormat(format).format(this.bDay);
	}

}
