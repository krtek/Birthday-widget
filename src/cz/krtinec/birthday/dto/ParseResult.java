package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

public class ParseResult {
	public ParseResult(LocalDate date, DateIntegrity integrity) {
		this.date = date;
		this.integrity = integrity;
	}
	
	public LocalDate date;
	public DateIntegrity integrity;     	
}
