package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON =
            "Contact list contains duplicate person(s).";
    public static final String MESSAGE_MISSING_PERSONS =
            "Address book persons list is missing!";

    private final String password;
    private final List<JsonAdaptedPerson> persons;

    /**
     * Constructs a {@code JsonSerializableAddressBook} from the combined persons format.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
                                       @JsonProperty("password") String password) {
        this.persons = persons;
        this.password = (password != null) ? password : "";
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        this.persons = new ArrayList<>();
        this.persons.addAll(source.getPersonList().stream()
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
        if (persons == null) {
            throw new IllegalValueException(MESSAGE_MISSING_PERSONS);
        }

        AddressBook addressBook = new AddressBook();
        addressBook.setPassword(password);

        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }

        return addressBook;
    }
}
