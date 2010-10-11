package cz.krtinec.birthday.widgets;

import java.util.List;

import android.content.ComponentName;
import android.widget.RemoteViews;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayProvider;
import cz.krtinec.birthday.dto.BContact;

public class UpdateService2x1 extends UpdateService {
	
	public ComponentName getComponentName() {
		return new ComponentName(this, BirthdayWidget2x1.class);
	}
	
	public RemoteViews updateViews() {
		RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget2x1);
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
		return views;
	}	

}
