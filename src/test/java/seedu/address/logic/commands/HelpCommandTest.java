package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    private static String expectedManual(String purpose, String usage, String example) {
        return "Purpose: " + purpose + "\n"
            + "Usage: " + usage + "\n"
            + "Example: " + example;
    }

    @Test
    public void execute_helpLockedMode_success() {
        CommandResult expectedCommandResult = new CommandResult(HelpCommand.GENERAL_MANUAL_LOCKED);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_helpUnlockedMode_success() throws Exception {
        CommandResult commandResult = new HelpCommand().execute(new CommandContext(model, AppMode.UNLOCKED));

        assertEquals(HelpCommand.GENERAL_MANUAL_UNLOCKED, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_helpSpecificCommandAdd_success() throws Exception {
        CommandResult commandResult = new HelpCommand("add")
                .execute(new CommandContext(model, AppMode.LOCKED));

        assertEquals(
                expectedManual("Add a new contact.",
                        "add -n NAME -p PHONE -e EMAIL -a ADDRESS [-t TAG]...",
                        "add -n John Doe -p 98765432 -e jdoe@example.com -a Clementi Ave 2"),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_helpSpecificCommandUnlock_success() throws Exception {
        CommandResult commandResult = new HelpCommand("unlock")
                .execute(new CommandContext(model, AppMode.UNLOCKED));

        assertEquals(
                expectedManual("Switch to unlocked mode using your password.",
                        "unlock PASSWORD",
                        "unlock mySecurePassword123"),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_helpSpecificCommandToggle_success() throws Exception {
        CommandResult commandResult = new HelpCommand("toggle")
                .execute(new CommandContext(model, AppMode.UNLOCKED));

        assertEquals(
                expectedManual("Toggle a contact's status between Public and Sensitive.",
                        "toggle INDEX",
                        "toggle 1"),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_visibleManualsInLockedMode_success() throws Exception {
        String[] visibleCommands = { "add", "edit", "delete", "clear", "find", "list", "view", "exit", "help" };

        for (String command : visibleCommands) {
            CommandResult commandResult = new HelpCommand(command)
                    .execute(new CommandContext(model, AppMode.LOCKED));
            String feedback = commandResult.getFeedbackToUser();
            assertTrue(feedback.startsWith("Purpose:"));
            assertTrue(feedback.contains("\nUsage:"));
            assertTrue(feedback.contains("\nExample:"));
        }
    }

    @Test
    public void execute_visibleManualsInUnlockedMode_success() throws Exception {
        String[] visibleCommands = {
            "add", "edit", "delete", "clear", "find", "list", "view", "toggle",
            "lock", "unlock", "setup", "exit", "help"
        };

        for (String command : visibleCommands) {
            CommandResult commandResult = new HelpCommand(command)
                    .execute(new CommandContext(model, AppMode.UNLOCKED));
            String feedback = commandResult.getFeedbackToUser();
            assertTrue(feedback.startsWith("Purpose:"));
            assertTrue(feedback.contains("\nUsage:"));
            assertTrue(feedback.contains("\nExample:"));
        }
    }

    @Test
    public void execute_unknownManual_throwsCommandException() {
        assertThrows(CommandException.class,
                String.format(HelpCommand.MESSAGE_UNKNOWN_MANUAL, "unknown"), ()
                -> new HelpCommand("unknown").execute(new CommandContext(model, AppMode.LOCKED)));
    }

    @Test
    public void execute_hiddenManualsInLockedMode_throwsCommandException() {
        String[] hiddenCommands = { "lock", "unlock", "setup", "toggle" };

        for (String hiddenCommand : hiddenCommands) {
            assertThrows(CommandException.class,
                    String.format(HelpCommand.MESSAGE_UNKNOWN_MANUAL, hiddenCommand), ()
                    -> new HelpCommand(hiddenCommand).execute(new CommandContext(model, AppMode.LOCKED)));
        }
    }
}
