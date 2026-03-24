package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.AppMode;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains unit tests for {@code LockCommand}.
 */
public class LockCommandTest {

    @Test
    public void execute_lockSuccessful() {
        ModelStub modelStub = new ModelStub();
        LockCommand lockCommand = new LockCommand();

        CommandContext context = new CommandContext(modelStub, AppMode.UNLOCKED);
        CommandResult result = lockCommand.execute(context);

        assertEquals(LockCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(Optional.of(AppMode.LOCKED), result.getRequestedMode());
    }

    @Test
    public void execute_nullContext_throwsNullPointerException() {
        LockCommand lockCommand = new LockCommand();
        assertThrows(NullPointerException.class, () -> lockCommand.execute(null));
    }

    /**
     * A default model stub where all methods fail by default.
     * LockCommand doesn't use the model, so we just need a non-null instance.
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
}
