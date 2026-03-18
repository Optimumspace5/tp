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
import seedu.address.logic.commands.UnlockCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Registers and parses commands with mode-based authorization.
 */
public class CommandRegistry {

    /**
     * Factory for creating a command object.
     */
    @FunctionalInterface
    private interface CommandFactory {
        /**
         * Creates a command object from the given arguments.
         *
         * @param arguments the command arguments
         * @return the created command
         * @throws ParseException if the arguments are invalid
         */
        Command create(String arguments) throws ParseException;
    }

    /**
     * Stores a command's factory and mode availability flags.
     */
    private static class CommandRegistration {
        private final CommandFactory factory;
        private final boolean allowedInLocked;
        private final boolean allowedInUnlocked;

        private CommandRegistration(CommandFactory factory, boolean allowedInLocked, boolean allowedInUnlocked) {
            this.factory = factory;
            this.allowedInLocked = allowedInLocked;
            this.allowedInUnlocked = allowedInUnlocked;
        }

        private boolean isAllowed(AppMode mode) {
            return mode == AppMode.LOCKED ? allowedInLocked : allowedInUnlocked;
        }
    }

    private final Map<String, CommandRegistration> registrations = new HashMap<>();

    /**
     * Constructs a CommandRegistry and registers all available commands.
     */
    public CommandRegistry() {
        // Core CRUD and query commands.
        register(AddCommand.COMMAND_WORD,
                arguments -> new AddCommandParser().parse(arguments),
                true, true);
        register(EditCommand.COMMAND_WORD,
                arguments -> new EditCommandParser().parse(arguments),
                true, true);
        register(DeleteCommand.COMMAND_WORD,
                arguments -> new DeleteCommandParser().parse(arguments),
                true, true);
        register(ClearCommand.COMMAND_WORD,
                arguments -> new ClearCommand(),
                true, true);
        register(FindCommand.COMMAND_WORD,
                arguments -> new FindCommandParser().parse(arguments),
                true, true);
        register(ListCommand.COMMAND_WORD,
                arguments -> new ListCommand(),
                true, true);

        // Utility commands.
        register(ExitCommand.COMMAND_WORD,
                arguments -> new ExitCommand(),
                true, true);
        register(HelpCommand.COMMAND_WORD,
                arguments -> new HelpCommand(),
                true, true);

        // Mode transition commands.
        register(LockCommand.COMMAND_WORD,
                arguments -> new LockCommand(),
                false, true);
        register(UnlockCommand.COMMAND_WORD,
                arguments -> new UnlockCommand(),
                true, true);
    }

    /**
     * Parses a command word and arguments into a Command, checking authorization for the given mode.
     *
     * @param commandWord the command word entered by the user
     * @param arguments the arguments string entered by the user
     * @param mode the current application mode
     * @return the command specified by the user
     * @throws ParseException if the command is unknown, disallowed in the current mode,
     *         or if the command parser rejects the arguments
     */
    public Command parse(String commandWord, String arguments, AppMode mode) throws ParseException {
        requireNonNull(commandWord);
        requireNonNull(arguments);
        requireNonNull(mode);

        CommandRegistration registration = registrations.get(commandWord);
        if (registration == null || !registration.isAllowed(mode)) {
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }

        // Delegate to the registered factory to create the command object.
        return registration.factory.create(arguments);
    }

    /**
     * Registers a command in the registry.
     *
     * @param commandWord the command word
     * @param factory the factory that creates the command
     * @param allowedInLocked whether the command is allowed in locked mode
     * @param allowedInUnlocked whether the command is allowed in unlocked mode
     */
    private void register(String commandWord, CommandFactory factory,
            boolean allowedInLocked, boolean allowedInUnlocked) {
        registrations.put(commandWord, new CommandRegistration(factory, allowedInLocked, allowedInUnlocked));
    }
}
