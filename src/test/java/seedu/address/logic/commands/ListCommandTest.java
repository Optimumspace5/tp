package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFilteredInLockedMode_showsOnlyLockedPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(ALICE)
                .withPerson(unlockedBenson)
                .build();

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());

        model.updateFilteredPersonList(person -> false, AppMode.LOCKED);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFilteredInUnlockedMode_showsEverything() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(ALICE)
                .withPerson(unlockedBenson)
                .build();

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());

        model.updateFilteredPersonList(person -> false, AppMode.UNLOCKED);

        assertCommandSuccess(new ListCommand(), model, AppMode.UNLOCKED,
                new CommandResult(ListCommand.MESSAGE_SUCCESS), expectedModel);
    }
}
