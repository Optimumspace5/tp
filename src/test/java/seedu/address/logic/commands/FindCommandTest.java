package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {

    private static final AppMode TEST_MODE = AppMode.LOCKED;

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        assertEquals(findFirstCommand, findFirstCommand);
        assertEquals(new FindCommand(firstPredicate), findFirstCommand);
        org.junit.jupiter.api.Assertions.assertNotEquals(1, findFirstCommand);
        org.junit.jupiter.api.Assertions.assertNotEquals(null, findFirstCommand);
        org.junit.jupiter.api.Assertions.assertNotEquals(findSecondCommand, findFirstCommand);
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate, TEST_MODE);

        assertCommandSuccess(command, model, TEST_MODE, new CommandResult(expectedMessage), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList(TEST_MODE));
    }

    @Test
    public void execute_multipleKeywordsAcrossFields_multiplePersonsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz 9482224 lydia");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate, TEST_MODE);

        assertCommandSuccess(command, model, TEST_MODE, new CommandResult(expectedMessage), expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList(TEST_MODE));
    }

    @Test
    public void execute_searchByAddressAndTag_multiplePersonsFound() {
        // "Clementi" (Benson's Address), "friends" (Alice, Benson, Daniel's Tag)
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Clementi friends");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate, TEST_MODE);

        assertCommandSuccess(command, model, TEST_MODE, new CommandResult(expectedMessage), expectedModel);
        // Alice (tag), Benson (address/tag), Daniel (tag)
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList(TEST_MODE));
    }

    @Test
    public void execute_emailSearchWithoutDotSplitting_returnsTrue() {
        String expectedMessageAlice = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate predicateAlice = preparePredicate("alice");
        FindCommand commandAlice = new FindCommand(predicateAlice);
        expectedModel.updateFilteredPersonList(predicateAlice, TEST_MODE);

        assertCommandSuccess(commandAlice, model, TEST_MODE, new CommandResult(expectedMessageAlice), expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList(TEST_MODE));

        NameContainsKeywordsPredicate predicateEmail = preparePredicate("example.com");
        FindCommand commandEmail = new FindCommand(predicateEmail);
        expectedModel.updateFilteredPersonList(predicateEmail, TEST_MODE);

        String expectedMessageEmail = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 7);

        assertCommandSuccess(commandEmail, model, TEST_MODE, new CommandResult(expectedMessageEmail), expectedModel);

        assertEquals(7, model.getFilteredPersonList(TEST_MODE).size());
        assertEquals(
                Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE),
                model.getFilteredPersonList(TEST_MODE)
        );
    }

    @Test
    public void execute_unlockedPersonInLockedMode_noPersonFound() {
        Person unlockedBenson = new PersonBuilder(BENSON).withStatus(PersonStatus.SENSITIVE).build();
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(ALICE)
                .withPerson(unlockedBenson)
                .build();

        Model mixedModel = new ModelManager(addressBook, new UserPrefs());
        Model expectedMixedModel = new ModelManager(addressBook, new UserPrefs());

        NameContainsKeywordsPredicate predicate = preparePredicate("Benson");
        FindCommand command = new FindCommand(predicate);
        expectedMixedModel.updateFilteredPersonList(predicate, AppMode.LOCKED);

        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        assertCommandSuccess(command, mixedModel, AppMode.LOCKED,
                new CommandResult(expectedMessage), expectedMixedModel);
        assertEquals(Collections.emptyList(), mixedModel.getFilteredPersonList(AppMode.LOCKED));
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
