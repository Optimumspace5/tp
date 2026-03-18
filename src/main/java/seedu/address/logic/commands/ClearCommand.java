package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Current contact list has been cleared!";

    @Override
    public CommandResult execute(CommandContext context) {
        requireNonNull(context);
        Model model = context.getModel();
        model.clearPersons(context.getAppMode());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
