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

package cz.krtinec.birthday.widgets;

import android.test.ServiceTestCase;
import cz.krtinec.birthday.dto.*;
import cz.krtinec.birthday.widgets.UpdateService2x2;
import junit.framework.TestCase;
import org.joda.time.LocalDate;

import java.util.Date;

/**
 * <b>Note:<b/> This test works only with phone language set to English!
 * User: lukas.marek@gmail.com
 * Date: 26.07.11
 * Time: 21:36
 */
public class UpdateServiceInstrumentationTest extends ServiceTestCase<UpdateService2x2> {

    public UpdateServiceInstrumentationTest() {
        super(UpdateService2x2.class);
    }

    @Override
    protected void setUp() throws Exception {
        this.setupService();
    }

    public void testFormatText() {
        UpdateService2x2 service = this.getService();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Hurry up, krtek has birthday!", service.formatMessage(e));
        e = new AnniversaryEvent("krtek", 1l, new LocalDate(), "1", DateIntegrity.FULL, 1l);
        assertEquals("Hurry up, krtek has anniversary!", service.formatMessage(e));
        e = new OtherEvent("krtek", 1l, new LocalDate(), "1", DateIntegrity.FULL, 1l);
        assertEquals("Hurry up, krtek has birthday!", service.formatMessage(e));
    }

    public void testFormatLabel() {
        UpdateService2x2 service = this.getService();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Birthday alert!", service.formatLabel(e));
        e = new AnniversaryEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Anniversary alert!", service.formatLabel(e));
        e = new OtherEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        assertEquals("Birthday alert!", service.formatLabel(e));
    }

    public void testFireBirthdayAlert() {
        UpdateService2x2 service = this.getService();
        Event e = new BirthdayEvent("krtek", 1l, new LocalDate(), "123", DateIntegrity.FULL, 1l);
        service.fireBirthdayAlert(e, new Date().getTime());
    }
}
