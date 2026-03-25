package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.AppMode;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private static final AppMode TEST_MODE = AppMode.LOCKED;

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null, TEST_MODE));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE, TEST_MODE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE, TEST_MODE);
        assertTrue(modelManager.hasPerson(ALICE, TEST_MODE));
    }

    @Test
    public void hasPerson_sameIdentityDifferentStatus_returnsTrue() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder().withPerson(unlockedBenson).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        assertTrue(modelManager.hasPerson(BENSON, TEST_MODE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
                modelManager.getFilteredPersonList(TEST_MODE).remove(0));
    }

    @Test
    public void getFilteredPersonList_lockedMode_showsOnlyLockedPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(unlockedBenson).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        assertEquals(1, modelManager.getFilteredPersonList(AppMode.LOCKED).size());
        assertEquals(ALICE, modelManager.getFilteredPersonList(AppMode.LOCKED).get(0));
    }

    @Test
    public void getFilteredPersonList_unlockedMode_showsAllPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(unlockedBenson).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        assertEquals(2, modelManager.getFilteredPersonList(AppMode.UNLOCKED).size());
        assertTrue(modelManager.getFilteredPersonList(AppMode.UNLOCKED).contains(ALICE));
        assertTrue(modelManager.getFilteredPersonList(AppMode.UNLOCKED).contains(unlockedBenson));
    }

    @Test
    public void clearPersons_lockedMode_removesOnlyLockedPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(unlockedBenson).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.clearPersons(AppMode.LOCKED);

        assertEquals(1, modelManager.getPersonList().size());
        assertEquals(unlockedBenson, modelManager.getPersonList().get(0));
        assertTrue(modelManager.getFilteredPersonList(AppMode.LOCKED).isEmpty());
        assertEquals(1, modelManager.getFilteredPersonList(AppMode.UNLOCKED).size());
        assertEquals(unlockedBenson, modelManager.getFilteredPersonList(AppMode.UNLOCKED).get(0));
    }

    @Test
    public void clearPersons_unlockedMode_removesAllPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(unlockedBenson).build();
        modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.clearPersons(AppMode.UNLOCKED);

        assertTrue(modelManager.getPersonList().isEmpty());
        assertTrue(modelManager.getFilteredPersonList(AppMode.LOCKED).isEmpty());
        assertTrue(modelManager.getFilteredPersonList(AppMode.UNLOCKED).isEmpty());
    }

    @Test
    public void getAddressBookPassword_defaultAddressBook_returnsEmptyString() {
        assertEquals("", modelManager.getAddressBookPassword());
    }

    @Test
    public void setAddressBookPassword_validPassword_setsPassword() {
        String newPassword = "newPassword123";
        modelManager.setAddressBookPassword(newPassword);
        assertEquals(newPassword, modelManager.getAddressBookPassword());
        assertEquals(newPassword, modelManager.getAddressBook().getPassword());
    }

    @Test
    public void setAddressBookPassword_nullPassword_setsEmptyString() {
        modelManager.setAddressBookPassword(null);
        assertEquals("", modelManager.getAddressBookPassword());
    }

    @Test
    public void equals_differentPassword_returnsFalse() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).build();
        UserPrefs userPrefs = new UserPrefs();

        modelManager = new ModelManager(addressBook, userPrefs);
        AddressBook addressBookWithDifferentPassword = new AddressBook(addressBook);
        addressBookWithDifferentPassword.setPassword("different");

        ModelManager otherModelManager = new ModelManager(addressBookWithDifferentPassword, userPrefs);

        assertFalse(modelManager.equals(otherModelManager));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        assertTrue(modelManager.equals(modelManager));
        assertFalse(modelManager.equals(null));
        assertFalse(modelManager.equals(5));
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)), TEST_MODE);
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, TEST_MODE);

        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
