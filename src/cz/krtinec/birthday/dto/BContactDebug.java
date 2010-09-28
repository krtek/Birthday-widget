package cz.krtinec.birthday.dto;

import java.util.Date;


public class BContactDebug extends BContact {
	
	private String bDayString;
	
	public BContactDebug(String displayName, long id, Date bDay, String bDayString, String lookupKey, String photoId, DateIntegrity integrity) {
		super(displayName, id, bDay, lookupKey, photoId, integrity);
		this.bDayString = bDayString;		
	}

	public String getbDayString() {
		return bDayString;
	}    
}
