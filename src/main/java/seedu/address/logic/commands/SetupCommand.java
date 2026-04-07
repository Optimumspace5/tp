package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Transitions the application to the Setup state.
 * This command is only accessible when the application is currently unlocked.
 */
public class SetupCommand extends Command {

    public static final String COMMAND_WORD = "setup";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Transitions to the setup page. "
            + "This can only be done while the application is unlocked.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Opening Setup Page...";

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        requireNonNull(context);

        return new CommandResult(MESSAGE_SUCCESS, true, false);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof SetupCommand);
    }
}
