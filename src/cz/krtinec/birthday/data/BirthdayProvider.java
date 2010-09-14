package cz.krtinec.birthday.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.krtinec.birthday.dto.BContact;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class BirthdayProvider {

	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat SHORT_FORMAT = new SimpleDateFormat("MM-dd");
	public static final Calendar TODAY = Calendar.getInstance();
	
	
	/**
	 * Returns list of contacts sorted by date of upcoming birthdays. 
	 * @param ctx
	 * @param showMonths - how many months to show ahead.
	 * @return
	 */
	public static List<BContact> upcomingBirthday(Context ctx, int showMonths) {
		Log.i("BirthdayProvider", "Show months: " + showMonths);
		String today = SHORT_FORMAT.format(TODAY.getTime());
		Calendar endOfSearch = (Calendar) TODAY.clone();
		endOfSearch.add(Calendar.MONTH, showMonths);
		endOfSearch.set(Calendar.DAY_OF_MONTH, endOfSearch.getActualMaximum(Calendar.DAY_OF_MONTH));
		Log.i("BirthdayProvider", "Searching till " + endOfSearch.getTime());
		List<BContact> contacts;
		if (endOfSearch.get(Calendar.YEAR) - TODAY.get(Calendar.YEAR) > 0) {
			contacts = upcomingBirthday(ctx, today, "12-31", false);
			contacts.addAll(upcomingBirthday(ctx, "01-01", SHORT_FORMAT.format(endOfSearch.getTime()), true));
		} else {
			contacts = upcomingBirthday(ctx, today, SHORT_FORMAT.format(endOfSearch.getTime()), false);
		}
		
		return contacts;
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
  	       " AND substr(" + ContactsContract.CommonDataKinds.Event.START_DATE + ",6) >= ?" + 
  	       " AND substr(" + ContactsContract.CommonDataKinds.Event.START_DATE + ",6) <= ?" ,
  	       new String[] {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, fromDate, toDate}, 
  	       "substr("+ ContactsContract.CommonDataKinds.Event.START_DATE +",6)");
  	  List<BContact> result = new ArrayList<BContact>();
  	  int i=0;
  	  while (c!= null && c.moveToNext()) {
  		  try {
			result.add(new BContact(c.getString(0), c.getLong(1), FORMAT.parse(c.getString(2)), c.getString(3), c.getString(4), nextYear));
		} catch (ParseException e) {
			throw new RuntimeException("Cannot parse: " + c.getString(2), e);
		}
  		  i++;
  	  }
  	  c.close();
  	  return result;
  }
    
    public static Uri getPhoto(Context ctx, long contactId, String lookupKey) {
    	//Uri uri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
    	Uri uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
    	uri = ContactsContract.Contacts.lookupContact(ctx.getContentResolver(), uri);
//    	ContactsContract.Contacts.CONTENT_LOOKUP_URI;
    	uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    	return uri;
    }  
    
    public static InputStream openPhoto(Context ctx, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = ctx.getContentResolver().query(photoUri,
             new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
    
}
