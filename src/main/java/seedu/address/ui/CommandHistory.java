package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A ui for the command history that is displayed at the top left of the application.
 */
public class CommandHistory extends UiPart<Region> {

    private static final String FXML = "CommandHistory.fxml";
    // Non-persistent memory for the command history
    private static ArrayList<String> log = new ArrayList<>();

    @FXML
    private TextArea commandHistory;

    /**
     * Creates a {@code CommandHistory} with the default FXML.
     */
    public CommandHistory() {
        super(FXML);
        commandHistory.setWrapText(true);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        log.add(feedbackToUser);

        if (commandHistory.getText().isEmpty()) {
            commandHistory.setText("> " + feedbackToUser);
        } else {
            commandHistory.appendText("\n\n> " + feedbackToUser);
        }
    }

}
