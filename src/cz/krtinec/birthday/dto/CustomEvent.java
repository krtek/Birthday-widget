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
                       DateIntegrity integrity, String label) {
        super(displayName, id, eventDate, lookupKey, integrity);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
