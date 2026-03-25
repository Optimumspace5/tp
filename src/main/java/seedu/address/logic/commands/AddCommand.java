package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_PHONE + " PHONE "
            + PREFIX_EMAIL + " EMAIL "
            + PREFIX_ADDRESS + " ADDRESS "
            + "[" + PREFIX_TAG + " TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + " John Doe "
            + PREFIX_PHONE + " 98765432 "
            + PREFIX_EMAIL + " johnd@example.com "
            + PREFIX_ADDRESS + " 311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + " friends "
            + PREFIX_TAG + " owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";

    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Address address;
    private final Set<Tag> tags;

    /**
     * Creates an AddCommand to add the specified {@code Person}.
     */
    public AddCommand(Person person) {
        this(requireNonNull(person).getName(),
                person.getPhone(),
                person.getEmail(),
                person.getAddress(),
                person.getTags());
    }

    /**
     * Creates an AddCommand from parsed person fields.
     */
    public AddCommand(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags = new HashSet<>(tags);
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        requireNonNull(context);
        Model model = context.getModel();

        Person personToAdd = createPersonForMode(context.getAppMode());
        Person personToOverride = findSamePerson(model, personToAdd);

        if (personToOverride != null) {
            model.deletePerson(personToOverride, context.getAppMode());
        }

        model.addPerson(personToAdd, context.getAppMode());
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(personToAdd)));
    }

    private Person createPersonForMode(AppMode appMode) {
        return new Person(name, phone, email, address, tags, getStatusForMode(appMode));
    }

    private static PersonStatus getStatusForMode(AppMode appMode) {
        return appMode == AppMode.LOCKED ? PersonStatus.LOCKED : PersonStatus.UNLOCKED;
    }

    private static Person findSamePerson(Model model, Person target) {
        return model.getPersonList().stream()
                .filter(target::isSamePerson)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return name.equals(otherAddCommand.name)
                && phone.equals(otherAddCommand.phone)
                && email.equals(otherAddCommand.email)
                && address.equals(otherAddCommand.address)
                && tags.equals(otherAddCommand.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }
}
