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

package cz.krtinec.birthday.ui;

import android.accounts.Account;
import android.app.*;
import android.content.*;

import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cz.krtinec.birthday.Birthday;
import cz.krtinec.birthday.DateFormatter;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.Utils;
import cz.krtinec.birthday.data.BirthdayService;

import cz.krtinec.birthday.data.StockPhotoLoader;
import cz.krtinec.birthday.dto.*;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * Allows edit particular event.
 */
public class EditActivity extends Activity {
    private static final int DIALOG_EDIT_DATE = 12;
    private static final int DIALOG_SAVE_FAILED = 13;
    private static final int DIALOG_SAVING = 14;
    private static final int DIALOG_CHOOSE_ACCOUNT = 15;
    private static EditableEvent eventToEdit = null;
    private static EditAdapter listAdapter;
    private static Map<Account, Long> accountMap;
    private static Account[] accounts;
    private static Button saveButton;
    private static long rawContactId;

    private static SpinnerItem[] SPINNER_ITEMS;
    private static final String EVENTS_LIST = "EVENTS_LIST";
    private static final String EVENTS_BASE = "EVENTS_BASE";
    private static final String EVENTS_DELETED = "EVENTS_DELETED";
    private static final String RAW_CONTACT_ID = "RAW_CONTACT_ID";

    private StockPhotoLoader photoLoader;
    @Inject private BirthdayService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        SPINNER_ITEMS = new SpinnerItem[] {
            new SpinnerItem(EventType.BIRTHDAY, this.getString(R.string.birthday)),
            new SpinnerItem(EventType.ANNIVERSARY, this.getString(R.string.anniversary)),
            new SpinnerItem(EventType.OTHER, this.getString(R.string.other))
        };
        photoLoader = new StockPhotoLoader(service, this, R.drawable.icon);

        Intent i = getIntent();
        if (i != null) {
            Uri contact = i.getData();
            Log.i("Birthday", "Going to edit " + contact);
            ContactInfo contactInfo = service.getContact(contact);
            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(contactInfo.contactName);
            //TODO LOAD Photo!
            //photoLoader.loadPhoto((ImageView) findViewById(R.id.bicon), contactInfo.contactID);

            //empty intent
            setIntent(null);
            if (!contact.toString().contains("raw_contact")) {
                Log.d("Birthday", "Must choose raw contact.");
                Map<Account, Long> rawIds =
                    service.getRawContactIds(Long.parseLong(contact.getLastPathSegment()));
                Log.i("Birthday", rawIds.toString());
                accountMap = rawIds;
                showDialog(DIALOG_CHOOSE_ACCOUNT);
            } else {
                Log.d("Birthday", "Get raw contact id for: " + contact);
                rawContactId = Long.parseLong(contact.getLastPathSegment());
                onRawContactIdSelected(rawContactId);
            }
        } else {
        	onRawContactIdSelected(rawContactId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoLoader.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        photoLoader.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        photoLoader.pause();
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        instanceState.putParcelableArrayList(EVENTS_LIST, (ArrayList<EditableEvent>) listAdapter.list);
        instanceState.putParcelableArrayList(EVENTS_BASE, (ArrayList<EditableEvent>) listAdapter.base);
        instanceState.putParcelableArrayList(EVENTS_DELETED, (ArrayList<EditableEvent>) listAdapter.deleted);
        instanceState.putLong(RAW_CONTACT_ID, listAdapter.rawContactId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(EVENTS_LIST)) {
            EditAdapter adapter = new EditAdapter(this,
                    (ArrayList) savedInstanceState.getParcelableArrayList(EVENTS_LIST),
                    (ArrayList) savedInstanceState.getParcelableArrayList(EVENTS_BASE),
                    (ArrayList) savedInstanceState.getParcelableArrayList(EVENTS_DELETED),
                    savedInstanceState.getLong(RAW_CONTACT_ID));
            listAdapter = adapter;
            ListView listView = (ListView) findViewById(R.id.list);
            Log.d("Birthday", "Adapter: " + listView.getAdapter());
            listView.setAdapter(listAdapter);
        }
    }

    private void onRawContactIdSelected(long rawContactId) {
        ListView listView = (ListView) findViewById(R.id.list);
        List<EditableEvent> listOfEvents = service.getEvents(rawContactId);

        listAdapter = new EditAdapter(this, listOfEvents, rawContactId);
        listView.setAdapter(listAdapter);
        ImageView b = (ImageView) findViewById(R.id.edit_button_add);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                listAdapter.addRow();
                //disable save button
                if (saveButton != null) {
                    saveButton.setEnabled(false);
                }
            }
        });

        final Context context = this;
        saveButton = (Button) findViewById(R.id.btn_done);
        Button cancelButton = (Button) findViewById(R.id.btn_discard);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), Birthday.class);
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showDialog(DIALOG_SAVING);
                    service.performUpdate(listAdapter.buildDiff());
                    //Reload widget...
                    Utils.startWidgetUpdateAlarm(getBaseContext());
                    removeDialog(DIALOG_SAVING);
                    Intent i = new Intent(getBaseContext(), Birthday.class);
                    startActivity(i);
                } catch (RemoteException e) {
                    Log.i("Birthday", "Save failed!", e);
                    showDialog(DIALOG_SAVE_FAILED);
                } catch (OperationApplicationException e) {
                    Log.i("Birthday", "Save failed!", e);
                    showDialog(DIALOG_SAVE_FAILED);
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
             case (DIALOG_EDIT_DATE): {
                return createEditDialog(this);
            }
             case (DIALOG_SAVE_FAILED): {
                 return new AlertDialog.Builder(this).
                         setTitle(R.string.error).
                         setMessage(R.string.save_error).
                         setPositiveButton(R.string.ok, null).
                         create();
             } case (DIALOG_SAVING): {
                return new ProgressDialog.Builder(this).
                        setTitle(R.string.save).
                        setMessage(R.string.wait_dialog)
                        .create();
            } case (DIALOG_CHOOSE_ACCOUNT): {
                accounts = accountMap.keySet().toArray(new Account[accountMap.keySet().size()]);
                return new AlertDialog.Builder(this).
                        setTitle(R.string.choose_account).
                        setAdapter(new AccountAdapter<Account>(this, R.layout.simple_list_item_2, accounts), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Birthday", "Selected " + accounts[i] + " account");
                                onRawContactIdSelected(accountMap.get(accounts[i]));
                            }
                        }).create();
            }
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case (DIALOG_EDIT_DATE): {
                DatePickerDialog datePicker = (DatePickerDialog) dialog;
                if (eventToEdit != null && eventToEdit.eventDate != null) {
                    datePicker.updateDate(eventToEdit.eventDate.getYear(),
                            eventToEdit.eventDate.getMonthOfYear() - 1,
                            eventToEdit.eventDate.getDayOfMonth());
                } else {
                    datePicker.updateDate(2011, 0, 1);
                }
                break;
            }
            case (DIALOG_CHOOSE_ACCOUNT): {
                ((AlertDialog)dialog).getListView().setAdapter(new AccountAdapter<Account>(this, android.R.layout.two_line_list_item, accounts));
                break;
            }
        }
    }

     private Dialog createEditDialog(final Context ctx) {

        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                eventToEdit.eventDate = new LocalDate(year, month + 1, day);
                eventToEdit.integrity = DateIntegrity.FULL;
                listAdapter.notifyDataSetChanged();
                //enable save button
                if (saveButton != null) {
                    saveButton.setEnabled(true);
                }

            }
        }, 2011, 0, 1);
        try {
	        if (eventToEdit != null && eventToEdit.eventDate != null) {
	            datePicker.updateDate(eventToEdit.eventDate.getYear(),
	                    eventToEdit.eventDate.getMonthOfYear() - 1,
	                    eventToEdit.eventDate.getDayOfMonth());
	        }
        } catch (IllegalArgumentException e) {
        	//tried to set some weird date - ignore
        }

        return datePicker;
    }

    static class SpinnerItem {
        String label;
        EventType eventType;

        SpinnerItem(EventType eventType, String label) {
            this.eventType = eventType;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpinnerItem that = (SpinnerItem) o;

            if (eventType != that.eventType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return eventType.hashCode();
        }
    }



    class EditAdapter extends BaseAdapter {
        private List<EditableEvent> list;
        private Context ctx;
        private List<EditableEvent> base;
        private List<EditableEvent> deleted;
        private Long rawContactId;

        public EditAdapter(Context ctx, List<EditableEvent> list, Long rawContactId) {
            this.ctx = ctx;
            this.list = list;
            this.deleted = new ArrayList<EditableEvent>(1);
            this.base = new ArrayList<EditableEvent>();
            for (EditableEvent e: list) {
                this.base.add(e.clone());
            }
            this.rawContactId = rawContactId;
        }

        private EditAdapter(Context ctx, List<EditableEvent> list, List<EditableEvent> base, List<EditableEvent> deleted, Long rawContactId) {
            this.ctx = ctx;
            this.list = list;
            this.base = base;
            this.deleted = deleted;
            this.rawContactId = rawContactId;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).eventId;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View v;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.edit_row, null);
			} else {
				v = convertView;
			}
            EditableEvent evt = list.get(i);
            fillEditRow(v, evt);

            return v;
        }

        private void fillEditRow(View v, final EditableEvent evt) {
            if (evt.eventDate != null) {
                ((Button)v.findViewById(R.id.edit_display_date)).setText(DateFormatter.getInstance(ctx).
                    formatEdit(evt.eventDate));
            } else {
                ((Button)v.findViewById(R.id.edit_display_date)).setText("Click to set");
            }

            Spinner s = (Spinner)v.findViewById(R.id.edit_type_button);
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(ctx,
                    android.R.layout.simple_spinner_item, SPINNER_ITEMS);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);
            s.setSelection(adapter.getPosition(getSpinnerType(evt.type)));
            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SpinnerItem si = (SpinnerItem) adapterView.getItemAtPosition(i);
                    Log.d("Birthday", "Selected item: " + si);
                    evt.type = si.eventType;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //do nothing
                }
            });



            v.findViewById(R.id.edit_remove_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(evt);
                    deleted.add(evt);
                    notifyDataSetChanged();
                }
            });

            v.findViewById(R.id.edit_display_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventToEdit = evt;
                    showDialog(DIALOG_EDIT_DATE);
                }
            });

        }

        public void addRow() {
            this.list.add(new EditableEvent(rawContactId));
            this.notifyDataSetChanged();
        }

        /**
         * Returns diff to update to database.
         * @return
         */
        public ArrayList<ContentProviderOperation> buildDiff() {
            ArrayList<ContentProviderOperation> diff = new ArrayList<ContentProviderOperation>();
            for (EditableEvent e: list) {

                //update
                if (containsId(base, e.eventId)) {
                    diff.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).
                        withSelection(ContactsContract.Data._ID + " = ?", new String[] {String.valueOf(e.eventId)}).
                        withValue(ContactsContract.CommonDataKinds.Event.START_DATE, e.eventDate.toString()).
                        withValue(ContactsContract.CommonDataKinds.Event.TYPE, e.type.getCode()).
                        build());
                } else {
                    diff.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                            withValue(ContactsContract.Data.RAW_CONTACT_ID, String.valueOf(e.rawContactId)).
                            withValue(ContactsContract.CommonDataKinds.Event.START_DATE, e.eventDate.toString()).
                            withValue(ContactsContract.CommonDataKinds.Event.TYPE, e.type == null ? EventType.BIRTHDAY.getCode() : e.type.getCode()).
                            withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE).
                            build());
                }

            }
            for (EditableEvent e: deleted) {
                diff.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).
                        withSelection(ContactsContract.Data._ID + " = ?", new String[] {String.valueOf(e.eventId)}).
                        build());
            }

            return diff;
        }

        private boolean containsId(List<EditableEvent> l, Long id) {
            for (EditableEvent e: l) {
                if (e.eventId.equals(id)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static SpinnerItem getSpinnerType(EventType eventType) {
        for (SpinnerItem i: SPINNER_ITEMS) {
            if (i.eventType.equals(eventType)) {
                return i;
            }
        }  return null;
    }

    static class AccountAdapter<T> extends ArrayAdapter<T> {
        public AccountAdapter(Context context, int textViewResourceId, T[] objects) {
            super(context, textViewResourceId, objects);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Account account = (Account) getItem(position);
            View v;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.simple_list_item_2, null);
			} else {
				v = convertView;
			}

            ((TextView) v.findViewById(android.R.id.text1)).setText(account.name);
            ((TextView) v.findViewById(android.R.id.text2)).setText(account.type);

            return v;
        }
    }
}
