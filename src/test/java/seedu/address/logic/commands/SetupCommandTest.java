package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.SetupCommand.MESSAGE_SUCCESS;

import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class SetupCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_unlockedMode_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_SUCCESS, true, false);

        assertCommandSuccess(new SetupCommand(), model, AppMode.UNLOCKED, expectedCommandResult, expectedModel);
    }

    @Test
    public void equals() {
        SetupCommand setupCommand = new SetupCommand();

        // same object -> returns true
        assert(setupCommand.equals(setupCommand));

        // same type -> returns true
        assert(setupCommand.equals(new SetupCommand()));

        // different types -> returns false
        assert(!setupCommand.equals(new ExitCommand()));

        // null -> returns false
        assert(!setupCommand.equals(null));
    }
}
