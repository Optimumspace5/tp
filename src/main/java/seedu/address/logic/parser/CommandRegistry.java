package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.HashMap;
import java.util.Map;

import seedu.address.logic.AppMode;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.LockCommand;
import seedu.address.logic.commands.SetupCommand;
import seedu.address.logic.commands.ToggleCommand;
import seedu.address.logic.commands.UnlockCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Registers and parses commands with mode-based authorization.
 */
public class CommandRegistry {

    /**
     * Functional interface for creating a command object from raw arguments.
     */
    @FunctionalInterface
    private interface CommandFactory {
        /**
         * Creates a command object from the given arguments.
         *
         * @param arguments The raw command arguments entered by the user.
         * @return The created command.
         * @throws ParseException If the arguments are invalid for the specific command.
         */
        Command create(String arguments) throws ParseException;
    }

    /**
     * Encapsulates a command's factory and its permission flags for different application modes.
     */
    private static class CommandRegistration {
        private final CommandFactory factory;
        private final boolean allowedInLocked;
        private final boolean allowedInUnlocked;

        /**
         * Constructs a registration entry for a command.
         *
         * @param factory The factory used to instantiate the command.
         * @param allowedInLocked Whether the command is permitted in {@code AppMode.LOCKED}.
         * @param allowedInUnlocked Whether the command is permitted in {@code AppMode.UNLOCKED}.
         */
        private CommandRegistration(CommandFactory factory, boolean allowedInLocked, boolean allowedInUnlocked) {
            this.factory = factory;
            this.allowedInLocked = allowedInLocked;
            this.allowedInUnlocked = allowedInUnlocked;
        }

        /**
         * Checks if the command is permitted in the specified application mode.
         *
         * @param mode The current application mode to check against.
         * @return True if the command is allowed, false otherwise.
         */
        private boolean isAllowed(AppMode mode) {
            return mode == AppMode.LOCKED ? allowedInLocked : allowedInUnlocked;
        }
    }

    private final Map<String, CommandRegistration> registrations = new HashMap<>();

    /**
     * Initializes the {@code CommandRegistry} and registers all available system commands
     * with their respective authorization settings.
     */
    public CommandRegistry() {
        // CRUD Commands
        register(AddCommand.COMMAND_WORD, args -> new AddCommandParser().parse(args), true, true);
        register(EditCommand.COMMAND_WORD, args -> new EditCommandParser().parse(args), true, true);
        register(DeleteCommand.COMMAND_WORD, args -> new DeleteCommandParser().parse(args), true, true);
        register(ClearCommand.COMMAND_WORD, args -> new ClearCommand(), true, true);

        // Query Commands
        register(FindCommand.COMMAND_WORD, args -> new FindCommandParser().parse(args), true, true);
        register(ListCommand.COMMAND_WORD, args -> new ListCommand(), true, true);
        register(ViewCommand.COMMAND_WORD, args -> new ViewCommandParser().parse(args), true, true);

        // Utility Commands
        register(ExitCommand.COMMAND_WORD, args -> new ExitCommand(), true, true);
        register(HelpCommand.COMMAND_WORD, args -> new HelpCommandParser().parse(args), true, true);
        register(SetupCommand.COMMAND_WORD, args -> new SetupCommand(), false, true);
        register(ToggleCommand.COMMAND_WORD, args -> new ToggleCommandParser().parse(args), false, true);

        // Mode Transition Commands
        register(LockCommand.COMMAND_WORD, args -> new LockCommand(), false, true);
        register(UnlockCommand.COMMAND_WORD, args -> new UnlockCommandParser().parse(args), true, true);
    }

    /**
     * Parses a command word and arguments into a {@code Command} object, checking
     * authorization against the provided {@code AppMode}.
     *
     * @param commandWord The command word entered by the user.
     * @param arguments The arguments string entered by the user.
     * @param mode The current application mode.
     * @return The instantiated command if authorized and valid.
     * @throws ParseException If the command is unknown, disallowed in the current mode,
     *                        or if the parser rejects the arguments.
     */
    public Command parse(String commandWord, String arguments, AppMode mode) throws ParseException {
        requireNonNull(commandWord);
        requireNonNull(arguments);
        requireNonNull(mode);

        CommandRegistration registration = registrations.get(commandWord);

        // If the command is not registered or not allowed in the current mode,
        // we throw a generic unknown command error.
        if (registration == null || !registration.isAllowed(mode)) {
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }

        return registration.factory.create(arguments);
    }

    /**
     * Internally registers a command in the registry.
     *
     * @param commandWord The command word that triggers this command.
     * @param factory The factory logic to create the command.
     * @param allowedInLocked Whether the command can be used while the app is locked.
     * @param allowedInUnlocked Whether the command can be used while the app is unlocked.
     */
    private void register(String commandWord, CommandFactory factory,
                          boolean allowedInLocked, boolean allowedInUnlocked) {
        registrations.put(commandWord, new CommandRegistration(factory, allowedInLocked, allowedInUnlocked));
    }
}
