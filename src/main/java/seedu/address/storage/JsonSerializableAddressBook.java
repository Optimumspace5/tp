package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonStatus;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON =
            "Contact list contains duplicate person(s).";

    private final String password;
    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    @JsonIgnore
    private final List<JsonAdaptedPerson> lockedPersons = new ArrayList<>();

    @JsonIgnore
    private final List<JsonAdaptedPerson> unlockedPersons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} from either the new combined format
     * or the legacy locked/unlocked format.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
                                       @JsonProperty("lockedPersons") List<JsonAdaptedPerson> lockedPersons,
                                       @JsonProperty("unlockedPersons") List<JsonAdaptedPerson> unlockedPersons,
                                       @JsonProperty("password") String password) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (lockedPersons != null) {
            this.lockedPersons.addAll(lockedPersons);
        }
        if (unlockedPersons != null) {
            this.unlockedPersons.addAll(unlockedPersons);
        }
        this.password = (password != null) ? password : "";
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        this.password = source.getPassword();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        addressBook.setPassword(password == null ? "" : password);

        if (!persons.isEmpty()) {
            for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
                Person person = jsonAdaptedPerson.toModelType(PersonStatus.LOCKED);
                if (addressBook.hasPerson(person)) {
                    throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
                }
                addressBook.addPerson(person);
            }
            return addressBook;
        }

        List<Person> migratedPersons = new ArrayList<>();

        for (JsonAdaptedPerson jsonAdaptedPerson : lockedPersons) {
            Person person = jsonAdaptedPerson.toModelType(PersonStatus.LOCKED);
            if (findSamePerson(migratedPersons, person) != null) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            migratedPersons.add(person);
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : unlockedPersons) {
            Person person = jsonAdaptedPerson.toModelType(PersonStatus.UNLOCKED);
            Person existingPerson = findSamePerson(migratedPersons, person);

            if (existingPerson == null) {
                migratedPersons.add(person);
            } else if (existingPerson.getStatus() == PersonStatus.LOCKED) {
                migratedPersons.remove(existingPerson);
                migratedPersons.add(person);
            } else {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
        }

        addressBook.setPersons(migratedPersons);
        return addressBook;
    }

    private Person findSamePerson(List<Person> persons, Person target) {
        return persons.stream()
                .filter(existingPerson -> existingPerson.isSamePerson(target))
                .findFirst()
                .orElse(null);
    }
}
