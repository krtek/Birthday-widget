package cz.krtinec.birthday;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;
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
	private static final int PREFS_MENU = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
        
    
    
	@Override
	protected void onResume() {		
		super.onResume();
		 ListView list = (ListView) findViewById(R.id.list1);
		 int monthsAhead = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("show_ahead.interval", "1"));
	        List<BContact> listOfContacts = BirthdayProvider.upcomingBirthday(
	        		this, monthsAhead);       
	        list.setAdapter(new BirthdayAdapter(listOfContacts, this));    
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(Menu.NONE, RELOAD_MENU, 0, R.string.reload).setIcon(R.drawable.ic_menu_refresh);
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
		}}
		return false;
		
	}
	
    static class BirthdayAdapter extends BaseAdapter {
    	private List<BContact> list;
    	private Context ctx;
    	
    	public BirthdayAdapter(List<BContact> list, Context ctx) {
    		this.list = list;
    		this.ctx = ctx;
    	}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return list.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BContact contact = list.get(position);
			View v;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row3, null);			
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
