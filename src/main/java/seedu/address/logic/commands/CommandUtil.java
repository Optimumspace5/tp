package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.logic.AppMode;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;

/**
 * Shared helper methods for command duplicate-handling logic.
 */
public final class CommandUtil {

    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private CommandUtil() {

    }

    /**
     * Resolves duplicate handling for add/edit flows.
     * If a conflicting person exists, either delete it when override is allowed
     * or throw a {@code CommandException} otherwise.
     *
     * @param personToIgnore the person to exclude from duplicate checks, used by edit flows
     */
    public static void resolveDuplicateConflict(Model model, Person personToProcess,
            AppMode appMode, Person personToIgnore) throws CommandException {
        requireAllNonNull(model, personToProcess, appMode);

        Person personToOverride = findDuplicatePerson(model, personToProcess, personToIgnore);

        if (personToOverride == null) {
            return;
        }

        if (!canOverrideExisting(appMode, personToOverride)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.deletePerson(personToOverride, appMode);
    }

    private static Person findDuplicatePerson(Model model, Person personToProcess, Person personToIgnore) {
        return model.getPersonList().stream()
                .filter(person -> !person.equals(personToIgnore) && person.isSamePerson(personToProcess))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns whether the conflicting existing person can be overridden in the current app mode.
     */
    public static boolean canOverrideExisting(AppMode appMode, Person personToOverride) {
        requireAllNonNull(appMode, personToOverride);
        return appMode == AppMode.LOCKED && personToOverride.getStatus() == PersonStatus.UNLOCKED;
    }
}
