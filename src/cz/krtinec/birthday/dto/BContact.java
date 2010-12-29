package cz.krtinec.birthday.dto;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.krtinec.birthday.DateFormatter;

import android.content.Context;


public class BContact implements Comparable<BContact> {
	
	public static final DateTimeFormatter SHORT_FORMAT = DateTimeFormat.forPattern("MMdd");
	protected final LocalDate today = new LocalDate();

	protected String displayName;
	protected long id;
	protected String lookupKey;
	protected String photoId;
	protected LocalDate bDay;
	protected DateIntegrity integrity;
	protected String bDaySort;
	public String pivot = SHORT_FORMAT.print(today);
	protected boolean nextYear;
	
	//static Calendar tempCalendar = new GregorianCalendar();
	
	private Integer age;
	private Integer daysToBirthday;
	
	
	public BContact(String displayName, long id, LocalDate bDay, String lookupKey, String photoId, DateIntegrity integrity) {
		this.displayName = displayName;
		this.id = id;
		this.lookupKey = lookupKey;
		this.photoId = photoId;			
		this.bDay = bDay;		
		this.integrity = integrity;
		
		if (this.bDay != null) {			
			bDaySort = SHORT_FORMAT.print(this.bDay);
		} else {
			bDaySort = "0000";
		}
		
		nextYear = bDaySort.compareTo(pivot) < 0;
		
		if (DateIntegrity.FULL == this.integrity) {
			age = today.getYear() - bDay.getYear();
			age = nextYear ? age + 1: age;
		} else {
			age = null;
		}
			
		if (this.bDay != null) {
			int year = nextYear ? today.getYear() + 1 : today.getYear();
			LocalDate tempCalendar = new LocalDate(year, bDay.getMonthOfYear(), bDay.getDayOfMonth());
			daysToBirthday = Days.daysBetween(today, tempCalendar).getDays();
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
	
	public String getbDaySort() {
		return bDaySort;
	}

	@Override
	public String toString() {
		return displayName + ":" + bDay == null ? "null" : bDay.toString();
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
		return DateFormatter.getInstance(ctx).format(this.bDay, this.integrity);
	}

}
