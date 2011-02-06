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

import android.text.Editable;
import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 * Date: 29.1.11
 * Time: 20:38
 * To change this template use File | Settings | File Templates.
 */
public class EditableEvent implements Cloneable {
    private Long eventId;
    private String label;
    private EventType type;
    private LocalDate eventDate;
    private DateIntegrity integrity;
    private Long contactId;

    public EditableEvent(Long eventId, EventType type, LocalDate eventDate, DateIntegrity integrity, String label) {
        this.eventId = eventId;
        this.type = type;
        this.eventDate = eventDate;
        this.integrity = integrity;
        this.label = label;
    }

    public EditableEvent(Long contactId) {
        this.contactId = contactId;
        this.eventId = -1L;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public DateIntegrity getIntegrity() {
        return integrity;
    }

    public void setIntegrity(DateIntegrity integrity) {
        this.integrity = integrity;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
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
}