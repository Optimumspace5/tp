package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();

        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        typicalPersonsAddressBook.setPassword("12345");

        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_fileMissingPassword_success() throws Exception {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                TypicalPersons.getTypicalPersons().stream()
                        .map(JsonAdaptedPerson::new)
                        .collect(Collectors.toList()),
                new ArrayList<>(),
                new ArrayList<>(),
                null);

        AddressBook addressBook = data.toModelType();

        assertEquals("", addressBook.getPassword());
        assertEquals(TypicalPersons.getTypicalAddressBook().getPersonList(),
                addressBook.getPersonList());
    }

    @Test
    public void toModelType_legacyLockedAndUnlockedSamePerson_unlockedWins() throws Exception {
        Person lockedAlice = new PersonBuilder(TypicalPersons.ALICE)
                .withStatus(PersonStatus.LOCKED)
                .build();
        Person unlockedAlice = new PersonBuilder(TypicalPersons.ALICE)
                .withEmail("alice-unlocked@example.com")
                .withStatus(PersonStatus.UNLOCKED)
                .build();

        List<JsonAdaptedPerson> legacyLockedPersons = new ArrayList<>();
        legacyLockedPersons.add(toLegacyJsonAdaptedPerson(lockedAlice));

        List<JsonAdaptedPerson> legacyUnlockedPersons = new ArrayList<>();
        legacyUnlockedPersons.add(toLegacyJsonAdaptedPerson(unlockedAlice));

        JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                new ArrayList<>(),
                legacyLockedPersons,
                legacyUnlockedPersons,
                "legacyPassword");

        AddressBook addressBook = data.toModelType();

        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.addPerson(unlockedAlice);
        expectedAddressBook.setPassword("legacyPassword");

        assertEquals(expectedAddressBook, addressBook);
    }

    @Test
    public void constructor_sourceAddressBook_copiesPassword() throws IllegalValueException {
        AddressBook source = new AddressBook();
        source.setPassword("secret_pw_123");

        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(source);

        assertEquals("secret_pw_123", serializable.toModelType().getPassword());
    }

    @Test
    public void toModelType_emptyPasswordFile_success() throws Exception {
        Path file = TEST_DATA_FOLDER.resolve("emptyPasswordAddressBook.json");
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(file,
                JsonSerializableAddressBook.class).get();

        AddressBook addressBookFromFile = dataFromFile.toModelType();
        assertEquals("", addressBookFromFile.getPassword());
    }

    @Test
    public void toModelType_nullPassword_setsEmptyString() throws Exception {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null);

        AddressBook addressBook = data.toModelType();

        assertEquals("", addressBook.getPassword());
    }

    private JsonAdaptedPerson toLegacyJsonAdaptedPerson(Person person) {
        List<JsonAdaptedTag> tags = person.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList());

        return new JsonAdaptedPerson(
                person.getName().toString(),
                person.getPhone().toString(),
                person.getEmail().toString(),
                person.getAddress().toString(),
                null,
                tags);
    }
}
