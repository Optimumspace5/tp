package seedu.address.ui;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.AppMode;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.security.Security;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Security security;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultHistory resultHistory;
    private PersonDetailPanel personDetailPanel;
    private CommandBox commandBox;
    private SetupPanel setupPanel;

    // Caches the dashboard layout to allow switching back after setup
    private Parent dashboardRoot;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultHistoryPlaceholder;

    @FXML
    private StackPane personDetailPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic} and {@code Security}.
     *
     * @param primaryStage The primary stage of the application.
     * @param logic The logic component of the application.
     * @param security The security component of the application.
     */
    public MainWindow(Stage primaryStage, Logic logic, Security security) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.security = security;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param menuItem the MenuItem to set the accelerator for.
     * @param keyCombination the KeyCombination value of the accelerator.
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultHistory contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultHistory.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window and configures custom focus traversal logic.
     */
    void fillInnerParts() {
        // personDetailPlaceholder is empty by default until a person is selected
        personDetailPanel = new PersonDetailPanel();
        personDetailPlaceholder.getChildren().add(personDetailPanel.getRoot());

        refreshPersonListPanel();

        resultHistory = new ResultHistory();
        resultHistoryPlaceholder.getChildren().add(resultHistory.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        updateUi(logic.getCurrentMode());

        if (!security.isAuthenticated()) {
            handleSetup();
        }

        installTabCycleFilter();
    }

    /**
     * Installs a global filter to cycle through person cards while keeping focus in CommandBox.
     */
    private void installTabCycleFilter() {
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                handleTabCycle(event.isShiftDown());
                event.consume();
            }
        });
    }

    /**
     * Coordinates the selection logic between components.
     */
    private void handleTabCycle(boolean isShiftDown) {
        // Ensure CommandBox always in focus
        commandBox.requestFocus();

        if (isShiftDown) {
            if (personListPanel.isAnySelected()) {
                personListPanel.selectLast();
            } else {
                personListPanel.selectPrevious();
            }
        } else {
            if (personListPanel.isAnySelected()) {
                personListPanel.selectFirst();
            } else {
                personListPanel.selectNext();
            }
        }
        // Check the SecurityManager to see if we should jump to setup immediately
        if (!security.isAuthenticated()) {
            handleSetup();
        }
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    private void refreshPersonListPanel() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanel.setOnSelectionChange(person -> {
            if (person == null) {
                personDetailPanel.clearPerson();
            } else {
                personDetailPanel.setPerson(person);
            }
        });
        personListPanelPlaceholder.getChildren().setAll(personListPanel.getRoot());
    }

    /**
     * Updates the UI according to the current mode.
     */
    private void updateUi(AppMode mode) {
        boolean isLocked = mode == AppMode.LOCKED;
        primaryStage.setTitle(isLocked ? "AddressBook" : "Spyglass");
        personDetailPanel.updateMode(mode);
        personDetailPanel.clearPerson();
        refreshPersonListPanel();
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    /**
     * Switches the primary stage root to the setup panel.
     */
    private void handleSetup() {
        logger.info("Transitioning to SetupPanel.");

        if (dashboardRoot == null) {
            dashboardRoot = primaryStage.getScene().getRoot();
        }

        setupPanel = new SetupPanel(this::handlePasswordInput);

        // switch view
        primaryStage.getScene().setRoot(setupPanel.getRoot());
    }

    /**
     * Handles the logic after a password has been entered in the SetupPanel.
     */
    private void handlePasswordInput(String password) {
        try {
            security.savePassword(password);
            primaryStage.getScene().setRoot(dashboardRoot);
            resultHistory.setFeedbackToUser("Setup process completed successfully.");

        } catch (Exception e) {
            logger.warning("Setup failed: " + e.getMessage());
            setupPanel.showError("Critical Error: " + e.getMessage());
        }
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);

            // Handle mode change if requested by the command result
            boolean isModeChangedToUnlocked = commandResult.getRequestedMode().isPresent()
                    && commandResult.getRequestedMode().get() == AppMode.UNLOCKED;

            commandResult.getRequestedMode().ifPresent(mode -> {
                resultHistory.clear();
                assert commandBox != null : "CommandBox should not be null";
                commandBox.clearCommandHistory();
                updateUi(mode);
            });

            // Only show feedback for unlock; lock clears and stays blank
            if (isModeChangedToUnlocked) {
                resultHistory.setFeedbackToUser(commandText, commandResult.getFeedbackToUser());
            } else if (commandResult.getRequestedMode().isEmpty()) {
                resultHistory.setFeedbackToUser(commandText, commandResult.getFeedbackToUser());
            }

            // Handle setup transition
            if (commandResult.isShowSetup()) {
                handleSetup();
            }

            commandResult.getSelectedIndex().ifPresent(personListPanel::select);

            logger.info("Result: " + commandResult.getFeedbackToUser());

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultHistory.setFeedbackToUser(commandText, e.getMessage());
            throw e;
        }
    }
}
