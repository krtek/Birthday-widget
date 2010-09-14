package cz.krtinec.birthday.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static cz.krtinec.birthday.data.BirthdayProvider.TODAY;

public final class BContact {
	static Calendar tempCalendar = new GregorianCalendar();
	private String displayName;
	private long id; 
	private Date bDay;
	private int age;
	private int daysToBirthday;
	private String lookupKey;
	private String photoId;
	
	
	public BContact(String displayName, long id, Date bDay, String lookupKey, String photoId, boolean nextYear) {
		this.displayName = displayName;
		this.id = id;
		this.bDay = bDay;
		this.lookupKey = lookupKey;
		this.photoId = photoId;
		
		tempCalendar.setTime(bDay);
		age = TODAY.get(Calendar.YEAR) - tempCalendar.get(Calendar.YEAR);
		age = nextYear ? age + 1 : age;
		//kvuli prestupnejm rokum
		tempCalendar.set(Calendar.YEAR, TODAY.get(Calendar.YEAR));
		daysToBirthday = tempCalendar.get(Calendar.DAY_OF_YEAR) - TODAY.get(Calendar.DAY_OF_YEAR);
		daysToBirthday = nextYear ? daysToBirthday + 365 : daysToBirthday;
	}


	public String getDisplayName() {
		return displayName;
	}

	public long getId() {
		return id;
	}


	public Date getbDay() {
		return bDay;
	}

	public int getAge() {
		return age;
	}
	
	public int getDaysToBirthday() {
		return daysToBirthday;
	}
	
	public String getLookupKey() {
		return lookupKey;
	}
	
	public String getPhotoId() {
		return photoId;
	}
	
	@Override
	public String toString() {
		return displayName + ":" + bDay.toGMTString();
	}
	
	
}
