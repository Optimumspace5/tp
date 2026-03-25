package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed in the combined contact list
 * (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private String password = "";

    {
        persons = new UniquePersonList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents of the combined person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setPassword(newData.getPassword());
    }

    /**
     * Returns true if a person with the same identity as {@code person}
     * exists in the combined contact list.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the combined contact list.
     * The person must not already exist in the contact list.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the combined list with {@code editedPerson}.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from the combined contact list.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /**
     * Clears all persons from the combined contact list.
     */
    public void clearPersons() {
        persons.setPersons(Collections.emptyList());
    }

    /**
     * Clears all persons that match the given {@code status}.
     */
    public void clearPersonsWithStatus(PersonStatus status) {
        requireNonNull(status);
        List<Person> remainingPersons = persons.asUnmodifiableObservableList().stream()
                .filter(person -> person.getStatus() != status)
                .collect(Collectors.toList());
        persons.setPersons(remainingPersons);
    }

    /**
     * Returns the password currently protecting this address book.
     *
     * @return The password string stored in the address book.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this address book.
     * Use this to update the credential stored within the address book data file.
     *
     * @param password The new password to be used for authentication.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("password", password)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && Objects.equals(password, otherAddressBook.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, password);
    }
}
