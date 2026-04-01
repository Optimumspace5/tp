package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;

/**
 * Toggles the status of a person identified using its displayed index from the address book.
 * This command is intended to be available only in unlocked mode.
 */
public class ToggleCommand extends Command {

    public static final String COMMAND_WORD = "toggle";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Toggles the status of the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_TOGGLE_PERSON_SUCCESS = "Updated %1$s to %2$s contact.";

    private final Index targetIndex;

    public ToggleCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        requireNonNull(context);
        Model model = context.getModel();
        List<Person> lastShownList = model.getFilteredPersonList(context.getAppMode());

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToToggle = lastShownList.get(targetIndex.getZeroBased());
        Person toggledPerson = createToggledPerson(personToToggle);

        model.setPerson(personToToggle, toggledPerson, context.getAppMode());
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, context.getAppMode());

        return new CommandResult(String.format(MESSAGE_TOGGLE_PERSON_SUCCESS,
                toggledPerson.getName(), toggledPerson.getStatus()));
    }

    private static Person createToggledPerson(Person person) {
        PersonStatus toggledStatus = person.getStatus() == PersonStatus.LOCKED
                ? PersonStatus.UNLOCKED
                : PersonStatus.LOCKED;

        return new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), toggledStatus);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ToggleCommand)) {
            return false;
        }

        ToggleCommand otherToggleCommand = (ToggleCommand) other;
        return targetIndex.equals(otherToggleCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
