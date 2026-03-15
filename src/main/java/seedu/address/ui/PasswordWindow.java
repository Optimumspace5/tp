package seedu.address.ui;

import java.util.Optional;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for the password setup window.
 */
public class PasswordWindow extends UiPart<Stage> {
    private static final String FXML = "PasswordWindow.fxml";
    private final Logger logger = LogsCenter.getLogger(getClass());

    private String passwordEntered = null;

    @FXML
    private TextField passwordInput;

    @FXML
    private Label errorMessage;

    /**
     * Creates a new PasswordWindow with specified settings.
     */
    public PasswordWindow(GuiSettings guiSettings) {
        this(new Stage(), guiSettings);
    }

    /**
     * Creates a new PasswordWindow using the given Stage as the root.
     */
    public PasswordWindow(Stage root, GuiSettings guiSettings) {
        super(FXML, root);
        setWindowDefaultSize(root, guiSettings);

        root.setMinWidth(450);
        root.setMinHeight(400);
    }

    private void setWindowDefaultSize(Stage stage, GuiSettings guiSettings) {
        stage.setHeight(guiSettings.getWindowHeight());
        stage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            stage.setX(guiSettings.getWindowCoordinates().getX());
            stage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    public void show() {
        logger.fine("Showing password setup window.");
        getRoot().showAndWait();
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(passwordEntered);
    }

    @FXML
    private void handleLogin() {
        String input = passwordInput.getText();
        if (input == null || input.trim().isEmpty()) {
            errorMessage.setText("Password cannot be empty!");
            passwordInput.getStyleClass().add("error");
        } else {
            passwordEntered = input;
            getRoot().close();
        }
    }
}