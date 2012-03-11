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

import cz.krtinec.birthday.dto.BirthdayEvent;
import cz.krtinec.birthday.dto.Event;
import cz.krtinec.birthday.dto.DateIntegrity;
import cz.krtinec.birthday.dto.Zodiac;
import junit.framework.TestCase;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class BirthdayWidgetTest extends TestCase {

    public void testHasBirthdayToday() {
        LocalDate birthDate = new LocalDate();
        birthDate = birthDate.minusYears(30);
        Event contact = new BirthdayEvent("Lukas Marek", 123L, birthDate, "lookupKey", DateIntegrity.FULL, 123L, Zodiac.toZodiac(birthDate));

        assertTrue("Birthday is today", NotificationSender.hasBirthdayToday(contact));

        birthDate = birthDate.minusDays(1);
        contact = new BirthdayEvent("Lukas Marek", 123L, birthDate, "lookupKey", DateIntegrity.FULL, 123L, Zodiac.toZodiac(birthDate));

        assertFalse("Birthday was yesterday", NotificationSender.hasBirthdayToday(contact));

        birthDate = birthDate.plusDays(2);
        contact = new BirthdayEvent("Lukas Marek", 123L, birthDate, "lookupKey",  DateIntegrity.FULL, 123L, Zodiac.toZodiac(birthDate));

        assertFalse("Birthday is tommorow", NotificationSender.hasBirthdayToday(contact));
    }
}
