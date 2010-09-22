package cz.krtinec.birthday.dto;

import java.text.ParseException;

import cz.krtinec.birthday.data.BirthdayProvider;

public class BContactDebug extends BContactParent {
	private String bDay;
	
	public BContactDebug(String displayName, long id, String bDay, String lookupKey, String photoId) {
		super(displayName, id, lookupKey, photoId);
		this.bDay = bDay;
		
	}

	public String getbDay() {
		return bDay;
	}
	
	public DateIntegrity getIntegrity() {
		try {
			ParseResult result = BirthdayProvider.tryParseBDay(this.bDay);
			return result.integrity;
		} catch (ParseException e) {
			return DateIntegrity.NONE;
		}
		
	}
    
}
