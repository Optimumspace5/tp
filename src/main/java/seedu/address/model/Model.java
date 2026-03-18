package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
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

    /** Returns the AddressBook */
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
     * Switches the active contact list to the locked persons list.
     */
    void useLockedPersonList();

    /**
     * Switches the active contact list to the unlocked persons list.
     */
    void useUnlockedPersonList();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the currently active contact list.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person from the currently active contact list.
     * The person must exist in the active contact list.
     */
    void deletePerson(Person target);

    /**
     * Clears all persons from the currently active contact list.
     */
    void clearPersons();

    /**
     * Adds the given person to the currently active contact list.
     * {@code person} must not already exist in the active contact list.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}
     * in the currently active contact list.
     * {@code target} must exist in the active contact list.
     * The identity of {@code editedPerson} must not be the same as another
     * existing person in the active contact list.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list for the currently active contact list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns the password of the address book.
     */
    String getAddressBookPassword();

    /**
     * Sets the password for the address book.
     */
    void setAddressBookPassword(String password);
}
