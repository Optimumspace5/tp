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
    private FilteredList<Person> filteredPersons;
    private AppMode currentMode;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.currentMode = AppMode.LOCKED;
        this.filteredPersons = new FilteredList<>(getActivePersonSourceList());
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
        refreshFilteredPersonList();
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
    public void setCurrentMode(AppMode mode) {
        requireNonNull(mode);
        currentMode = mode;
        refreshFilteredPersonList();
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return isLockedMode()
                ? addressBook.hasLockedPerson(person)
                : addressBook.hasUnlockedPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        requireNonNull(target);

        if (isLockedMode()) {
            addressBook.removeLockedPerson(target);
        } else {
            addressBook.removeUnlockedPerson(target);
        }
    }

    @Override
    public void clearPersons() {
        if (isLockedMode()) {
            addressBook.clearLockedPersons();
        } else {
            addressBook.clearUnlockedPersons();
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void addPerson(Person person) {
        requireNonNull(person);

        if (isLockedMode()) {
            addressBook.addLockedPerson(person);
        } else {
            addressBook.addUnlockedPerson(person);
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        if (isLockedMode()) {
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

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the
     * currently active contact list.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    private boolean isLockedMode() {
        return currentMode == AppMode.LOCKED;
    }

    private ObservableList<Person> getActivePersonSourceList() {
        return isLockedMode() ? addressBook.getLockedPersonList() : addressBook.getUnlockedPersonList();
    }

    private void refreshFilteredPersonList() {
        Predicate<? super Person> currentPredicate = filteredPersons != null
                ? filteredPersons.getPredicate()
                : PREDICATE_SHOW_ALL_PERSONS;
        filteredPersons = new FilteredList<>(getActivePersonSourceList());
        filteredPersons.setPredicate(currentPredicate != null ? currentPredicate : PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && currentMode == otherModelManager.currentMode;
    }

}
