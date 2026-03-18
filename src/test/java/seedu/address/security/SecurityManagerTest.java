package seedu.address.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.AppMode;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

public class SecurityManagerTest {

    @Test
    public void isAuthenticated_passwordExists_returnsTrue() {
        LogicStub logicStub = new LogicStub();
        logicStub.setAddressBookPassword("any_password");
        SecurityManager securityManager = new SecurityManager(logicStub, Optional::empty);

        assertTrue(securityManager.isAuthenticated());
    }

    @Test
    public void isAuthenticated_noPassword_successfulSetup() {
        LogicStub logicStub = new LogicStub();
        String rawPassword = "nusStudent2026";

        SecurityManager securityManager = new SecurityManager(logicStub, () -> Optional.of(rawPassword));

        assertTrue(securityManager.isAuthenticated());
        assertEquals(rawPassword, logicStub.getAddressBookPassword());
    }

    @Test
    public void isAuthenticated_setupCancelled_returnsFalse() {
        LogicStub logicStub = new LogicStub();

        // Simulate user closes the window (supplier returns empty)
        SecurityManager securityManager = new SecurityManager(
                logicStub,
                Optional::empty
        );

        assertFalse(securityManager.isAuthenticated());
        assertEquals("", logicStub.getAddressBookPassword());
    }

    @Test
    public void isAuthenticated_invalidStoredPassword_promptsForSetup() {
        LogicStub logicStub = new LogicStub();
        logicStub.setAddressBookPassword("password with spaces");

        String newValidPassword = "newValidPassword123";
        SecurityManager securityManager =
                new SecurityManager(logicStub, () -> Optional.of(newValidPassword));

        assertTrue(securityManager.isAuthenticated());
        assertEquals(newValidPassword, logicStub.getAddressBookPassword());
    }

    /**
     * A stub Logic where all methods fail except those needed for SecurityManager.
     */
    private static class LogicStub implements Logic {
        private String password = "";

        @Override
        public String getAddressBookPassword() {
            return password;
        }

        @Override
        public void setAddressBookPassword(String password) {
            this.password = password;
        }

        @Override
        public void saveAddressBook() {
            // No-op: We don't need to actually write to disk for this unit test
        }

        @Override
        public GuiSettings getGuiSettings() {
            return new GuiSettings();
        }

        @Override
        public CommandResult execute(String commandText) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public AppMode getCurrentMode() {
            return AppMode.LOCKED;
        }
    }
}
