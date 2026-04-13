package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    // OR Logic Tests

    @Test
    public void test_oneKeywordMatches_returnsTrue() {
        // Match Name only
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Match Phone only
        predicate = new NameContainsKeywordsPredicate(List.of("91234567"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("91234567").build()));

        // Match Email only
        predicate = new NameContainsKeywordsPredicate(List.of("alice@example.com"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Match Address only
        predicate = new NameContainsKeywordsPredicate(List.of("Clementi"));
        assertTrue(predicate.test(new PersonBuilder().withAddress("Clementi Ave 3").build()));

        // Match Tag only
        predicate = new NameContainsKeywordsPredicate(List.of("friends"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
    }

    @Test
    public void test_multipleKeywordsAnyMatch_returnsTrue() {
        // Multiple keywords, only one matches (Name)
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays
                .asList("Alice", "NonExistent"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords, only one matches (Phone)
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("NonExistent", "91234567"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("91234567").build()));

        // Multiple keywords, each matching a different field
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice", "91234567"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withPhone("91234567").build()));
    }

    // Case Sensitivity & Formatting Tests

    @Test
    public void test_caseInsensitive_returnsTrue() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List
                .of("aLIce", "eMAil", "fRIEnds"));

        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
        assertTrue(predicate.test(new PersonBuilder().withEmail("EMAIL@example.com").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("Friends").build()));
    }

    // Negative Tests

    @Test
    public void test_noMatch_returnsFalse() {
        // Keywords provided but no field matches
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("Carol", "88888888"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345").build()));

        // Empty keywords list
        predicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Keywords match substring but not whole word
        predicate = new NameContainsKeywordsPredicate(List.of("Al"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    // Duplicate & Overlap Tests

    @Test
    public void test_keywordMatchesMultipleFields_returnsTrue() {
        // Keyword "Alex" exists in both Name and Tag
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alex"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alex").withTags("Alex").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
