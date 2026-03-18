package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.address.logic.AppMode;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Switches the application from a locked state to an unlocked state.
 * Access is granted only if the user provides a password that matches the one stored in the model.
 */
public class UnlockCommand extends Command {
    public static final String COMMAND_WORD = "unlock";
    public static final String MESSAGE_SUCCESS = "Switched to Unlocked Interface.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unlocks the application using the password.\n"
            + "Example: " + COMMAND_WORD + " myPassword123";
    public static final String MESSAGE_ALREADY_UNLOCKED = "The application is already unlocked.\n"
            + MESSAGE_USAGE;

    private final String providedPassword;

    /**
     * Constructs an {@code UnlockCommand} with the provided password.
     *
     * @param password The raw password string entered by the user.
     */
    public UnlockCommand(String password) {
        requireNonNull(password);
        this.providedPassword = password;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        requireNonNull(context);
        Model model = context.getModel();
        AppMode currentMode = context.getAppMode();

        // Check if the application is already unlocked
        if (currentMode == AppMode.UNLOCKED) {
            throw new CommandException(MESSAGE_ALREADY_UNLOCKED);
        }

        // Validate password against the model.
        // If incorrect, return a generic unknown command error.
        if (!model.getAddressBookPassword().equals(providedPassword)) {
            throw new CommandException(MESSAGE_UNKNOWN_COMMAND);
        }

        return new CommandResult(MESSAGE_SUCCESS, false, false, AppMode.UNLOCKED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnlockCommand)) {
            return false;
        }

        UnlockCommand otherUnlockCommand = (UnlockCommand) other;
        return providedPassword.equals(otherUnlockCommand.providedPassword);
    }

    @Override
    public int hashCode() {
        return providedPassword.hashCode();
    }
}
