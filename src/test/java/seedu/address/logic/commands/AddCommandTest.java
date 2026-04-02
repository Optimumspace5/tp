package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();
        Person expectedPerson = new PersonBuilder(validPerson).withStatus(PersonStatus.UNLOCKED).build();

        CommandContext context = new CommandContext(modelStub, AppMode.UNLOCKED);
        CommandResult commandResult = new AddCommand(validPerson).execute(context);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(expectedPerson), modelStub.persons);
        assertTrue(commandResult.getSelectedIndex().isPresent());
        assertEquals(Index.fromOneBased(1), commandResult.getSelectedIndex().get());
    }

    @Test
    public void execute_duplicatePersonInUnlockedMode_throwsCommandException() {
        Person existingPerson = new PersonBuilder().build();
        ModelStubWithExistingPerson modelStub = new ModelStubWithExistingPerson(existingPerson);

        CommandContext context = new CommandContext(modelStub, AppMode.UNLOCKED);

        assertThrows(CommandException.class, CommandUtil.MESSAGE_DUPLICATE_PERSON, () ->
            new AddCommand(existingPerson).execute(context));
        assertEquals(Arrays.asList(existingPerson), modelStub.persons);
        assertTrue(modelStub.getDeletedPerson() == null);
    }

    @Test
    public void execute_unlockedDuplicateInLockedMode_replacesExistingPerson() throws Exception {
        Person existingUnlockedPerson = new PersonBuilder().withStatus(PersonStatus.UNLOCKED).build();
        Person expectedPerson = new PersonBuilder(existingUnlockedPerson).withStatus(PersonStatus.LOCKED).build();
        ModelStubWithExistingPerson modelStub = new ModelStubWithExistingPerson(existingUnlockedPerson);

        CommandContext context = new CommandContext(modelStub, AppMode.LOCKED);
        CommandResult commandResult = new AddCommand(existingUnlockedPerson).execute(context);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(existingUnlockedPerson, modelStub.getDeletedPerson());
        assertEquals(Arrays.asList(expectedPerson), modelStub.persons);
        assertTrue(commandResult.getSelectedIndex().isPresent());
        assertEquals(Index.fromOneBased(1), commandResult.getSelectedIndex().get());
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        assertTrue(addAliceCommand.equals(addAliceCommand));
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));
        assertFalse(addAliceCommand.equals(1));
        assertFalse(addAliceCommand.equals(null));
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName()
                + "{name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail()
                + ", address=" + ALICE.getAddress()
                + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person, AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target, AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearPersons(AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person, AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson, AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList(AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getAddressBookPassword() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookPassword(String password) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate, AppMode appMode) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> persons = new ArrayList<>();

        @Override
        public ObservableList<Person> getPersonList() {
            return FXCollections.observableArrayList(persons);
        }

        @Override
        public void addPerson(Person person, AppMode appMode) {
            requireNonNull(person);
            persons.add(person);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList(AppMode appMode) {
            return FXCollections.observableArrayList(persons);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    /**
     * A Model stub that starts with one existing person and records duplicate-handling effects.
     */
    private class ModelStubWithExistingPerson extends ModelStub {
        final ArrayList<Person> persons = new ArrayList<>();
        private Person deletedPerson;

        ModelStubWithExistingPerson(Person person) {
            requireNonNull(person);
            persons.add(person);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return FXCollections.observableArrayList(persons);
        }

        @Override
        public void deletePerson(Person target, AppMode appMode) {
            requireNonNull(target);
            persons.remove(target);
            deletedPerson = target;
        }

        @Override
        public void addPerson(Person person, AppMode appMode) {
            requireNonNull(person);
            persons.add(person);
        }

        @Override
        public ObservableList<Person> getFilteredPersonList(AppMode appMode) {
            return FXCollections.observableArrayList(persons);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }

        public Person getDeletedPerson() {
            return deletedPerson;
        }
    }
}
