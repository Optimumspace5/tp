package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson, AppMode.LOCKED);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePersonInUnlockedMode_throwsCommandException() {
        Person existingPerson = model.getAddressBook().getPersonList().get(0);
        Person personToAdd = new PersonBuilder(existingPerson).build();

        assertCommandFailure(new AddCommand(personToAdd), model, AppMode.UNLOCKED,
                CommandUtil.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_unlockedDuplicateInLockedMode_replacesExistingPerson() {
        Person existingUnlockedPerson = new PersonBuilder().withStatus(PersonStatus.UNLOCKED).build();
        Person personToAdd = new PersonBuilder(existingUnlockedPerson).build();
        Person expectedPerson = new PersonBuilder(existingUnlockedPerson).withStatus(PersonStatus.LOCKED).build();

        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(existingUnlockedPerson);
        model = new ModelManager(addressBook, new UserPrefs());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(existingUnlockedPerson, AppMode.LOCKED);
        expectedModel.addPerson(expectedPerson, AppMode.LOCKED);

        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson));
        assertCommandSuccess(new AddCommand(personToAdd), model, AppMode.LOCKED,
                new CommandResult(expectedMessage), expectedModel);
    }
}
