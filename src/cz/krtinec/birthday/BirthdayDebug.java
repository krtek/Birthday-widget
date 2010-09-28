package cz.krtinec.birthday;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import com.admob.android.ads.AdManager;

import cz.krtinec.birthday.Birthday.BirthdayAdapter;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;
import cz.krtinec.birthday.dto.BContactDebug;
import cz.krtinec.birthday.dto.DateIntegrity;
import cz.krtinec.birthday.ui.AdapterParent;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BirthdayDebug extends Activity {
	   /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);
    }
    
	@Override
	protected void onResume() {		
		super.onResume();
		 ListView list = (ListView) findViewById(R.id.debug_list);		 
	        List<BContactDebug> listOfContacts = BirthdayProvider.getInstance().allBirthday(this);
	        list.setAdapter(new BirthdayDebugAdapter(listOfContacts, this));    
	}
	
    static class BirthdayDebugAdapter extends AdapterParent<BContactDebug> {
    	public BirthdayDebugAdapter(List<BContactDebug> list, Context ctx) {
    		super(list, ctx);
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BContactDebug contact = list.get(position);
			View v;
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.debug_row, null);			
			} else {
				v = convertView;
			}
			
			((TextView)v.findViewById(R.id.name)).setText(contact.getDisplayName());				
			((TextView)v.findViewById(R.id.dateParsed)).setText(MessageFormat.format(ctx.getString(R.string.debug_parsed), contact.getDisplayDate(ctx)));
			
			((TextView)v.findViewById(R.id.dateAsString)).setText(MessageFormat.format(ctx.getString(R.string.debug_raw), contact.getbDayString()));				
			ImageView check = (ImageView)v.findViewById(R.id.check);
			
			switch (contact.getIntegrity()) {
				case FULL: {
					check.setImageResource(R.drawable.accept);
					break;
				}
				case WITHOUT_YEAR: {
					check.setImageResource(R.drawable.exclamation);
					break;					
				}
				case NONE: {
					check.setImageResource(R.drawable.cancel);
					break;										
				}
			}
			
			
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
