package seedu.address.security;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.security.util.PasswordUtil;
import seedu.address.ui.PasswordWindow;

/**
 * Manages the security and authentication state of the application.
 * The {@code SecurityManager} handles the lifecycle of application access,
 * including initial password setup and persistent authentication state.
 * It coordinates between the UI (password collection) and the model via {@code Logic}.
 */
public class SecurityManager implements Security {

    private static final Logger logger = LogsCenter.getLogger(SecurityManager.class);

    private final Logic logic;
    private final Supplier<Optional<String>> passwordSupplier;

    /**
     * Constructs a {@code SecurityManager} for production use.
     * Initializes a real {@link PasswordWindow} to collect user input.
     *
     * @param logic The logic component used to retrieve GUI settings and password state.
     */
    public SecurityManager(Logic logic) {
        this(logic, () -> {
            PasswordWindow passwordWindow = new PasswordWindow(logic.getGuiSettings());
            passwordWindow.show();
            return passwordWindow.getPassword();
        });
    }

    /**
     * Constructs a {@code SecurityManager} with custom dependencies.
     * This constructor is primarily used for testing to inject simulated password input.
     *
     * @param logic The logic component.
     * @param passwordSupplier A functional interface providing an Optional password string.
     */
    public SecurityManager(Logic logic, Supplier<Optional<String>> passwordSupplier) {
        this.logic = logic;
        this.passwordSupplier = passwordSupplier;
    }

    /**
     * Checks if the application is currently authenticated.
     * If a password exists within the AddressBook JSON, authentication is successful.
     * If not, the first-time password setup dialog is triggered.
     *
     * @return True if a password exists or if setup is completed successfully; false otherwise.
     */
    @Override
    public boolean isAuthenticated() {
        String storedPassword = logic.getAddressBookPassword();

        if (storedPassword != null && !storedPassword.isEmpty()
                && PasswordUtil.isValidPassword(storedPassword)) {

            logger.info("Valid password detected. Proceeding to authentication.");
            return true;
        }

        if (storedPassword != null && !storedPassword.isEmpty()) {
            logger.warning("Invalid password detected in data file. Prompting for reset.");
        } else {
            logger.info("No password found. Starting first-time setup.");
        }

        return showPasswordSetupDialog();
    }

    /**
     * Orchestrates the password setup process.
     * It retrieves the raw password from the {@code passwordSupplier} and attempts to
     * update the model.
     *
     * @return True if a password was successfully provided and saved; false otherwise.
     */
    private boolean showPasswordSetupDialog() {
        Optional<String> result = passwordSupplier.get();
        if (result.isPresent()) {
            return savePassword(result.get());
        }

        logger.warning("Security setup aborted: User closed the setup window.");
        return false;
    }

    /**
     * Saves the provided raw password to the model via logic.
     *
     * @param rawPassword The plain text password entered by the user.
     * @return True if the password was valid and accepted; false otherwise.
     */
    private boolean savePassword(String rawPassword) {
        if (!PasswordUtil.isValidPassword(rawPassword)) {
            return false;
        }

        logic.setAddressBookPassword(rawPassword);

        try {
            logic.saveAddressBook();
            logger.info("Security setup complete: Password saved to data file.");
        } catch (IOException e) {
            logger.severe("Failed to save address book after password update.");
        }

        return true;
    }
}
