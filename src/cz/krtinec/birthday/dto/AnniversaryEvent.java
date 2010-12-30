package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 */
public class AnniversaryEvent extends Event {
      public AnniversaryEvent(String displayName, long id, LocalDate eventDate, String lookupKey,
                         DateIntegrity integrity) {
          super(displayName, id, eventDate, lookupKey, integrity);
      }
}
