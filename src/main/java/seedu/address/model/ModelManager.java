package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.AppMode;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredLockedPersons;
    private final FilteredList<Person> filteredUnlockedPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredLockedPersons = new FilteredList<>(this.addressBook.getLockedPersonList());
        this.filteredUnlockedPersons = new FilteredList<>(this.addressBook.getUnlockedPersonList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public ObservableList<Person> getLockedPersonList() {
        return addressBook.getLockedPersonList();
    }

    @Override
    public ObservableList<Person> getUnlockedPersonList() {
        return addressBook.getUnlockedPersonList();
    }

    @Override
    public boolean hasPerson(Person person, AppMode appMode) {
        requireAllNonNull(person, appMode);
        return isLockedMode(appMode)
                ? addressBook.hasLockedPerson(person)
                : addressBook.hasUnlockedPerson(person);
    }

    @Override
    public void deletePerson(Person target, AppMode appMode) {
        requireAllNonNull(target, appMode);

        if (isLockedMode(appMode)) {
            addressBook.removeLockedPerson(target);
        } else {
            addressBook.removeUnlockedPerson(target);
        }
    }

    @Override
    public void clearPersons(AppMode appMode) {
        requireNonNull(appMode);

        if (isLockedMode(appMode)) {
            addressBook.clearLockedPersons();
        } else {
            addressBook.clearUnlockedPersons();
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, appMode);
    }

    @Override
    public void addPerson(Person person, AppMode appMode) {
        requireAllNonNull(person, appMode);

        if (isLockedMode(appMode)) {
            addressBook.addLockedPerson(person);
        } else {
            addressBook.addUnlockedPerson(person);
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, appMode);
    }

    @Override
    public void setPerson(Person target, Person editedPerson, AppMode appMode) {
        requireAllNonNull(target, editedPerson, appMode);

        if (isLockedMode(appMode)) {
            addressBook.setLockedPerson(target, editedPerson);
        } else {
            addressBook.setUnlockedPerson(target, editedPerson);
        }
    }

    //=========== Password ===================================================================================

    @Override
    public String getAddressBookPassword() {
        return addressBook.getPassword();
    }

    @Override
    public void setAddressBookPassword(String password) {
        String sanitizedPassword = (password != null) ? password : "";
        addressBook.setPassword(sanitizedPassword);
    }

    //=========== Filtered Person List Accessors =============================================================

    @Override
    public ObservableList<Person> getFilteredPersonList(AppMode appMode) {
        requireNonNull(appMode);
        return getFilteredList(appMode);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate, AppMode appMode) {
        requireAllNonNull(predicate, appMode);
        getFilteredList(appMode).setPredicate(predicate);
    }

    private boolean isLockedMode(AppMode appMode) {
        return appMode == AppMode.LOCKED;
    }

    private FilteredList<Person> getFilteredList(AppMode appMode) {
        return isLockedMode(appMode) ? filteredLockedPersons : filteredUnlockedPersons;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredLockedPersons.equals(otherModelManager.filteredLockedPersons)
                && filteredUnlockedPersons.equals(otherModelManager.filteredUnlockedPersons);
    }
}
