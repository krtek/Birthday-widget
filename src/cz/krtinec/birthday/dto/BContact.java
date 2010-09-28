package cz.krtinec.birthday.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public final class BContact extends BContactParent implements Comparable<BContact>{
	public static final DateFormat SHORT_FORMAT = new SimpleDateFormat("MMdd");
	private static final Calendar TODAY = Calendar.getInstance();
	
	static Calendar tempCalendar = new GregorianCalendar();
	private Date bDay;
	private Integer age;
	private Integer daysToBirthday;
	private DateIntegrity integrity;
	private String bDaySort;
	private String pivot = SHORT_FORMAT.format(TODAY.getTime());
	private boolean nextYear;
	
	
	public BContact(String displayName, long id, Date bDay, String lookupKey, String photoId, DateIntegrity integrity) {
		super(displayName, id, lookupKey, photoId);		
		this.bDay = bDay;		
		this.integrity = integrity;
		
		tempCalendar.setTime(bDay);
		bDaySort = SHORT_FORMAT.format(this.bDay);
		nextYear = bDaySort.compareTo(pivot) < 0;	
		
		if (DateIntegrity.FULL == this.integrity) {
			age = TODAY.get(Calendar.YEAR) - tempCalendar.get(Calendar.YEAR);	
			age = nextYear ? age + 1: age;
		} else {
			age = null;
		}
			
		//due to leap years
		tempCalendar.set(Calendar.YEAR, TODAY.get(Calendar.YEAR));
		
			
		daysToBirthday = tempCalendar.get(Calendar.DAY_OF_YEAR) - TODAY.get(Calendar.DAY_OF_YEAR);
		if (nextYear) {
			daysToBirthday = daysToBirthday + 365;
		}
				
	}


	public Date getbDay() {
		return bDay;
	}

	public Integer getAge() {
		return age;
	}
	
	public int getDaysToBirthday() {
		return daysToBirthday;
	}
	
	
	@Override
	public String toString() {
		return displayName + ":" + bDay.toGMTString();
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
}
