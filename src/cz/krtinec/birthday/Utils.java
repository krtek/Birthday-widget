package cz.krtinec.birthday;

import java.text.MessageFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {

	public static String getCongrats(Context ctx, String name) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String template = prefs.getString(Birthday.TEMPLATE_KEY, ctx.getString(R.string.congrats_pattern));
		return MessageFormat.format(template, name);
	}
	

}
