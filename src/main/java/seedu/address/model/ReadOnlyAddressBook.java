package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the combined persons list.
     * This list will not contain duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns the password required to access the address book data.
     */
    String getPassword();

}

