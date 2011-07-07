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

import android.provider.ContactsContract;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 * Date: 29.1.11
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public enum EventType {


    BIRTHDAY(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY),
    ANNIVERSARY(ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY),
    CUSTOM(ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM),
    OTHER(ContactsContract.CommonDataKinds.Event.TYPE_OTHER);

    EventType(int code) {
        this.code = code;
    }
    private int code;

    public int getCode() {
        return code;
    }

    public static EventType getEventType(int code) {
        switch (code) {
            case (ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY): {
                return EventType.BIRTHDAY;
            }
            case (ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY): {
                return EventType.ANNIVERSARY;
            }
            case (ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM): {
                return EventType.CUSTOM;
            }
            case (ContactsContract.CommonDataKinds.Event.TYPE_OTHER): {
                return EventType.OTHER;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + code);
    }
}
