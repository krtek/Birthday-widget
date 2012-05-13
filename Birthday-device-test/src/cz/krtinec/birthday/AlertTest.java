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

import android.test.ActivityInstrumentationTestCase2;
import cz.krtinec.birthday.dto.*;
import org.joda.time.LocalDate;

/**
 * User: lukas.marek@gmail.com
 * Date: 01.08.11
 * Time: 18:11
 */
public class AlertTest extends ActivityInstrumentationTestCase2<Birthday> {
    public AlertTest() {
        super("cz.krtinec.birthday", Birthday.class);
    }

    public void testFormatText() {
        Birthday ctx = this.getActivity();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l, Zodiac.AQUARIUS);
        assertEquals("Hurry up, krtek has birthday!", NotificationSender.formatMessage(ctx, e));
        e = new AnniversaryEvent("krtek", 1l, new LocalDate(), "1", DateIntegrity.FULL, 1l);
        assertEquals("Hurry up, krtek has anniversary!", NotificationSender.formatMessage(ctx, e));
        e = new OtherEvent("krtek", 1l, new LocalDate(), "1", DateIntegrity.FULL, 1l);
        assertEquals("Hurry up, krtek has birthday!", NotificationSender.formatMessage(ctx, e));
    }

    public void testFormatLabel() {
        Birthday ctx = this.getActivity();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l, Zodiac.AQUARIUS);
        assertEquals("Birthday alert!", NotificationSender.formatLabel(ctx, e));
        e = new AnniversaryEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Anniversary alert!", NotificationSender.formatLabel(ctx, e));
        e = new OtherEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Birthday alert!", NotificationSender.formatLabel(ctx, e));
    }

    public void testFireBirthdayAlert() {
        Birthday ctx = this.getActivity();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l, Zodiac.AQUARIUS);
        NotificationSender.fireBirthdayAlert(ctx, e, System.currentTimeMillis());
    }

    public void testNotificationSender() {
        Birthday ctx = this.getActivity();
        Utils.startNotificationAlarm(ctx, System.currentTimeMillis() + 5000);
    }
}
