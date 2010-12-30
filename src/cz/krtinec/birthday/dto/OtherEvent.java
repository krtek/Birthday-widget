package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 * Date: 30.12.10
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class OtherEvent extends Event {

    public OtherEvent(String displayName, long id, LocalDate eventDate, String lookupKey,
                      DateIntegrity integrity) {
        super(displayName, id, eventDate, lookupKey, integrity);
    }
}
