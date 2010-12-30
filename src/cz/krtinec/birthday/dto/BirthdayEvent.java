package cz.krtinec.birthday.dto;

import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * User: krtek
 */
public class BirthdayEvent extends Event {
    private Integer age;

    public BirthdayEvent(String displayName, long id, LocalDate eventDate, String lookupKey,
                          DateIntegrity integrity) {
        super(displayName, id, eventDate, lookupKey, integrity);
        if (DateIntegrity.FULL == this.integrity) {
             age = today.getYear() - eventDate.getYear();
             age = nextYear ? age + 1: age;
         } else {
             age = null;
         }
    }

    public Integer getAge() {
		return age;
	}

}
