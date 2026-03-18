package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.AppMode;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook. */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns an unmodifiable view of the locked person list.
     */
    ObservableList<Person> getLockedPersonList();

    /**
     * Returns an unmodifiable view of the unlocked person list.
     */
    ObservableList<Person> getUnlockedPersonList();

    /**
     * Returns true if a person with the same identity as {@code person}
     * exists in the contact list for the given {@code appMode}.
     */
    boolean hasPerson(Person person, AppMode appMode);

    /**
     * Deletes the given person from the contact list for the given {@code appMode}.
     * The person must exist in the relevant contact list.
     */
    void deletePerson(Person target, AppMode appMode);

    /**
     * Clears all persons from the contact list for the given {@code appMode}.
     */
    void clearPersons(AppMode appMode);

    /**
     * Adds the given person to the contact list for the given {@code appMode}.
     * {@code person} must not already exist in the relevant contact list.
     */
    void addPerson(Person person, AppMode appMode);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}
     * in the contact list for the given {@code appMode}.
     * {@code target} must exist in the relevant contact list.
     * The identity of {@code editedPerson} must not be the same as another
     * existing person in the relevant contact list.
     */
    void setPerson(Person target, Person editedPerson, AppMode appMode);

    /**
     * Returns an unmodifiable view of the filtered person list
     * for the given {@code appMode}.
     */
    ObservableList<Person> getFilteredPersonList(AppMode appMode);

    /**
     * Updates the filter of the filtered person list for the given {@code appMode}
     * to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} or {@code appMode} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate, AppMode appMode);

    /**
     * Returns the password of the address book.
     */
    String getAddressBookPassword();

    /**
     * Sets the password for the address book.
     */
    void setAddressBookPassword(String password);
}
