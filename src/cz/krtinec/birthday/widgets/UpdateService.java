package cz.krtinec.birthday.widgets;

import java.io.IOException;
import java.io.InputStream;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.widget.RemoteViews;
import cz.krtinec.birthday.Birthday;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;

public abstract class UpdateService extends Service {		 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;		
	}

	public abstract RemoteViews updateViews();
	public abstract ComponentName getComponentName();
	
	@Override
	public void onStart(Intent intent, int startId) {
		
		AppWidgetManager manager = AppWidgetManager.getInstance(this);

		RemoteViews views = updateViews();
		Intent i = new Intent(getApplicationContext(), Birthday.class);
		views.setOnClickPendingIntent(R.id.layout, PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT));
		manager.updateAppWidget(getComponentName(), views);
	}
	
	
	protected void replaceIconWithPhoto(RemoteViews views, BContact contact, int viewId) {
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
		} else {
			views.setImageViewResource(viewId, R.drawable.icon);
		}
	}
}

