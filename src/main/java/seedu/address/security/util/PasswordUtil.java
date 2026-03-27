package seedu.address.security.util;

/**
 * Utility methods for password validation and security requirements.
 * This class provides static methods to ensure passwords meet application policies,
 * distinguishing between user input and strict data storage formats.
 */
public class PasswordUtil {

    public static final String MESSAGE_EMPTY = "Password cannot be empty!";
    public static final String MESSAGE_NO_SPACES = "Password must not contain spaces!";
    public static final String MESSAGE_ONLY_ASCII = "Password must only have "
            + "alphanumeric characters and symbols!";

    /**
     * Checks if the password is valid, allowing for leading or trailing whitespace.
     * This method is intended for UI input where the application can be forgiving by
     * auto-trimming the user's entry.
     *
     * @param password The raw password string to validate.
     * @return True if the trimmed password meets all requirements; false otherwise.
     */
    public static boolean isValidPassword(String password) {
        return getValidationError(password) == null;
    }

    /**
     * Checks if the password is valid and contains no leading or trailing whitespace.
     * This method is intended for validating data directly from storage (e.g., JSON files)
     * to ensure data integrity and detect formatting errors.
     *
     * @param password The stored password string to check.
     * @return True if the password is valid and requires no trimming; false otherwise.
     */
    public static boolean isStrictlyValid(String password) {
        if (password == null) {
            return false;
        }
        boolean hasOuterWhitespace = !password.equals(password.trim());
        return !hasOuterWhitespace && isValidPassword(password);
    }

    /**
     * Returns a specific error message describing why a password is invalid.
     * The validation logic ignores leading and trailing whitespace to determine if the
     * core password content is acceptable.
     *
     * @param password The password string to evaluate.
     * @return A {@code String} constant representing the error, or null if the password is valid.
     */
    public static String getValidationError(String password) {
        if (password == null || password.trim().isEmpty()) {
            return MESSAGE_EMPTY;
        }

        String trimmed = password.trim();

        if (trimmed.contains(" ")) {
            return MESSAGE_NO_SPACES;
        }

        if (!trimmed.matches("^[\\x20-\\x7E]*$")) {
            return MESSAGE_ONLY_ASCII;
        }

        return null;
    }
}
