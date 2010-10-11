package cz.krtinec.birthday;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;

import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;
import cz.krtinec.birthday.ui.PhotoLoader;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class BirthdayWidget extends AppWidgetProvider {	
	 public void onUpdate(Context context, AppWidgetManager appWidgetManager,
		        int[] appWidgetIds) {
	        // To prevent any ANR timeouts, we perform the update in a service
	        context.startService(new Intent(context, UpdateService.class));
	 }
	 
	 public static class UpdateService extends Service {		 
			@Override
			public IBinder onBind(Intent intent) {
				// TODO Auto-generated method stub
				return null;		
			}
			
			@Override
			public void onStart(Intent intent, int startId) {
	            ComponentName thisWidget = new ComponentName(this, BirthdayWidget.class);
	            AppWidgetManager manager = AppWidgetManager.getInstance(this);
	            
	            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget);
	            updateViews(views);
	            Intent i = new Intent(getApplicationContext(), Birthday.class);
	            views.setOnClickPendingIntent(R.id.layout, PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT));
	            manager.updateAppWidget(thisWidget, views);

			}

			private void updateViews(RemoteViews views) {
				List<BContact> list = BirthdayProvider.getInstance().upcomingBirthday(this);
				if (list.size() > 0) {
					BContact contact = list.get(0);
					views.setTextViewText(R.id.first_name, contact.getDisplayName());
					views.setTextViewText(R.id.first_date, contact.getDisplayDate(this));	
					replaceIconWithPhoto(views, contact, R.id.first_icon);					
				} else {
					views.setTextViewText(R.id.first_name, getText(R.string.no_name_found));				
				}
				if (list.size() > 1) {
					BContact contact = list.get(1);
					views.setTextViewText(R.id.second_name, contact.getDisplayName());
					views.setTextViewText(R.id.second_date, contact.getDisplayDate(this));
					replaceIconWithPhoto(views, contact, R.id.second_icon);
				} else {
					views.setTextViewText(R.id.second_name, getText(R.string.no_name_found));
				}
			}

			private void replaceIconWithPhoto(RemoteViews views, BContact contact, int viewId) {
				InputStream is = BirthdayProvider.openPhoto(this, contact.getId());
				if (is != null) {
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					views.setImageViewBitmap(viewId, bitmap);
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	 }
	
}
