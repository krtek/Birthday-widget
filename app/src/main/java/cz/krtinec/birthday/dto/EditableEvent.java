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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import org.joda.time.LocalDate;

/**
 * Object used in event editing.
 */
public class EditableEvent implements Cloneable, Parcelable {
    public Long eventId;
    public String label;
    public EventType type;
    public LocalDate eventDate;
    public DateIntegrity integrity;
    public Long rawContactId;

    public EditableEvent(Long eventId, EventType type, LocalDate eventDate, DateIntegrity integrity, String label) {
        this.eventId = eventId;
        this.type = type;
        this.eventDate = eventDate;
        this.integrity = integrity;
        this.label = label;
    }

    public EditableEvent(Long rawContactId) {
        this.rawContactId = rawContactId;
        this.eventId = -1L;
    }


    @Override
    public EditableEvent clone() {
        return new EditableEvent(this.eventId, this.type, this.eventDate, this.integrity, this.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EditableEvent that = (EditableEvent) o;

        if (eventDate != null ? !eventDate.equals(that.eventDate) : that.eventDate != null) return false;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventId != null ? eventId.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        return result;
    }

    private EditableEvent(Parcel in) {
        eventId = in.readLong();
        label = in.readString();
        type = EventType.getEventType(in.readInt());
        Long evtLong = in.readLong();
        integrity = DateIntegrity.valueOf(in.readString());
        Long contactId = in.readLong();
        rawContactId = contactId == -1 ? null : contactId;

    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(eventId);
        parcel.writeString(label);
        //default to birthday
        parcel.writeInt(type == null ? EventType.BIRTHDAY.getCode() : type.getCode());
        parcel.writeLong(eventDate == null ? -1 : eventDate.toDateMidnight().toDate().getTime() );
        parcel.writeString(integrity == null ? null : integrity.name());
        parcel.writeLong(rawContactId == null ? -1l : rawContactId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<EditableEvent> CREATOR =
            new Parcelable.Creator<EditableEvent>() {
                public EditableEvent createFromParcel(Parcel in) {
                    return new EditableEvent(in);
                }

                public EditableEvent[] newArray(int size) {
                    return new EditableEvent[size];
                }
            };
}