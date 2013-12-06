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

import junit.framework.TestCase;

public class ZodiacTest extends TestCase {

    public void testToZodiac() {
        assertEquals(Zodiac.SAGITTARIUS, Zodiac.toZodiac(new LocalDate(1978, 12, 21)));
        assertEquals(Zodiac.CAPRICORN, Zodiac.toZodiac(new LocalDate(1977, 12, 25)));
        assertEquals(Zodiac.CAPRICORN, Zodiac.toZodiac(new LocalDate(1980, 1, 1)));

        assertEquals(Zodiac.SAGITTARIUS, Zodiac.toZodiac(new LocalDate().withMonthOfYear(12).withDayOfMonth(21)));
        assertEquals(Zodiac.CAPRICORN, Zodiac.toZodiac(new LocalDate().withMonthOfYear(12).withDayOfMonth(25)));


        assertEquals(Zodiac.VIRGO, Zodiac.toZodiac(new LocalDate(1974, 8, 23)));
        assertEquals(Zodiac.VIRGO, Zodiac.toZodiac(new LocalDate(1974, 9, 23)));
        assertEquals(Zodiac.LIBRA, Zodiac.toZodiac(new LocalDate(1974, 9, 24)));

    }
}
