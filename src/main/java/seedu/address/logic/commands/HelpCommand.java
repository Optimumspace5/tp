package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.AppMode;
import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows command manuals.\n"
            + "Parameters: [COMMAND]\n"
            + "Example: " + COMMAND_WORD + " add";

    public static final String MESSAGE_UNKNOWN_MANUAL = "No command '%1$s'.";
    public static final String GENERAL_MANUAL_LOCKED = "Welcome to AddressBook. "
        + "Below are the commands available in this application.\n"
        + "Usage: help [COMMAND] to view more details\n"
        + "Commands: add, edit, delete, clear, find, list, view, exit, help\n"
        + "Example: help help | help add";
    public static final String GENERAL_MANUAL_UNLOCKED = "Welcome to Spyglass. "
        + "Below are the commands available in this application.\n"
        + "Usage: help [COMMAND] to view more details\n"
        + "Commands: add, edit, delete, clear, find, list, view, toggle, lock, unlock, setup, exit, help\n"
        + "Example: help help | help toggle";

    private final String targetCommand;

    public HelpCommand() {
        this(null);
    }

    /**
     * Creates a HelpCommand that targets a specific command manual.
     */
    public HelpCommand(String targetCommand) {
        this.targetCommand = targetCommand;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        requireNonNull(context);
        AppMode appMode = context.getAppMode();

        if (targetCommand == null) {
            return new CommandResult(buildGeneralManual(appMode));
        }

        if (isHiddenInCurrentMode(targetCommand, appMode)) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_MANUAL, targetCommand));
        }

        switch (targetCommand) {
        case AddCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Add a new contact.",
                    "add -n NAME -p PHONE -e EMAIL -a ADDRESS [-t TAG]...",
                    "add -n John Doe -p 98765432 -e jdoe@example.com -a Clementi Ave 2"));
        case EditCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Edit an existing contact by index.",
                    "edit INDEX [-n NAME] [-p PHONE] [-e EMAIL] [-a ADDRESS] [-t TAG]...",
                    "edit 2 -p 91234567 -e johndoe@example.com"));
        case DeleteCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Delete a contact by index.",
                    "delete INDEX",
                    "delete 3"));
        case ClearCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Remove all contacts in the current mode.",
                    "clear",
                    "clear"));
        case FindCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                    "Search contacts by any keyword (name, phone, address, email, tags).",
                    "find KEYWORD [MORE_KEYWORDS]",
                    "find alex 91234567 Geylang"));
        case ListCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "List all contacts in the current mode.",
                    "list",
                    "list"));
        case ViewCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "View a contact's details by index.",
                    "view INDEX",
                    "view 1"));
        case ToggleCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Toggle a contact's status between Public and Sensitive.",
                    "toggle INDEX",
                    "toggle 1"));
        case LockCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Switch to locked mode.",
                    "lock",
                    "lock"));
        case UnlockCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Switch to unlocked mode using your password.",
                    "unlock PASSWORD",
                    "unlock mySecurePassword123"));
        case SetupCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Open password setup flow.",
                    "setup",
                    "setup"));
        case ExitCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Exit the application.",
                    "exit",
                    "exit"));
        case HelpCommand.COMMAND_WORD:
            return new CommandResult(buildManual(
                "Show command manuals.",
                    "help [COMMAND]",
                    "help add\nhelp toggle"));
        default:
            throw new CommandException(String.format(MESSAGE_UNKNOWN_MANUAL, targetCommand));
        }
    }

    private static String buildGeneralManual(AppMode appMode) {
        if (appMode == AppMode.LOCKED) {
            return GENERAL_MANUAL_LOCKED;
        }

        return GENERAL_MANUAL_UNLOCKED;
    }

    private static boolean isHiddenInCurrentMode(String commandWord, AppMode appMode) {
        if (appMode != AppMode.LOCKED) {
            return false;
        }

        return commandWord.equals(LockCommand.COMMAND_WORD)
                || commandWord.equals(UnlockCommand.COMMAND_WORD)
                || commandWord.equals(SetupCommand.COMMAND_WORD)
                || commandWord.equals(ToggleCommand.COMMAND_WORD);
    }

    private static String buildManual(String description, String usage, String examples) {
        return "Purpose: " + description + "\n"
            + "Usage: " + usage + "\n"
            + "Example: " + examples.replace("\n", " | ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof HelpCommand)) {
            return false;
        }

        HelpCommand otherHelpCommand = (HelpCommand) other;
        return Objects.equals(targetCommand, otherHelpCommand.targetCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetCommand);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetCommand", targetCommand)
                .toString();
    }
}
