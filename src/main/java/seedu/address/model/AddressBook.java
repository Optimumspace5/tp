package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed within each contact list
 * (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList lockedPersons;
    private final UniquePersonList unlockedPersons;
    private String password = "";

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     */
    {
        lockedPersons = new UniquePersonList();
        unlockedPersons = new UniquePersonList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the locked person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setLockedPersons(List<Person> persons) {
        this.lockedPersons.setPersons(persons);
    }

    /**
     * Replaces the contents of the unlocked person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setUnlockedPersons(List<Person> persons) {
        unlockedPersons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setLockedPersons(newData.getLockedPersonList());
        setUnlockedPersons(newData.getUnlockedPersonList());
        setPassword(newData.getPassword());
    }

    /**
     * Returns true if a person with the same identity as {@code person}
     * exists in the locked contact list.
     */
    public boolean hasLockedPerson(Person person) {
        requireNonNull(person);
        return lockedPersons.contains(person);
    }

    /**
     * Returns true if a person with the same identity as {@code person}
     * exists in the unlocked contact list.
     */
    public boolean hasUnlockedPerson(Person person) {
        requireNonNull(person);
        return unlockedPersons.contains(person);
    }

    /**
     * Adds a person to the locked contact list.
     * The person must not already exist in the locked contact list.
     */
    public void addLockedPerson(Person p) {
        lockedPersons.add(p);
    }

    /**
     * Adds a person to the unlocked contact list.
     * The person must not already exist in the unlocked contact list.
     */
    public void addUnlockedPerson(Person p) {
        unlockedPersons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the locked list with {@code editedPerson}.
     */
    public void setLockedPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        lockedPersons.setPerson(target, editedPerson);
    }
    
    /**
     * Replaces the given person {@code target} in the unlocked list with {@code editedPerson}.
     */
    public void setUnlockedPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        unlockedPersons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from the locked contact list.
     */
    public void removeLockedPerson(Person key) {
        lockedPersons.remove(key);
    }

    /**
     * Removes {@code key} from the unlocked contact list.
     */
    public void removeUnlockedPerson(Person key) {
        unlockedPersons.remove(key);
    }

    /**
     * Clears all persons from the locked contact list.
     */
    public void clearLockedPersons() {
        lockedPersons.setPersons(Collections.emptyList());
    }

    /**
     * Clears all persons from the unlocked contact list.
     */
    public void clearUnlockedPersons() {
        unlockedPersons.setPersons(Collections.emptyList());
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

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("lockedPersons", lockedPersons)
                .add("unlockedPersons", unlockedPersons)
                .add("password", password)
                .toString();
    }

    @Override
    public ObservableList<Person> getLockedPersonList() {
        return lockedPersons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Person> getUnlockedPersonList() {
        return unlockedPersons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return lockedPersons.equals(otherAddressBook.lockedPersons)
                && unlockedPersons.equals(otherAddressBook.unlockedPersons)
                && Objects.equals(password, otherAddressBook.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockedPersons, unlockedPersons, password);
    }
}
