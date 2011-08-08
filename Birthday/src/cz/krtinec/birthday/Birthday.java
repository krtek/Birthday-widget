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

package cz.krtinec.birthday;

import java.util.List;

import android.app.*;
import android.provider.ContactsContract;
import android.text.AndroidCharacter;
import android.util.Log;
import android.widget.*;

import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.*;
import cz.krtinec.birthday.ui.AdapterParent;
import cz.krtinec.birthday.ui.BirthdayPreference;
import cz.krtinec.birthday.ui.EditActivity;
import cz.krtinec.birthday.ui.PhotoLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Birthday extends Activity {
    private static final String VERSION_KEY = "version";
    public static final String TEMPLATE_KEY = "template";
    public static final String TEMPLATE_KEY_ANNIVERSARY = "template.anniversary";
    public static final String TEMPLATE_KEY_CUSTOM = "template.custom";
    public static final String TEMPLATE_KEY_OTHER = "template.other";

    private static final int DEBUG_MENU = 0;
    private static final int PREFS_MENU = 1;
    private static final int HELP_MENU = 2;
    private static final int ADD_BDAY_MENU = 3;

    private static final int ACTIVITY_PICK_CONTACT = 10;
    private static final int DIALOG_HELP = 11;

    private static final int DIALOG_LOADING = 13;
    private static final int DIALOG_EMPTY = 14;
    private Handler handler = new Handler();
    private PhotoLoader loader;


    private Uri contactToEdit;


    private ProgressDialog dialog;
    private List<Event> listOfContacts;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            int version = this.getPackageManager().getPackageInfo("cz.krtinec.birthday", 0).versionCode;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (version != prefs.getInt(VERSION_KEY, 1)) {
                //showDialog(DIALOG_HELP);
                Utils.setOrCancelNotificationsAlarm(this, prefs);
                Editor editor = prefs.edit();
                editor.putInt(VERSION_KEY, version);
                editor.commit();
            }
        } catch (NameNotFoundException e) {
            //obviosly this is always installed
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Birthday", "onStart() called.");
		loader = new PhotoLoader(handler, this);
		Thread thread = new Thread(loader);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();

        /*
           * To enable tracing, android.permission.WRITE_EXTERNAL_STORAGE must be set to true!
           */
//		Debug.startMethodTracing("birthday");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Birthday", "onPause() called.");

    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.shutdown();
        Log.d("Birthday", "onStop() called.");
//		Debug.stopMethodTracing();		
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Birthday", "onResume() called.");
        showDialog(DIALOG_LOADING);
        new Thread(new StartupThread(this)).start();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.context_menu_header);

        Event item = listOfContacts.get(((AdapterContextMenuInfo) menuInfo).position);
        MenuItem callItem = menu.add(R.string.context_menu_call).setEnabled(false);
        MenuItem smstItem = menu.add(R.string.context_menu_text).setEnabled(false);
        MenuItem emailItem = menu.add(R.string.context_menu_email).setEnabled(false);
        String phone = BirthdayProvider.getPhoneNumber(this, item.getId());
        if (phone != null) {
            smstItem.setEnabled(true);
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
            smsIntent.putExtra("sms_body", Utils.getCongrats(this, item));
            smstItem.setIntent(smsIntent);
            callItem.setEnabled(true);
            Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone));
            callItem.setIntent(callIntent);
        }
        String email = BirthdayProvider.getEmail(this, item.getId());
        if (email != null) {
            emailItem.setEnabled(true);
            String subject = getString(R.string.congrats_subject);
            String body = Utils.getCongrats(this, item);
            Intent mailer = new Intent(Intent.ACTION_SEND);
            mailer.setType("text/plain");
            mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            mailer.putExtra(Intent.EXTRA_SUBJECT, subject);
            mailer.putExtra(Intent.EXTRA_TEXT, body);
            emailItem.setIntent(mailer);
        }
    }

    private static void launchNewShareIntent(Context c, String subject, String text, String dialogTitle, Uri recipient) {
        Intent shareintent = new Intent(Intent.ACTION_SENDTO);
        shareintent.setData(recipient);
        shareintent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareintent.putExtra(Intent.EXTRA_TEXT, text);
        shareintent.setType("text/plain");
        shareintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(Intent.createChooser(shareintent, dialogTitle));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, DEBUG_MENU, 3, R.string.debug_menu).setIcon(android.R.drawable.ic_menu_edit);
        menu.add(Menu.NONE, PREFS_MENU, 1, R.string.preferences_menu).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, HELP_MENU, 3, R.string.help_menu).setIcon(android.R.drawable.ic_menu_help);
        menu.add(Menu.NONE, ADD_BDAY_MENU, 0, R.string.add_bday_menu).setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case PREFS_MENU: {
                Intent settingsActivity = new Intent(getBaseContext(), BirthdayPreference.class);
                startActivity(settingsActivity);
                return true;
            }
            case DEBUG_MENU: {
                Intent debugIntent = new Intent(getBaseContext(), BirthdayDebug.class);
                startActivity(debugIntent);
                return true;
            }
            case HELP_MENU: {
                showDialog(DIALOG_HELP);
                return true;
            }
            case ADD_BDAY_MENU: {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, ACTIVITY_PICK_CONTACT);
                return true;
            }
        }
        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ACTIVITY_PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    contactToEdit = data.getData();
                    Intent intent = new Intent(getBaseContext(), EditActivity.class);
                    intent.setData(contactToEdit);
                    startActivity(intent);
                    return;
                }
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case (DIALOG_HELP): {
                return createHelpDialog(this);
            }
            case (DIALOG_LOADING): {
                if (this.dialog != null) {
                    //cancel current dialog
                    this.dialog.dismiss();
                }
                this.dialog = ProgressDialog.show(this, getString(R.string.app_name),
                        getString(R.string.loading), true);
                return this.dialog;
            }
            case (DIALOG_EMPTY): {
                return new AlertDialog.Builder(this)
                        .setTitle(R.string.help_title)
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(R.string.ok, null)
                        .setMessage(R.string.no_data_found)
                        .create();
            }

        }
        return null;
    }


    private static AlertDialog createHelpDialog(Context context) {
        final TextView message = new TextView(context);
        // i.e.: R.string.dialog_message =>
        // "Test this dialog following the link to dtmilano.blogspot.com"
        final SpannableString s =
                new SpannableString(context.getText(R.string.help_string));
        Linkify.addLinks(s, Linkify.ALL);
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        return new AlertDialog.Builder(context)
                .setTitle(R.string.help_title)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.ok, null)
                .setView(message)
                .create();
    }


    static class BirthdayAdapter extends AdapterParent<Event> implements AbsListView.OnScrollListener {

        public BirthdayAdapter(List<Event> list, Context ctx, PhotoLoader loader) {
            super(list, ctx, loader);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = list.get(position);
            View v;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            } else {
                v = convertView;
            }
            ((TextView) v.findViewById(R.id.name)).setText(event.getDisplayName());
            ((TextView) v.findViewById(R.id.days)).setText(String.valueOf(event.getDaysToEvent()));
            ((ImageView) v.findViewById(R.id.bicon)).setImageResource(R.drawable.icon);
            loader.addPhotoToLoad((ImageView) v.findViewById(R.id.bicon), event.getId());
            if (event instanceof BirthdayEvent) {
                BirthdayEvent bEvent = (BirthdayEvent) event;
                ((TextView) v.findViewById(R.id.age)).setText(
                        String.valueOf((bEvent.getAge() == null) ? "--" : bEvent.getAge()));
                ((TextView) v.findViewById(R.id.date)).setText(event.getDisplayDate(ctx));
                ((TextView) v.findViewById(R.id.type)).setText(getCaption(R.string.birthday));
            } else if (event instanceof AnniversaryEvent) {
                AnniversaryEvent aEvent = (AnniversaryEvent) event;
                ((TextView) v.findViewById(R.id.age)).setText(
                        String.valueOf((aEvent.getYearsFrom() == null) ? "--" : aEvent.getYearsFrom()));
                ((TextView) v.findViewById(R.id.date)).setText(event.getDisplayDate(ctx));
                ((TextView) v.findViewById(R.id.type)).setText(getCaption(R.string.anniversary));
            } else if (event instanceof CustomEvent) {
                CustomEvent cEvent = (CustomEvent) event;
                ((TextView) v.findViewById(R.id.age)).setText("--");
                ((TextView) v.findViewById(R.id.date)).setText(event.getDisplayDate(ctx));
                ((TextView) v.findViewById(R.id.type)).setText(getCaption(cEvent.getLabel()));
            } else if (event instanceof OtherEvent) {
                ((TextView) v.findViewById(R.id.age)).setText("--");
                ((TextView) v.findViewById(R.id.date)).setText(event.getDisplayDate(ctx));
                ((TextView) v.findViewById(R.id.type)).setText(getCaption(R.string.other));
            }

            return v;

        }

        private String getCaption(int resId) {
            String desc = ctx.getString(resId);
            return getCaption(desc);
        }

        private String getCaption(String desc) {
            return desc + ": ";
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                loader.pause();
            } else {
                loader.resume();
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            //empty
        }
    }

    class StartupThread implements Runnable {
        private Activity activity;

        public StartupThread(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    ListView list = (ListView) activity.findViewById(android.R.id.list);
                    listOfContacts = BirthdayProvider.getInstance().upcomingBirthday(activity);
                    final BirthdayAdapter adapter = new BirthdayAdapter(listOfContacts, activity, loader);
                    list.setAdapter(adapter);
                    list.setEmptyView(activity.findViewById(android.R.id.empty));
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Event e = (Event) adapter.getItem(i);
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            Uri uri = Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI,
                                    String.valueOf(e.getRawContactId()));
                            intent.setData(uri);
                            intent.setClass(getBaseContext(), EditActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    registerForContextMenu((ListView) findViewById(android.R.id.list));
                    dialog.cancel();
                    if (listOfContacts.isEmpty()) {
                        showDialog(DIALOG_EMPTY);
                    }
                }
            });
        }

    }


}
