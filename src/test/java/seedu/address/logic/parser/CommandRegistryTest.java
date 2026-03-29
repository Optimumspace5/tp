package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.LockCommand;
import seedu.address.logic.commands.ToggleCommand;
import seedu.address.logic.commands.UnlockCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Test suite for CommandRegistry to ensure mode-based authorization and
 * correct command instantiation.
 */
public class CommandRegistryTest {

    private final CommandRegistry registry = new CommandRegistry();

    @Test
    public void parse_validCommandInBothModes_success() throws Exception {
        // Help is allowed in both LOCKED and UNLOCKED
        assertTrue(registry.parse(HelpCommand.COMMAND_WORD, "", AppMode.LOCKED) instanceof HelpCommand);
        assertTrue(registry.parse(HelpCommand.COMMAND_WORD, "", AppMode.UNLOCKED) instanceof HelpCommand);
    }

    @Test
    public void parse_lockInLockedMode_throwsParseException() {
        // LockCommand is only registered for Unlocked mode.
        // In Locked mode, it should be treated as an unknown command.
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                registry.parse(LockCommand.COMMAND_WORD, "", AppMode.LOCKED));
    }

    @Test
    public void parse_lockInUnlockedMode_success() throws Exception {
        assertTrue(registry.parse(LockCommand.COMMAND_WORD, "", AppMode.UNLOCKED) instanceof LockCommand);
    }

    @Test
    public void parse_unlockInLockedMode_success() throws Exception {
        // UnlockCommand is allowed in Locked mode to facilitate the transition
        Command command = registry.parse(UnlockCommand.COMMAND_WORD, "password123", AppMode.LOCKED);
        assertTrue(command instanceof UnlockCommand);
    }

    @Test
    public void parse_unlockInUnlockedMode_success() throws Exception {
        // UnlockCommand is allowed in Unlocked mode to provide already unlocked feedback
        Command command = registry.parse(UnlockCommand.COMMAND_WORD, "anyPassword", AppMode.UNLOCKED);
        assertTrue(command instanceof UnlockCommand);
    }

    @Test
    public void parse_toggleInLockedMode_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                registry.parse(ToggleCommand.COMMAND_WORD, " 1", AppMode.LOCKED));
    }

    @Test
    public void parse_toggleInUnlockedMode_success() throws Exception {
        assertTrue(registry.parse(ToggleCommand.COMMAND_WORD, " 1", AppMode.UNLOCKED) instanceof ToggleCommand);
    }

    @Test
    public void parse_commandWithArgs_correctlyDelegatesToParser() throws Exception {
        // Verify complex commands like AddCommand work through the registry factory
        String addArgs = " -n John Doe -p 98765432 -e johnd@example.com -a 311, Clementi Ave 2, #02-25";
        assertTrue(registry.parse(AddCommand.COMMAND_WORD, addArgs, AppMode.UNLOCKED) instanceof AddCommand);
    }

    @Test
    public void parse_viewInBothModes_success() throws Exception {
        assertTrue(registry.parse(ViewCommand.COMMAND_WORD, " 1", AppMode.LOCKED) instanceof ViewCommand);
        assertTrue(registry.parse(ViewCommand.COMMAND_WORD, " 1", AppMode.UNLOCKED) instanceof ViewCommand);
    }

    @Test
    public void parse_unregisteredCommand_throwsParseException() {
        // Truly unknown commands should return standard unknown message
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                registry.parse("not_a_real_command", "", AppMode.UNLOCKED));
    }

    @Test
    public void parse_nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.parse(null, "", AppMode.LOCKED));
        assertThrows(NullPointerException.class, () -> registry.parse("help", null, AppMode.LOCKED));
        assertThrows(NullPointerException.class, () -> registry.parse("help", "", null));
    }
}
