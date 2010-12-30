package cz.krtinec.birthday;

import java.text.MessageFormat;
import java.util.List;

import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.EventDebug;
import cz.krtinec.birthday.ui.AdapterParent;
import cz.krtinec.birthday.ui.PhotoLoader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BirthdayDebug extends Activity {
	private PhotoLoader loader;
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
			loader = new PhotoLoader(new Handler(), this);
			Thread thread = new Thread(loader);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
	        List<EventDebug> listOfContacts = BirthdayProvider.getInstance().allBirthday(this);
	        list.setAdapter(new BirthdayDebugAdapter(listOfContacts, this, loader));    
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		loader.shutdown();
//		Debug.stopMethodTracing();		
	}
	
    static class BirthdayDebugAdapter extends AdapterParent<EventDebug> {
    	public BirthdayDebugAdapter(List<EventDebug> list, Context ctx, PhotoLoader loader) {
    		super(list, ctx, loader);
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			EventDebug contact = list.get(position);
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
			final ImageView imageView = (ImageView)v.findViewById(R.id.bicon);
			imageView.setImageResource(R.drawable.icon);			
			loader.addPhotoToLoad((ImageView)v.findViewById(R.id.bicon), contact.getId());
			

			
			return v;

		}    	
    }

}
