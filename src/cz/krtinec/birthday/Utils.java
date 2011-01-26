/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */

package cz.krtinec.birthday;

import java.text.MessageFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cz.krtinec.birthday.dto.Event;

public class Utils {

	public static String getCongrats(Event event, Context ctx, String name) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String template = prefs.getString(Birthday.TEMPLATE_KEY, ctx.getString(R.string.congrats_pattern));
		return MessageFormat.format(template, name);
	}
	

}
