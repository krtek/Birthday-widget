package cz.krtinec.birthday.dto;

import java.util.Date;

public class ParseResult {
	public ParseResult(Date date, DateIntegrity integrity) {
		this.date = date;
		this.integrity = integrity;
	}
	
	public Date date;
	public DateIntegrity integrity;     	
}
