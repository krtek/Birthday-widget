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

package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 * Date: 30.12.10
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class CustomEvent extends Event {
    private String label;

    public CustomEvent(String displayName, long id, LocalDate eventDate, String lookupKey,
                       DateIntegrity integrity, long rawContactId, String label) {
        super(displayName, id, eventDate, lookupKey, integrity, rawContactId);
        this.label = label;
    }

    /**
     * Custom event description.
     * @return
     */
    public String getLabel() {
        return label;
    }
}
