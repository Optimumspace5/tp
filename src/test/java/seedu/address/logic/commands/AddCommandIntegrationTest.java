package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
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
    public void execute_duplicatePersonInDifferentMode_replacesExistingPerson() {
        Person existingPerson = model.getAddressBook().getPersonList().get(0);
        Person personToAdd = new PersonBuilder(existingPerson).build();
        Person expectedPerson = new PersonBuilder(existingPerson).withStatus(PersonStatus.UNLOCKED).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(existingPerson, AppMode.UNLOCKED);
        expectedModel.addPerson(expectedPerson, AppMode.UNLOCKED);

        String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson));
        assertCommandSuccess(new AddCommand(personToAdd), model, AppMode.UNLOCKED,
                new CommandResult(expectedMessage), expectedModel);
    }
}
