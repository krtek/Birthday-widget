package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;


public class BContactDebug extends BContact {
	
	private String bDayString;
	
	public BContactDebug(String displayName, long id, LocalDate bDay, String bDayString, String lookupKey, String photoId, DateIntegrity integrity) {
		super(displayName, id, bDay, lookupKey, photoId, integrity);
		this.bDayString = bDayString;		
	}

	public String getbDayString() {
		return bDayString;
	}    
}
