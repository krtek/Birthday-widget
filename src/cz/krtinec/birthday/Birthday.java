package cz.krtinec.birthday;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import com.admob.android.ads.AdManager;

import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;
import cz.krtinec.birthday.ui.AdapterParent;
import cz.krtinec.birthday.ui.BirthdayPreference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Birthday extends Activity {
	private static final String VERSION_TEXT = "version";
	private static final int DEBUG_MENU = 0;
	private static final int PREFS_MENU = 1;
	private static final int HELP_MENU = 2;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AdManager.setTestDevices( new String[] { "F57D9A6828124CD742993F5653A6AC3C", AdManager.TEST_EMULATOR} );
                
        try {
			int version = this.getPackageManager().getPackageInfo("cz.krtinec.birthday", 0).versionCode;
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (version != prefs.getInt(VERSION_TEXT, 1)) {
				AlertDialog dialog = createHelpDialog(this);
				dialog.show();
				Editor editor = prefs.edit();
				editor.putInt(VERSION_TEXT, version);
				editor.commit();				
			}
		} catch (NameNotFoundException e) {
			//obviosly this is always installed
		}
    }
        
    
    
	@Override
	protected void onResume() {		
		super.onResume();
		 ListView list = (ListView) findViewById(R.id.list1);		 
	        List<BContact> listOfContacts = BirthdayProvider.getInstance().upcomingBirthday(this);       
	        list.setAdapter(new BirthdayAdapter(listOfContacts, this));    
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, DEBUG_MENU, 0, R.string.debug_menu).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, PREFS_MENU, 1, R.string.preferences_menu).setIcon(android.R.drawable.ic_menu_preferences);		
		menu.add(Menu.NONE, HELP_MENU, 2, R.string.help_menu).setIcon(android.R.drawable.ic_menu_help);
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
				AlertDialog dialog = createHelpDialog(this);
				dialog.show();
			}
		}
		return false;
		
	}
	
	public static AlertDialog createHelpDialog(Context context) {
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
	
    static class BirthdayAdapter extends AdapterParent<BContact> {
    	
    	public BirthdayAdapter(List<BContact> list, Context ctx) {
    		super(list,ctx);
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BContact contact = list.get(position);
			View v;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);			
			} else {
				v = convertView;
			}
			
			((TextView)v.findViewById(R.id.name)).setText(contact.getDisplayName());
			((TextView)v.findViewById(R.id.age)).setText(String.valueOf((contact.getAge() == null) ? "--" : contact.getAge()));
			((TextView)v.findViewById(R.id.date)).setText(contact.getDisplayDate(ctx));
			((TextView)v.findViewById(R.id.days)).setText(String.valueOf(contact.getDaysToBirthday()));
			
			//set photo
			InputStream photoStream;
			if (contact.getPhotoId() != null && 
					(photoStream = BirthdayProvider.openPhoto(ctx, contact.getId())) != null) {
				Drawable d = Drawable.createFromStream(photoStream, "src");
				((ImageView)v.findViewById(R.id.bicon)).setImageDrawable(d);
			} else {
				((ImageView)v.findViewById(R.id.bicon)).setImageResource(R.drawable.icon);
			}
			
			return v;

		} 		
    }
}
