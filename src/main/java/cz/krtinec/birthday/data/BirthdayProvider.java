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

package cz.krtinec.birthday.data;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.os.RemoteException;
import cz.krtinec.birthday.dto.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class BirthdayProvider {

    private static BirthdayProvider instance;
    private static List<DatePattern> PATTERNS = new ArrayList<DatePattern>();
    private static String TIMESTAMP_PATTERN = "[-]{0,1}\\d{9,}";
    private static String[] BIRTHDAY_PROJECTION = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Event.CONTACT_ID,
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Event.TYPE,
            ContactsContract.CommonDataKinds.Event.LABEL,
            ContactsContract.CommonDataKinds.Event._ID,
            ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID
    };

    private final Account ACCOUNT_PHONE;
    private final Account ACCOUNT_UNKNOWN;

    static {
        PATTERNS.add(new DatePattern("(0000)\\-\\d{1,2}\\-\\d{1,2}", "0000-MM-dd", DateIntegrity.WITHOUT_YEAR));
        PATTERNS.add(new DatePattern("(0000)\\d{4}", "0000MMdd", DateIntegrity.WITHOUT_YEAR));
        PATTERNS.add(new DatePattern("\\d{4}\\-\\d{1,2}\\-\\d{1,2}", "yyyy-MM-dd", DateIntegrity.FULL));
        PATTERNS.add(new DatePattern("\\d{2}\\-\\d{1,2}\\-\\d{1,2}", "yy-MM-dd", DateIntegrity.FULL));
        PATTERNS.add(new DatePattern("\\-\\-\\d{1,2}\\-\\d{1,2}", "--MM-dd", DateIntegrity.WITHOUT_YEAR));
        PATTERNS.add(new DatePattern("\\d{8}", "yyyyMMdd", DateIntegrity.FULL));
        PATTERNS.add(new DatePattern("\\d{2}/\\d{2}/\\d{4}", "MM/dd/yyyy", DateIntegrity.FULL));
        PATTERNS.add(new DatePattern("\\d{2}/\\d{2}", "MM/dd", DateIntegrity.WITHOUT_YEAR));
    }

    private BirthdayProvider() {
        ACCOUNT_PHONE = new Account("Internal", "Phone");
        ACCOUNT_UNKNOWN = new Account("Unknown", "Phone");
    }

    public synchronized static BirthdayProvider getInstance() {
        if (instance == null) {
            instance = new BirthdayProvider();
        }
        return instance;
    }

    public List<EditableEvent> getEvents(Context ctx, long rawContactId) {
        Log.i("Birthday", "Going to get events for " + rawContactId);

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Event._ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL,
        };

        Cursor c = ctx.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                ContactsContract.Data.RAW_CONTACT_ID + "= ? AND " +
                        ContactsContract.Data.MIMETYPE + "= ? AND " +
                        "(" + ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_OTHER + ")",
                new String[]{String.valueOf(rawContactId), ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                null);

        List<EditableEvent> events = new ArrayList<EditableEvent>();
        while (c != null && c.moveToNext()) {
            try {
                ParseResult parseResult = tryParseBDay(c.getString(1));

                EditableEvent evt = new EditableEvent(c.getLong(0),
                        EventType.getEventType(c.getInt(2)),
                        parseResult.date,
                        parseResult.integrity,
                        c.getString(3));

                events.add(evt);

            } catch (ParseException e) {
                Log.i("Birthday", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
            } catch (IllegalArgumentException e) {
                Log.i("Birthday", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
            } catch (NullPointerException e) {
                Log.i("Birthday", "Skipping contact id: " + c.getString(1) + " due to NPE.");
            }
        }

        if (c != null) {
            c.close();
        }
        Log.i("Birthday", "Returning " + events);
        return events;
    }


    public ContactInfo getContact(Context ctx, Uri contact) {
        Cursor c = ctx.getContentResolver().query(contact, null, null, null, null);
        String displayName = null;
        Long contactID = 0L;
        if (c != null && c.moveToFirst()) {
            int id = c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
            if (id == -1) {
                id = c.getColumnIndex(ContactsContract.Contacts._ID);
            }
            contactID = c.getLong(id);
            Log.d("Birthday", "getContactName(), contactID: " + contactID);
            c.close();
            Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(contactID));
            c = ctx.getContentResolver().query(contactUri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                displayName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactID = c.getLong(c.getColumnIndex(Contacts._ID));
            } else {
                Log.e("Birthday", "Cannot find contact with contactID: " + contactID);
                displayName = "Invalid contact";
                contactID = 0L;
            }
        }
        c.close();
        return new ContactInfo(displayName, contactID);

    }


    public List<Event> upcomingBirthday(Context ctx) {
        Log.i("Birthday", "Going to get upcoming events");
        long start = System.currentTimeMillis();
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        Cursor c = ctx.getContentResolver().query(
                dataUri,
                BIRTHDAY_PROJECTION,
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        "(" + ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM + " OR " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_OTHER + ")",
                new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.DISPLAY_NAME);

        Set<Event> result = new TreeSet<Event>();
        while (c != null && c.moveToNext()) {
            try {
                result.add(parseCursor(c));

            } catch (ParseException e) {
                Log.i("Birthday", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
            } catch (IllegalArgumentException e) {
                Log.i("Birthday", "Skipping " + c.getString(0) + " due to unparseable bday date (" + c.getString(2) + ")");
            } catch (NullPointerException e) {
                Log.i("Birthday", "Skipping contact id: " + c.getString(1) + " due to NPE.");
            }
        }
        if (c != null) {
            c.close();
        }
        Log.i("Birthday", "Loaded in " + (System.currentTimeMillis() - start) + " [ms]");
        start = System.currentTimeMillis();
        List<Event> result2 = new ArrayList<Event>(result);
        Log.i("Birthday", "Converted in " + (System.currentTimeMillis() - start) + " [ms]");
        return result2;
    }

    private Event parseCursor(Cursor c) throws ParseException {
        ParseResult parseResult = tryParseBDay(c.getString(2));
        if (ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY == c.getInt(5)) {
            return new BirthdayEvent(c.getString(0), c.getLong(1), parseResult.date, c.getString(3),
                    parseResult.integrity, c.getLong(8), Zodiac.toZodiac(parseResult.date));
        } else if (ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY == c.getInt(5)) {
            return new AnniversaryEvent(c.getString(0), c.getLong(1), parseResult.date, c.getString(3),
                    parseResult.integrity, c.getLong(8));
        } else if (ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM == c.getInt(5)) {
            return new CustomEvent(c.getString(0), c.getLong(1), parseResult.date, c.getString(3),
                    parseResult.integrity, c.getLong(8), c.getString(6));
        } else if (ContactsContract.CommonDataKinds.Event.TYPE_OTHER == c.getInt(5)) {
            return new OtherEvent(c.getString(0), c.getLong(1), parseResult.date, c.getString(3),
                    parseResult.integrity, c.getLong(8));
        }
        return null;
    }


    public List<EventDebug> allBirthday(Context ctx) {
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID,
        };


        Cursor c = ctx.getContentResolver().query(
                dataUri,
                projection,
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.DISPLAY_NAME);

        //TODO add reason (Exception message)
        Set<EventDebug> result = new TreeSet<EventDebug>();
        while (c != null && c.moveToNext()) {
            ParseResult parseResult;
            try {
                parseResult = tryParseBDay(c.getString(2));
                result.add(new EventDebug(c.getString(0), c.getLong(1), parseResult.date, c.getString(2), c.getString(3), parseResult.integrity, c.getLong(5)));
            } catch (Exception e) {
                result.add(new EventDebug(c.getString(0), c.getLong(1), null, c.getString(2), c.getString(3), DateIntegrity.NONE, c.getLong(5)));
            }

        }
        if (c != null) {
            c.close();
        }
        return new ArrayList<EventDebug>(result);
    }


    /**
     * @param string
     * @return
     * @throws ParseException
     * @throws IllegalArgumentException
     */
    public static ParseResult tryParseBDay(String string) throws ParseException {
        if (string == null) {
            throw new ParseException("Cannot parse: <null>", 0);
        }

        if (string.matches(TIMESTAMP_PATTERN)) {
            LocalDate date = new DateTime(Long.parseLong(string)).withZone(DateTimeZone.UTC).toLocalDate();
            return new ParseResult(date, DateIntegrity.FULL);
        }

        Matcher m;
        for (DatePattern pat : PATTERNS) {
            m = Pattern.compile(pat.pattern).matcher(string);
            if (m.find()) {
                string = string.substring(m.start(), m.end());
                LocalDate date = pat.format.withZone(DateTimeZone.UTC).parseDateTime(string).toLocalDate();
                return new ParseResult(date, pat.integrity);
            }
        }

        throw new ParseException("Cannot parse: " + string, 0);

    }

    public static InputStream openPhoto(Context ctx, long contactId) {
        Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(contactId));
        return Contacts.openContactPhotoInputStream(ctx.getContentResolver(), contactUri);
    }

    public static String getPhoneNumber(Context ctx, long id) {
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id
                        + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE
                        + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                , null, null);


        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    public static String getEmail(Context ctx, long id) {
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Email.DATA},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id
                , null, null);


        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    public ContentProviderResult[] performUpdate(Context ctx, ArrayList<ContentProviderOperation> ops) throws RemoteException, OperationApplicationException {
        Log.i("Birthday", "Going to update events: " + ops);
        ContentProviderResult[] result = ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        Log.i("Birthday", "Update result: ");
        return result;
    }

    public Map<Account, Long> getRawContactIds(Context ctx, Long contactId) {
        Cursor cursor = ctx.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]
                        {ContactsContract.RawContacts._ID,
                                ContactsContract.RawContacts.ACCOUNT_TYPE,
                                ContactsContract.RawContacts.ACCOUNT_NAME},
                ContactsContract.RawContacts.CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)}, null);

        Account[] accounts = AccountManager.get(ctx).getAccounts();
        Log.i("Birthday", "Available accounts: " + accounts.length);
        Map<Account, Long> result = new HashMap<Account, Long>();
        while (cursor.moveToNext()) {
            Log.d("Birthday",
                    "RawId: " + cursor.getLong(0) + ", type: " + cursor.getString(1) + ", name: " + cursor.getString(2));
            result.put(findAccount(accounts, cursor.getString(1), cursor.getString(2)), cursor.getLong(0));
        }
        return result;
    }


    /**
     * @param list
     * @param type
     * @param name
     * @return Never returns null.
     */
    private Account findAccount(Account[] list, String type, String name) {
        Log.d("Birthday", "Matching " + name + " and " + type);
        if (name == null && type == null) {
            return ACCOUNT_PHONE;
        }
        for (Account a : list) {
            Log.d("Birthday", "Account Name: " + a.name + " Account Type: " + a.type);
            if (a.type.equals(type) && a.name.equals(name)) {
                return a;
            }
        }
        Log.d("Birthday", "Match not found");
        return ACCOUNT_UNKNOWN;
    }


    static class DatePattern {
        String pattern;
        DateTimeFormatter format;
        DateIntegrity integrity;

        public DatePattern(String pattern, String format, DateIntegrity integrity) {
            this.pattern = pattern;
            this.format = DateTimeFormat.forPattern(format);
            this.integrity = integrity;
        }
    }

}


