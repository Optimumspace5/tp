package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ToggleCommand}.
 */
public class ToggleCommandTest {

    private static final AppMode TEST_MODE = AppMode.UNLOCKED;

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_toggleLockedPersonUnfilteredList_success() {
        Person personToToggle = model.getFilteredPersonList(TEST_MODE).get(INDEX_FIRST_PERSON.getZeroBased());
        Person toggledPerson = new PersonBuilder(personToToggle).withStatus(PersonStatus.SENSITIVE).build();
        ToggleCommand toggleCommand = new ToggleCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ToggleCommand.MESSAGE_TOGGLE_PERSON_SUCCESS,
                toggledPerson.getName(), toggledPerson.getStatus());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToToggle, toggledPerson, TEST_MODE);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, TEST_MODE);

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, INDEX_FIRST_PERSON);
        assertCommandSuccess(toggleCommand, model, TEST_MODE, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_toggleUnlockedPersonUnfilteredList_success() {
        Person unlockedPerson = new PersonBuilder().withStatus(PersonStatus.SENSITIVE).build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(unlockedPerson);
        Model customModel = new ModelManager(addressBook, new UserPrefs());

        Person toggledPerson = new PersonBuilder(unlockedPerson).withStatus(PersonStatus.PUBLIC).build();
        ToggleCommand toggleCommand = new ToggleCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ToggleCommand.MESSAGE_TOGGLE_PERSON_SUCCESS,
                toggledPerson.getName(), toggledPerson.getStatus());

        Model expectedModel = new ModelManager(customModel.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(unlockedPerson, toggledPerson, TEST_MODE);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, TEST_MODE);

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, INDEX_FIRST_PERSON);
        assertCommandSuccess(toggleCommand, customModel, TEST_MODE,
                expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList(TEST_MODE).size() + 1);
        ToggleCommand toggleCommand = new ToggleCommand(outOfBoundIndex);

        assertCommandFailure(toggleCommand, model, TEST_MODE, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showUnlockedPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToToggle = model.getFilteredPersonList(TEST_MODE).get(INDEX_FIRST_PERSON.getZeroBased());
        Person toggledPerson = new PersonBuilder(personToToggle).withStatus(PersonStatus.SENSITIVE).build();
        ToggleCommand toggleCommand = new ToggleCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ToggleCommand.MESSAGE_TOGGLE_PERSON_SUCCESS,
                toggledPerson.getName(), toggledPerson.getStatus());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToToggle, toggledPerson, TEST_MODE);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS, TEST_MODE);

        CommandResult expectedCommandResult = new CommandResult(expectedMessage, INDEX_FIRST_PERSON);
        assertCommandSuccess(toggleCommand, model, TEST_MODE, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showUnlockedPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ToggleCommand toggleCommand = new ToggleCommand(outOfBoundIndex);
        assertCommandFailure(toggleCommand, model, TEST_MODE, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ToggleCommand toggleFirstCommand = new ToggleCommand(INDEX_FIRST_PERSON);
        ToggleCommand toggleSecondCommand = new ToggleCommand(INDEX_SECOND_PERSON);

        assertTrue(toggleFirstCommand.equals(toggleFirstCommand));
        ToggleCommand toggleFirstCommandCopy = new ToggleCommand(INDEX_FIRST_PERSON);
        assertTrue(toggleFirstCommand.equals(toggleFirstCommandCopy));
        assertFalse(toggleFirstCommand.equals(1));
        assertFalse(toggleFirstCommand.equals(null));
        assertFalse(toggleFirstCommand.equals(toggleSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ToggleCommand toggleCommand = new ToggleCommand(targetIndex);
        String expected = ToggleCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, toggleCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show only the person at the given {@code targetIndex}
     * in the unlocked filtered person list.
     */
    private void showUnlockedPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredPersonList(TEST_MODE).size());

        Person person = model.getFilteredPersonList(TEST_MODE).get(targetIndex.getZeroBased());
        final String[] splitName = person.getName().fullName.split("\\s+");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])), TEST_MODE);

        assertEquals(1, model.getFilteredPersonList(TEST_MODE).size());
    }
}
