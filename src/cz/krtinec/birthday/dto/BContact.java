package cz.krtinec.birthday.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static cz.krtinec.birthday.data.BirthdayProvider.TODAY;

public final class BContact extends BContactParent {
	
	
	static Calendar tempCalendar = new GregorianCalendar();
	private Date bDay;
	private Integer age;
	private Integer daysToBirthday;
	private DateIntegrity integrity;
	
	
	public BContact(String displayName, long id, Date bDay, String lookupKey, String photoId, boolean nextYear, DateIntegrity integrity) {
		super(displayName, id, lookupKey, photoId);		
		this.bDay = bDay;		
		this.integrity = integrity;
		
		tempCalendar.setTime(bDay);
		if (DateIntegrity.FULL == this.integrity) {
			age = TODAY.get(Calendar.YEAR) - tempCalendar.get(Calendar.YEAR);
			age = nextYear ? age + 1 : age;
		} else {
			age = null;
		}
			
		//due to leap years
		tempCalendar.set(Calendar.YEAR, TODAY.get(Calendar.YEAR));
		daysToBirthday = tempCalendar.get(Calendar.DAY_OF_YEAR) - TODAY.get(Calendar.DAY_OF_YEAR);
		daysToBirthday = nextYear ? daysToBirthday + 365 : daysToBirthday;
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
	
	
}
