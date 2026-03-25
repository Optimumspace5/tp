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
     * Returns an unmodifiable view of the combined person list.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns true if a person with the same identity as {@code person}
     * exists in the combined contact list.
     */
    boolean hasPerson(Person person, AppMode appMode);

    /**
     * Deletes the given person from the combined contact list.
     */
    void deletePerson(Person target, AppMode appMode);

    /**
     * Clears persons according to the current app mode.
     * In locked mode, only status=LOCKED persons are cleared.
     * In unlocked mode, the whole combined list is cleared.
     */
    void clearPersons(AppMode appMode);

    /**
     * Adds the given person to the combined contact list.
     */
    void addPerson(Person person, AppMode appMode);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}
     * in the combined contact list.
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
