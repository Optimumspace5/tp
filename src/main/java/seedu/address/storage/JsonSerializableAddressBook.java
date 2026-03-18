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

    public static final String MESSAGE_DUPLICATE_LOCKED_PERSON =
            "Contact list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_UNLOCKED_PERSON =
            "Contact list contains duplicate person(s).";

    private final String password;
    private final List<JsonAdaptedPerson> lockedPersons = new ArrayList<>();
    private final List<JsonAdaptedPerson> unlockedPersons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given locked persons,
     * unlocked persons, and password.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("lockedPersons") List<JsonAdaptedPerson> lockedPersons,
                                       @JsonProperty("unlockedPersons") List<JsonAdaptedPerson> unlockedPersons,
                                       @JsonProperty("password") String password) {
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
        lockedPersons.addAll(source.getLockedPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        unlockedPersons.addAll(source.getUnlockedPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        this.password = source.getPassword();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in either person list.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        if (password == null) {
            addressBook.setPassword("");
        } else {
            addressBook.setPassword(password);
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : lockedPersons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasLockedPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_LOCKED_PERSON);
            }
            addressBook.addLockedPerson(person);
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : unlockedPersons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasUnlockedPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_UNLOCKED_PERSON);
            }
            addressBook.addUnlockedPerson(person);
        }

        return addressBook;
    }
}
