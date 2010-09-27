package cz.krtinec.birthday.data;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.acra.ErrorReporter;

import cz.krtinec.birthday.dto.BContact;
import cz.krtinec.birthday.dto.BContactDebug;
import cz.krtinec.birthday.dto.DateIntegrity;
import cz.krtinec.birthday.dto.ParseResult;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;

public class BirthdayProvider {    
	
	private static BirthdayProvider instance = new BirthdayProvider();
	private List<DatePattern> patterns = new ArrayList<DatePattern>();
	
	private BirthdayProvider() {
		patterns.add(new DatePattern("\\d{4}\\-\\d{1,2}\\-\\d{1,2}$", "yyyy-MM-dd", DateIntegrity.FULL));
		patterns.add(new DatePattern("\\-\\-\\d{1,2}\\-\\d{1,2}$", "--MM-dd", DateIntegrity.WITHOUT_YEAR));
		patterns.add(new DatePattern("\\d{8}$", "yyyyMMdd", DateIntegrity.FULL));
		patterns.add(new DatePattern("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.*", 
				"yyyy-MM-dd'T'hh:mm:ss", DateIntegrity.FULL));
		
	}
	
	public static BirthdayProvider getInstance() {
		return instance;
	}
	
	
	public List<BContact> upcomingBirthday(Context ctx) {
		Log.i("Birthday provider", "Going to get upcoming bdays");
		long start = System.currentTimeMillis();
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
	  	  
	  	Set<BContact> result = new TreeSet<BContact>();
	  	while (c!= null && c.moveToNext()) {
	  		  try {
	  			  ParseResult parseResult = tryParseBDay(c.getString(2));
	  			  result.add(new BContact(c.getString(0), c.getLong(1),parseResult.date, c.getString(3), c.getString(4), parseResult.integrity));  		
	  		  } catch (ParseException e) {
	  			  Log.i("BirthdayProvider", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
	  			  ErrorReporter.getInstance().handleSilentException(e);
	  		  }
	  	}	  	
	  	if (c != null) {
	  		c.close();
	  	}	  		  
	  	Log.i("Birthday provider", "Loaded in " + (System.currentTimeMillis() - start) + " [ms]");
	  	start = System.currentTimeMillis();
	  	List<BContact> result2 = new ArrayList<BContact>(result);
	  	Log.i("Birthday provider", "Converted in " + (System.currentTimeMillis() - start) + "[ms]");
	  	return result2;
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

    
    public ParseResult tryParseBDay(String string) throws ParseException {
    	for (DatePattern pat: patterns) {
    		if (string.matches(pat.pattern)) {
    			return new ParseResult(pat.format.parse(string), pat.integrity);
    		}
    	}
    	//handle timestamp value
    	if (string.matches("[-]{0,1}\\d*")) {
    		Date d = new Date();
    		d.setTime(Long.parseLong(string));
    		return new ParseResult(d, DateIntegrity.FULL);
    	}
    	
    	throw new ParseException("Cannot parse: " + string, 0);
    	
	}

    public static InputStream openPhoto(Context ctx, long contactId) {
    	Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(contactId));
		return Contacts.openContactPhotoInputStream(ctx.getContentResolver(), contactUri);
    }
        
    class DatePattern {
    	String pattern;
    	SimpleDateFormat format;
    	DateIntegrity integrity;
    	
    	public DatePattern(String pattern, String format, DateIntegrity integrity) {
    		this.pattern = pattern;    		
    		this.format = new SimpleDateFormat(format);
    		this.integrity = integrity;
		}
    }
    
}
