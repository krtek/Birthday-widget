package cz.krtinec.birthday.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import cz.krtinec.birthday.Birthday;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;

public abstract class UpdateService extends Service {
	protected List<BContact> list;
	private int NOTIFY_CODE = 1;
	private int WIDGET_CODE = 0;

	public abstract RemoteViews updateViews();
	public abstract ComponentName getComponentName();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("UpdateService", "Service started...");
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		list = BirthdayProvider.getInstance().upcomingBirthday(this);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("notifications.enabled", true)) {
			alertOnBirthday();
		}
		RemoteViews views = updateViews();
		Intent i = new Intent(getApplicationContext(), Birthday.class);
		views.setOnClickPendingIntent(R.id.layout, PendingIntent.getActivity(this, WIDGET_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT));
		list = null;
		manager.updateAppWidget(getComponentName(), views);		
		stopSelf();
		Log.d("UpdateService", "Service finished...");
	}
	

	protected void replaceIconWithPhoto(RemoteViews views, BContact contact, int viewId) {
		InputStream is = BirthdayProvider.openPhoto(this, contact.getId());
		if (is != null) {
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			views.setImageViewBitmap(viewId, bitmap);
			try {
				is.close();
			} catch (IOException e) {
			}
		} else {
			views.setImageViewResource(viewId, R.drawable.icon);
		}
	}
	
	private void alertOnBirthday() {		
		for (BContact c:list) {
			if (hasBirthdayToday(c)) {
				//should fire alarm
				Calendar now = Calendar.getInstance();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
				int hourToAlert = Integer.valueOf(prefs.getString("notifications.time", "8"));
				if (isTimeToNotify(now, hourToAlert)) {
                    fireBirthdayAlert(c, now.getTimeInMillis());
				}
			}
		}
	}


    static boolean hasBirthdayToday(BContact c) {
        return c.getDaysToBirthday() == 0;
    }

    static boolean isTimeToNotify(Calendar now, int hourToAlert) {
        return now.get(Calendar.HOUR_OF_DAY) == hourToAlert;
    }


    private void fireBirthdayAlert(BContact c, Long when) {
        String notificationFormat = this.getString(R.string.notification_pattern);
        Notification n = new Notification(R.drawable.icon, getString(R.string.notification_alert), when);
        n.flags = n.flags | Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(getApplicationContext(), Birthday.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFY_CODE, i, PendingIntent.FLAG_CANCEL_CURRENT);

        n.setLatestEventInfo(this, getString(R.string.notification_alert),
                MessageFormat.format(notificationFormat, c.getDisplayName()), pendingIntent);
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify("Birthday", (int)c.getId(), n);
    }
}

