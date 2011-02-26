/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */

package cz.krtinec.birthday.dto;

import org.joda.time.Days;
import org.joda.time.IllegalFieldValueException;
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
    protected String eventDaySort;
	public String pivot = SHORT_FORMAT.print(today);
	protected boolean nextYear;
	
	//static Calendar tempCalendar = new GregorianCalendar();

	private Integer daysToEvent;
    protected LocalDate eventDate;
    protected DateIntegrity integrity;
    private long rawContactId;


    public Event(String displayName, long id, LocalDate eventDate, String lookupKey , DateIntegrity integrity, long rawContactId) {
        this.integrity = integrity;
        this.displayName = displayName;
		this.id = id;
		this.lookupKey = lookupKey;
        this.eventDate = eventDate;

        if (this.eventDate != null) {
			eventDaySort = SHORT_FORMAT.print(this.eventDate);
		} else {
			eventDaySort = "0000";
		}
		
		nextYear = eventDaySort.compareTo(pivot) < 0;
			
		if (this.eventDate != null) {
			int year = nextYear ? today.getYear() + 1 : today.getYear();
            LocalDate tempCalendar;
            try {
			    tempCalendar = new LocalDate(year, eventDate.getMonthOfYear(), eventDate.getDayOfMonth());
            } catch (IllegalFieldValueException e) {
                //Probably February 29th
                tempCalendar = new LocalDate(year, eventDate.getMonthOfYear(), eventDate.getDayOfMonth() - 1);
            }
			daysToEvent = Days.daysBetween(today, tempCalendar).getDays();
		}
        this.rawContactId = rawContactId;
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

    public int getYear() {
        return integrity == DateIntegrity.FULL ? eventDate.getYear() : today.getYear();
    }

    public int getMonthOfYear() {
        return eventDate.getMonthOfYear();
    }

    public int getDayOfMonth() {
        return eventDate.getDayOfMonth();
    }

    public Long getRawContactId() {
        return rawContactId;
    }
}
