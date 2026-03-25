package seedu.address.model.person;

/**
 * Represents the visibility status of a person entry.
 */
public enum PersonStatus {
    LOCKED(0),
    UNLOCKED(1);

    public static final String MESSAGE_CONSTRAINTS = "Person status must be 0 (locked) or 1 (unlocked).";

    private final int code;

    PersonStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * Returns the {@code PersonStatus} for the given code.
     *
     * @throws IllegalArgumentException if the code is invalid.
     */
    public static PersonStatus fromCode(int code) {
        switch (code) {
        case 0:
            return LOCKED;
        case 1:
            return UNLOCKED;
        default:
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
    }
}
