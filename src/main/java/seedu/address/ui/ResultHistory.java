package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A ui for the result history that is displayed at the top left of the application.
 */
public class ResultHistory extends UiPart<Region> {

    private static final int MAX_ENTRIES = 200;
    private static final String FXML = "ResultHistory.fxml";

    // Non-persistent memory for the result history
    private static ArrayList<String> log = new ArrayList<>();

    @FXML
    private TextArea resultHistory;

    /**
     * Creates a {@code ResultHistory} with the default FXML.
     */
    public ResultHistory() {
        super(FXML);
        resultHistory.setWrapText(true);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        appendEntry(formatFeedbackEntry(feedbackToUser));
    }

    public void setFeedbackToUser(String commandText, String feedbackToUser) {
        requireNonNull(commandText);
        requireNonNull(feedbackToUser);
        appendEntry(formatCommandResultEntry(commandText, feedbackToUser));
    }

    static String formatFeedbackEntry(String feedbackToUser) {
        return "> " + feedbackToUser;
    }

    static String formatCommandResultEntry(String commandText, String feedbackToUser) {
        return "> " + commandText + "\n" + feedbackToUser;
    }

    private void appendEntry(String entry) {
        log.add(entry);
        log = new ArrayList<>(capEntries(log, MAX_ENTRIES));
        resultHistory.setText(String.join("\n\n", log));
    }

    static List<String> capEntries(List<String> entries, int maxEntries) {
        if (entries.size() <= maxEntries) {
            return new ArrayList<>(entries);
        }

        return new ArrayList<>(entries.subList(entries.size() - maxEntries, entries.size()));
    }

    /**
     * Clears the in-memory and displayed result history.
     */
    public void clear() {
        log.clear();
        resultHistory.clear();
    }

}
