package cz.krtinec.birthday.dto;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.krtinec.birthday.DateFormatter;

import android.content.Context;


public abstract class Event implements Comparable<Event> {
	
	public static final DateTimeFormatter SHORT_FORMAT = DateTimeFormat.forPattern("MMdd");
	protected final LocalDate today = new LocalDate();

	protected String displayName;
	protected long id;
	protected String lookupKey;
	protected LocalDate eventDate;
	protected DateIntegrity integrity;
	protected String eventDaySort;
	public String pivot = SHORT_FORMAT.print(today);
	protected boolean nextYear;
	
	//static Calendar tempCalendar = new GregorianCalendar();

	private Integer daysToEvent;
	
	
	public Event(String displayName, long id, LocalDate eventDate, String lookupKey , DateIntegrity integrity) {
		this.displayName = displayName;
		this.id = id;
		this.lookupKey = lookupKey;
		this.eventDate = eventDate;
		this.integrity = integrity;
		
		if (this.eventDate != null) {
			eventDaySort = SHORT_FORMAT.print(this.eventDate);
		} else {
			eventDaySort = "0000";
		}
		
		nextYear = eventDaySort.compareTo(pivot) < 0;
			
		if (this.eventDate != null) {
			int year = nextYear ? today.getYear() + 1 : today.getYear();
			LocalDate tempCalendar = new LocalDate(year, eventDate.getMonthOfYear(), eventDate.getDayOfMonth());
			daysToEvent = Days.daysBetween(today, tempCalendar).getDays();
		}
	}
	
	public int getDaysToEvent() {
		return daysToEvent;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public long getId() {
		return id;
	}
	
	public DateIntegrity getIntegrity() {
		return this.integrity;
	}

	@Override
	public String toString() {
		return displayName + ":" + (eventDate == null ? "null" : eventDate.toString());
	}
	

	@Override
	public int compareTo(Event another) {
		if (this.nextYear && !another.nextYear) {
			return 1;
		} else if (!this.nextYear && another.nextYear) {
			return -1;
		}
		
		int bCompare = this.eventDaySort.compareTo(another.eventDaySort);
		if (bCompare == 0) {
			return this.displayName.compareTo(another.displayName);
		} else {
			return bCompare;
		}
	}
	
	public String getDisplayDate(Context ctx) {
		return DateFormatter.getInstance(ctx).format(this.eventDate, this.integrity);
	}

}
