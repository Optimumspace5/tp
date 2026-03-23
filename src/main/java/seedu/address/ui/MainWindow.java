package seedu.address.ui;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private CommandHistory commandHistory;
    private HelpWindow helpWindow;
    private CommandBox commandBox;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane commandHistoryPlaceholder;

    @FXML
    private StackPane summaryPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     *
     * @param primaryStage The primary stage of the application.
     * @param logic The logic component of the application.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        helpWindow = new HelpWindow();
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return The primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets the accelerator of a MenuItem and installs a filter to ensure accelerators
     * work even when focus is within a TextInputControl.
     *
     * @param menuItem the MenuItem to set the accelerator for.
     * @param keyCombination the KeyCombination value of the accelerator.
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

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
        refreshPersonListPanel();

        commandHistory = new CommandHistory();
        commandHistoryPlaceholder.getChildren().add(commandHistory.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        updateUi(logic.getCurrentMode());

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
            if (!personListPanel.isAnySelected()) {
                personListPanel.selectLast();
            } else {
                personListPanel.selectPrevious();
            }
        } else {
            if (!personListPanel.isAnySelected()) {
                personListPanel.selectFirst();
            } else {
                personListPanel.selectNext();
            }
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

    /**
     * Re-initializes the PersonListPanel with current data.
     */
    private void refreshPersonListPanel() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().setAll(personListPanel.getRoot());
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Updates the UI (Title and List) according to the current application mode.
     *
     * @param mode The current {@code AppMode}.
     */
    private void updateUi(AppMode mode) {
        boolean isLocked = mode == AppMode.LOCKED;
        primaryStage.setTitle(isLocked ? "AddressBook" : "Spyglass");
        refreshPersonListPanel();
    }

    /**
     * Displays the main window.
     */
    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application and saves the current GUI settings.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    /**
     * Returns the PersonListPanel component.
     *
     * @return The PersonListPanel.
     */
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
            commandResult.getRequestedMode().ifPresent(mode -> {
                commandHistory.clear();
                updateUi(mode);
            });

            logger.info("Result: " + commandResult.getFeedbackToUser());
            commandHistory.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            commandHistory.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
