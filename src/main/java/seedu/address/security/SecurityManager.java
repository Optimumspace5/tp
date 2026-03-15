package seedu.address.security;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.FileUtil;
import seedu.address.logic.Logic;
import seedu.address.ui.PasswordWindow;

/**
 * Manages the security and authentication state of the application.
 */
public class SecurityManager implements Security {
    private static final Logger logger = LogsCenter.getLogger(SecurityManager.class);
    private static final Path PASSWORD_FILE_PATH = Paths.get("data", "password.txt");

    private final Logic logic;

    /**
     * Constructs a {@code SecurityManager} with the given {@code Logic}.
     */
    public SecurityManager(Logic logic) {
        this.logic = logic;
    }

    @Override
    public boolean isAuthenticated() {
        if (FileUtil.isFileExists(PASSWORD_FILE_PATH)) {
            return true;
        }
        return showPasswordSetupDialog();
    }

    private boolean showPasswordSetupDialog() {
        PasswordWindow passwordWindow = new PasswordWindow(logic.getGuiSettings());
        passwordWindow.show();

        Optional<String> result = passwordWindow.getPassword();
        if (result.isPresent()) {
            try {
                FileUtil.createParentDirsOfFile(PASSWORD_FILE_PATH);
                FileUtil.writeToFile(PASSWORD_FILE_PATH, result.get());
                return true;
            } catch (IOException e) {
                logger.severe("Could not save password file: " + e.getMessage());
            }
        }
        return false;
    }
}