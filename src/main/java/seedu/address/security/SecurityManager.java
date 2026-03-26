package seedu.address.security;

import java.io.IOException;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.security.util.PasswordUtil;

/**
 * Manages the security and authentication state of the application.
 * The {@code SecurityManager} handles the lifecycle of application access,
 * including persistent authentication state and password updates.
 * It coordinates between the UI components and the data model via {@code Logic}.
 */
public class SecurityManager implements Security {

    private static final Logger logger = LogsCenter.getLogger(SecurityManager.class);

    private final Logic logic;

    /**
     * Constructs a {@code SecurityManager} with custom dependencies.
     * This constructor is primarily used for testing or specialized password retrieval.
     *
     * @param logic The logic component.
     */
    public SecurityManager(Logic logic) {
        this.logic = logic;
    }

    /**
     * Checks if the application requires initial password setup.
     * This logic determines if the user should be forced into the setup view.
     *
     * @return False if no password exists or the stored password is invalid; true otherwise.
     */
    @Override
    public boolean isAuthenticated() {
        String storedPassword = logic.getAddressBookPassword();

        if (storedPassword == null) {
            logger.info("No password found in storage. Setup required.");
            return false;
        }

        if (PasswordUtil.isStrictlyValid(storedPassword)) {
            return true;
        }

        String trimmed = storedPassword.trim();
        if (PasswordUtil.isValidPassword(trimmed)) {
            logger.warning("Whitespace detected in stored password. Repairing data file...");
            try {
                savePassword(trimmed);
                return true;
            } catch (Exception e) {
                logger.severe("Auto-repair failed: " + e.getMessage());
            }
        }

        logger.warning("Password is unrecoverable. Setup required.");
        return false;
    }

    /**
     * Validates and saves the provided raw password to the model via logic.
     *
     * @param password The plain text password entered by the user.
     */
    @Override
    public void savePassword(String password) {
        if (!PasswordUtil.isValidPassword(password)) {
            logger.warning("Attempted to save an invalid password.");
            throw new IllegalArgumentException("Invalid password format.");
        }

        try {
            logic.setAddressBookPassword(password);
            logic.saveAddressBook();
            logger.info("Password saved to data file.");

        } catch (IOException e) {
            logger.severe("Failed to save address book after password update.");
            throw new RuntimeException("Data storage failure during setup", e);
        }
    }
}
