package seedu.address.logic.commands;

import seedu.address.logic.AppMode;
import seedu.address.model.Model;

/**
 * Switches the app to the Unlocked interface.
 */

public class UnlockCommand extends Command {

    public static final String COMMAND_WORD = "unlock";
    public static final String MESSAGE_SUCCESS = "Switched to Unlocked Interface.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_SUCCESS, false, false, AppMode.UNLOCKED);
    }
}
