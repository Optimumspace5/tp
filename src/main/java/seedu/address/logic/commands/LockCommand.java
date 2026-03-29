package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.AppMode;

/**
 * Switches the application to the Locked interface.
 * This command triggers a state transition to {@code AppMode.LOCKED}.
 */
public class LockCommand extends Command {

    public static final String COMMAND_WORD = "lock";
    public static final String MESSAGE_SUCCESS = "Switched to Locked Interface.";

    @Override
    public CommandResult execute(CommandContext context) {
        requireNonNull(context);

        // Transition to LOCKED mode.
        return new CommandResult(MESSAGE_SUCCESS, false, false, AppMode.LOCKED);
    }
}
