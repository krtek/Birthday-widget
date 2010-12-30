package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;


public class EventDebug extends Event {
	
	private String bDayString;
	
	public EventDebug(String displayName, long id, LocalDate bDay, String bDayString, String lookupKey, DateIntegrity integrity) {
		super(displayName, id, bDay, lookupKey, integrity);
		this.bDayString = bDayString;		
	}

	public String getbDayString() {
		return bDayString;
	}    
}
