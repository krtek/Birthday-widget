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

import java.text.ParseException;

import cz.krtinec.birthday.dto.DateIntegrity;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cz.krtinec.birthday.data.BirthdayProvider;
import junit.framework.TestCase;

public class ParseDateTest extends TestCase {
	private DateTimeFormatter SHORT = DateTimeFormat.forPattern("MMdd");
	private DateTimeFormatter LONG = DateTimeFormat.forPattern("yyyyMMdd");
	
	public void testParseDate() throws ParseException {
		BirthdayProvider provider = BirthdayProvider.getInstance();
		assertEquals(new LocalDate(1977, 12, 25), provider.tryParseBDay("1977-12-25").date);
		assertEquals(new LocalDate(1977, 2, 5), provider.tryParseBDay("1977-2-5").date);
		assertEquals(new LocalDate(1977, 12, 25), provider.tryParseBDay("19771225").date);
		assertEquals(SHORT.print(new LocalDate(1977, 12, 25)), SHORT.print(provider.tryParseBDay("--12-25").date));
		assertEquals("20110214", LONG.print(provider.tryParseBDay("1297709820964").date));
		assertEquals("19300823", LONG.print(provider.tryParseBDay("-1242021270969").date));		
		assertEquals(LONG.print(new LocalDate(1977, 12, 25)), LONG.print(provider.tryParseBDay("1977-12-25T00:00:00").date));		
		assertEquals("19800909", LONG.print(provider.tryParseBDay("1980-9-9-0").date));
		assertEquals("20090515", LONG.print(provider.tryParseBDay("2009-05-15T23:00:00.000Z").date));		
		assertEquals("19721018", LONG.print(provider.tryParseBDay("1972-10-18T00:00:00.000+01:00").date));
		assertEquals("19721018", LONG.print(provider.tryParseBDay("72-10-18").date));
        assertEquals(DateIntegrity.WITHOUT_YEAR, provider.tryParseBDay("00001018").integrity);
        assertEquals(DateIntegrity.WITHOUT_YEAR, provider.tryParseBDay("0000-10-18").integrity);
        assertEquals("1018", SHORT.print(provider.tryParseBDay("0000-10-18").date));
        assertEquals("1018", SHORT.print(provider.tryParseBDay("00001018").date));
		
	}
}
