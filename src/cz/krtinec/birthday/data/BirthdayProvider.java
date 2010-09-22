package cz.krtinec.birthday.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.acra.ErrorReporter;

import cz.krtinec.birthday.CrashReporting;
import cz.krtinec.birthday.dto.BContact;
import cz.krtinec.birthday.dto.BContactDebug;
import cz.krtinec.birthday.dto.DateIntegrity;
import cz.krtinec.birthday.dto.ParseResult;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;

public class BirthdayProvider {
	
	private static final DateFormat SHORT_FORMAT = new SimpleDateFormat("MMdd");
    public static final Calendar TODAY = Calendar.getInstance();
    
    private static final DateFormat DB_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat DB_SHORT_FORMAT = new SimpleDateFormat("--MM-dd");
	private static final DateFormat DB_NO_DASH_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	private static final String EVENT_SQL_PART = "substr(replace(" + Event.START_DATE + ", '-', ''),-4)";

	
	
	/**
	 * Returns list of contacts sorted by date of upcoming birthdays. 
	 * @param ctx
	 * @param showMonths - how many months to show ahead.
	 * @return
	 */
	public static List<BContact> upcomingBirthday(Context ctx, int showMonths) {
		//init
		DB_NO_DASH_FORMAT.setLenient(false);
		
		Log.i("BirthdayProvider", "Show months: " + showMonths);
		String today = SHORT_FORMAT.format(TODAY.getTime());
		Calendar endOfSearch = (Calendar) TODAY.clone();
		endOfSearch.add(Calendar.MONTH, showMonths);
		endOfSearch.set(Calendar.DAY_OF_MONTH, endOfSearch.getActualMaximum(Calendar.DAY_OF_MONTH));
		Log.i("BirthdayProvider", "Searching till " + endOfSearch.getTime());
		List<BContact> contacts;
		if (endOfSearch.get(Calendar.YEAR) - TODAY.get(Calendar.YEAR) > 0) {
			contacts = upcomingBirthday(ctx, today, "1231", false);
			contacts.addAll(upcomingBirthday(ctx, "0101", SHORT_FORMAT.format(endOfSearch.getTime()), true));
		} else {
			contacts = upcomingBirthday(ctx, today, SHORT_FORMAT.format(endOfSearch.getTime()), false);
		}
		
		return contacts;
	}
	
	public static List<BContactDebug> allBirthday(Context ctx) {
	  	  Uri dataUri = ContactsContract.Data.CONTENT_URI;
	  	  
	  	  String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME,
	  			  	ContactsContract.CommonDataKinds.Event.CONTACT_ID,
	  			  	ContactsContract.CommonDataKinds.Event.START_DATE,
	  			  	ContactsContract.Contacts.LOOKUP_KEY,
	  			  	ContactsContract.Contacts.PHOTO_ID
	  			  	};
	  	  
	  	  
	  	  Cursor c = ctx.getContentResolver().query(
	  	       dataUri,
	  	       projection, 
	  	       ContactsContract.Data.MIMETYPE + "= ? AND " + 
	  	       Event.TYPE + "=" + Event.TYPE_BIRTHDAY, new String[]{Event.CONTENT_ITEM_TYPE}, 
	  	       ContactsContract.Contacts.DISPLAY_NAME); 
	  	  
	  	List<BContactDebug> result = new ArrayList<BContactDebug>();
	  	while (c!= null && c.moveToNext()) {
	  		result.add(new BContactDebug(c.getString(0), c.getLong(1),c.getString(2), c.getString(3), c.getString(4)));
	  	}
	  	if (c != null) {
	  		c.close();
	  	}
	  	return result;
	}
	
	
    public static List<BContact> upcomingBirthday(Context ctx, String fromDate, String toDate, boolean nextYear) {
  	      	  
  	  Uri dataUri = ContactsContract.Data.CONTENT_URI;
  	  
  	  String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME,
  			  	ContactsContract.CommonDataKinds.Event.CONTACT_ID,
  			  	ContactsContract.CommonDataKinds.Event.START_DATE,
  			  	ContactsContract.Contacts.LOOKUP_KEY,
  			  	ContactsContract.Contacts.PHOTO_ID
  			  	};
  	  
  	  
  	  Cursor c = ctx.getContentResolver().query(
  	       dataUri,
  	       projection, 
  	       ContactsContract.Data.MIMETYPE + "= ? AND " + 
  	       ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + 
  	       " AND " + EVENT_SQL_PART  + " >= ?" + 
  	       " AND " + EVENT_SQL_PART + " <= ?" ,
  	       new String[] {Event.CONTENT_ITEM_TYPE, fromDate, toDate}, EVENT_SQL_PART);
  	  List<BContact> result = new ArrayList<BContact>();
  	  while (c!= null && c.moveToNext()) {
  		  try {
  			  ParseResult parseResult = tryParseBDay(c.getString(2));
  			  result.add(new BContact(c.getString(0), c.getLong(1),parseResult.date, c.getString(3), c.getString(4), nextYear, parseResult.integrity));  		
  		  } catch (ParseException e) {
  			  Log.i("BirthdayProvider", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
  			  ErrorReporter.getInstance().handleSilentException(e);
  		  }
  	  }
  	  if (c != null) {
  		  c.close();
  	  }
  	  return result;
  }
    
    public static ParseResult tryParseBDay(String string) throws ParseException {
    	ParseResult result = new ParseResult();
    	if (string.startsWith("--")) {
    		result.integrity = DateIntegrity.WITHOUT_YEAR;
    		result.date = DB_SHORT_FORMAT.parse(string);
    	} else if (string.indexOf('-') == -1) {
    		result.integrity = DateIntegrity.FULL;
    		result.date = DB_NO_DASH_FORMAT.parse(string);    		
    	} else {
    		result.integrity = DateIntegrity.FULL;
    		result.date = DB_FORMAT.parse(string);    		    		
    	}
    	
    	return result;
	}

    public static InputStream openPhoto(Context ctx, long contactId) {
    	Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(contactId));
		return Contacts.openContactPhotoInputStream(ctx.getContentResolver(), contactUri);
    }
    
}
