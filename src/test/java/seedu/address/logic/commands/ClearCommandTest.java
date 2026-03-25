package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

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

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBookLockedMode_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_mixedAddressBookLockedMode_clearsOnlyLockedPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(ALICE)
                .withPerson(unlockedBenson)
                .build();

        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager(addressBook, new UserPrefs());
        expectedModel.clearPersons(AppMode.LOCKED);

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_mixedAddressBookUnlockedMode_clearsAllPersons() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.UNLOCKED).build();
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(ALICE)
                .withPerson(unlockedBenson)
                .build();

        Model model = new ModelManager(addressBook, new UserPrefs());
        Model expectedModel = new ModelManager(addressBook, new UserPrefs());
        expectedModel.clearPersons(AppMode.UNLOCKED);

        assertCommandSuccess(new ClearCommand(), model, AppMode.UNLOCKED,
                new CommandResult(ClearCommand.MESSAGE_SUCCESS), expectedModel);
    }
}
