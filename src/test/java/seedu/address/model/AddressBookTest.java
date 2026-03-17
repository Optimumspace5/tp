package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getLockedPersonList());
        assertEquals(Collections.emptyList(), addressBook.getUnlockedPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateLockedPersons_throwsDuplicatePersonException() {
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newLockedPersons = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newLockedPersons, Collections.emptyList());

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasLockedPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasLockedPerson(null));
    }

    @Test
    public void hasLockedPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasLockedPerson(ALICE));
    }

    @Test
    public void hasLockedPerson_personInAddressBook_returnsTrue() {
        addressBook.addLockedPerson(ALICE);
        assertTrue(addressBook.hasLockedPerson(ALICE));
    }

    @Test
    public void hasLockedPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addLockedPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasLockedPerson(editedAlice));
    }

    @Test
    public void getLockedPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getLockedPersonList().remove(0));
    }

    @Test
    public void getUnlockedPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getUnlockedPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName()
                + "{lockedPersons=" + addressBook.getLockedPersonList()
                + ", unlockedPersons=" + addressBook.getUnlockedPersonList()
                + ", password=" + addressBook.getPassword() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons lists can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> lockedPersons = FXCollections.observableArrayList();
        private final ObservableList<Person> unlockedPersons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> lockedPersons, Collection<Person> unlockedPersons) {
            this.lockedPersons.setAll(lockedPersons);
            this.unlockedPersons.setAll(unlockedPersons);
        }

        @Override
        public ObservableList<Person> getLockedPersonList() {
            return lockedPersons;
        }

        @Override
        public ObservableList<Person> getUnlockedPersonList() {
            return unlockedPersons;
        }

        @Override
        public String getPassword() {
            return "";
        }
    }

}
