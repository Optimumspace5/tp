package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.AppMode;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains unit tests for {@code UnlockCommand}.
 */
public class UnlockCommandTest {

    private static final String VALID_PASSWORD = "nusStudent2026";
    private static final String WRONG_PASSWORD = "wrongPassword";

    @Test
    public void execute_alreadyUnlocked_throwsCommandException() {
        UnlockCommand unlockCommand = new UnlockCommand(VALID_PASSWORD);
        ModelStub modelStub = new ModelStubWithPassword(VALID_PASSWORD);

        CommandContext context = new CommandContext(modelStub, AppMode.UNLOCKED);

        assertThrows(CommandException.class,
                UnlockCommand.MESSAGE_ALREADY_UNLOCKED, () -> unlockCommand.execute(context));
    }

    @Test
    public void execute_wrongPasswordLockedMode_throwsDiscreetException() {
        UnlockCommand unlockCommand = new UnlockCommand(WRONG_PASSWORD);
        ModelStub modelStub = new ModelStubWithPassword(VALID_PASSWORD);

        CommandContext context = new CommandContext(modelStub, AppMode.LOCKED);

        assertThrows(CommandException.class,
                MESSAGE_UNKNOWN_COMMAND, () -> unlockCommand.execute(context));
    }

    @Test
    public void execute_correctPasswordLockedMode_unlockSuccessful() throws Exception {
        UnlockCommand unlockCommand = new UnlockCommand(VALID_PASSWORD);
        ModelStub modelStub = new ModelStubWithPassword(VALID_PASSWORD);

        CommandContext context = new CommandContext(modelStub, AppMode.LOCKED);

        CommandResult result = unlockCommand.execute(context);

        assertEquals(UnlockCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(Optional.of(AppMode.UNLOCKED), result.getRequestedMode());
    }

    /**
     * A Model stub that provides the password for validation.
     */
    private class ModelStubWithPassword extends ModelStub {
        private final String password;

        ModelStubWithPassword(String password) {
            this.password = password;
        }

        @Override
        public String getAddressBookPassword() {
            return password;
        }
    }

    /**
     * A default model stub where all methods fail by default.
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
