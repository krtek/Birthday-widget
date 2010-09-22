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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private static final int DEBUG_MENU = 0;
	private static final int PREFS_MENU = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AdManager.setTestDevices( new String[] { "F57D9A6828124CD742993F5653A6AC3C" } );

    }
        
    
    
	@Override
	protected void onResume() {		
		super.onResume();
		 ListView list = (ListView) findViewById(R.id.list1);
		 int monthsAhead = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("show_ahead.interval", "12"));
	        List<BContact> listOfContacts = BirthdayProvider.upcomingBirthday(
	        		this, monthsAhead);       
	        list.setAdapter(new BirthdayAdapter(listOfContacts, this));    
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, DEBUG_MENU, 0, R.string.debug_menu).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, PREFS_MENU, 1, R.string.preferences_menu).setIcon(android.R.drawable.ic_menu_preferences);		
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
		}
		return false;
		
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
			((TextView)v.findViewById(R.id.date)).setText(MessageFormat.format(ctx.getString(R.string.date_of_birthday), contact.getbDay()));
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
