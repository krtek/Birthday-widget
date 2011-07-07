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

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.krtinec.birthday.dto.DateIntegrity;

import android.content.Context;
import android.preference.PreferenceManager;

public class DateFormatter {
	private static DateFormatter instance = null;
    private static final String EMPTY_DATE = "--";
    private DateTimeFormatter longFormat;
	private DateTimeFormatter shortFormat;
    private Context ctx;
	
	private DateFormatter(Context ctx) {
        this.ctx = ctx;
		int fIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ctx).getString("date_format", "1"));
		longFormat = DateTimeFormat.forPattern(ctx.getResources().getStringArray(R.array.long_format_values)[fIndex]);
		shortFormat = DateTimeFormat.forPattern(ctx.getResources().getStringArray(R.array.short_format_values)[fIndex]); 
	}
	
	public static DateFormatter getInstance(Context ctx) {
		if (instance == null) {
			instance = new DateFormatter(ctx);
		}
		
		return instance;
	}
	
	public String format(LocalDate date, DateIntegrity integrity) {
		if (date == null) {
			return EMPTY_DATE;
		}
		
		DateTimeFormatter format = integrity == DateIntegrity.FULL ? 
			 longFormat : shortFormat;
			
		return format.print(date);
	}

    /**
     * Format date for edit button.
     * @param date
     * @return
     */
    public String formatEdit(LocalDate date) {
        if (date == null) {
            return EMPTY_DATE;
        }
        DateTimeFormatter pattern =
                DateTimeFormat.forPattern(ctx.getResources().getStringArray(R.array.long_format_values)[0]);

        return pattern.print(date);
    }
	
	public static void reset() {
		instance = null;
	}
}
