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
import cz.krtinec.birthday.dto.*;

public class Utils {

	public static String getCongrats(Context ctx, Event event) {
        String template = getTemplate(ctx, event);
		return MessageFormat.format(template, event.getDisplayName() , getEventLabel(ctx, event));
	}


    private static String getTemplate(Context ctx, Event event) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (event instanceof BirthdayEvent) {
            return prefs.getString(Birthday.TEMPLATE_KEY, ctx.getString(R.string.congrats_pattern));
        } else if (event instanceof AnniversaryEvent) {
            return prefs.getString(Birthday.TEMPLATE_KEY_ANNIVERSARY, ctx.getString(R.string.congrats_pattern));
        } else if (event instanceof OtherEvent) {
            //TODO Solve other events ?
            return prefs.getString(Birthday.TEMPLATE_KEY_OTHER, ctx.getString(R.string.congrats_pattern));
        } else {
            return prefs.getString(Birthday.TEMPLATE_KEY_CUSTOM, ctx.getString(R.string.congrats_pattern));
        }
    }

    public static String getEventLabel(Context ctx, Event event) {
        if (event instanceof BirthdayEvent) {
            return ctx.getString(R.string.birthday);
        } else if (event instanceof AnniversaryEvent) {
            return ctx.getString(R.string.anniversary);
        } else if (event instanceof CustomEvent) {
            return ((CustomEvent) event).getLabel();
        } else {
            //TODO Solve other events ?
            return ctx.getString(R.string.birthday);
        }
    }

    public static String getEventLabel(Context ctx, EditableEvent evt) {
        switch (evt.getType()) {
            case BIRTHDAY: {
                return ctx.getString(R.string.birthday);
            }
            case ANNIVERSARY: {
                return ctx.getString(R.string.anniversary);
            }
            case CUSTOM: {
                return evt.getLabel();
            }
            case OTHER: {
                return ctx.getString(R.string.other);
            }
        }
        return ctx.getString(R.string.birthday);
    }
}
