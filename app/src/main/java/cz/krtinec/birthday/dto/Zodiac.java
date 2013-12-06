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
 * Copyright (c) Lukas Marek, 2012.
 */

package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

import android.graphics.drawable.Drawable;
import cz.krtinec.birthday.R;

/**
 * Enum which holds the zodiac signs.
 */
public enum Zodiac {

    ARIES(newDate(3, 21), newDate(4,20), R.drawable.zodiac_aries),
    TAURUS(newDate(4,21), newDate(5,21), R.drawable.zodiac_taurus),
    GEMINI(newDate(5,22), newDate(6,21), R.drawable.zodiac_gemini),
    CANCER(newDate(6,22), newDate(7,22), R.drawable.zodiac_cancer),
    LEO(newDate(7,23), newDate(8,22), R.drawable.zodiac_leo),
    VIRGO(newDate(8,23), newDate(9,23), R.drawable.zodiac_virgo),
    LIBRA(newDate(9,24), newDate(10,23), R.drawable.zodiac_libra),
    SCORPIO(newDate(10,24), newDate(11,22), R.drawable.zodiac_scorpio),
    SAGITTARIUS(newDate(11,23), newDate(12,21), R.drawable.zodiac_sagittarius),
    CAPRICORN(newDate(12,22), newDate(1,20), R.drawable.zodiac_capricorn),
    AQUARIUS(newDate(1,21), newDate(2,19), R.drawable.zodiac_aquarius),
    PISCES(newDate(2,20), newDate(3,20), R.drawable.zodiac_pisces);



    private LocalDate from;
    private LocalDate to;
    private int iconId;
    
    private Zodiac(LocalDate from, LocalDate to, int iconId) {
        this.from = from;
        this.to = to;
        this.iconId = iconId;
    }
    
    public int getIconId() {
        return iconId;
    }

    private static LocalDate newDate(int monthOfYear, int dayOfMonth) {
        return new LocalDate().withMonthOfYear(monthOfYear).withDayOfMonth(dayOfMonth);
    }
    
    public static Zodiac toZodiac(LocalDate birthday) {
        for (Zodiac zodiac: values()) {
            LocalDate fromWithYear = zodiac.from.withYear(birthday.getYear());
            LocalDate toWithYear = zodiac.to.withYear(birthday.getYear());

            if (birthday.getMonthOfYear() == 12 && Zodiac.CAPRICORN.equals(zodiac)) {
                toWithYear = toWithYear.plusYears(1);
            } else if (birthday.getMonthOfYear() == 1 && Zodiac.CAPRICORN.equals(zodiac)) {
                fromWithYear = fromWithYear.minusYears(1);
            }

            if ((fromWithYear.isBefore(birthday) || fromWithYear.isEqual(birthday))
                    && (toWithYear.isAfter(birthday) || toWithYear.isEqual(birthday))) {
                return zodiac;
            }
        }
        
        throw new IllegalArgumentException("Cannot find zodiac sign for date: " + birthday);
    }
}
