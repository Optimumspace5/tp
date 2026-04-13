package seedu.address.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the history of commands executed.
 */
public class CommandHistory {
    private static final int MAX_ENTRIES = 100;

    private final List<String> history = new ArrayList<>();
    private int currentIndex = 0;

    /**
     * Adds a command to the history.
     */
    public void add(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }

        if (history.size() == MAX_ENTRIES) {
            history.remove(0);
        }

        history.add(command);
        currentIndex = history.size();
    }

    /**
     * Returns the previous command in the history, moving the pointer backwards.
     */
    public String getPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        return currentIndex < history.size() ? history.get(currentIndex) : "";
    }

    /**
     * Returns the next command in the history, moving the pointer forwards.
     */
    public String getNext() {
        if (currentIndex < history.size()) {
            currentIndex++;
        }
        return currentIndex < history.size() ? history.get(currentIndex) : "";
    }

    /**
     * Clears the command history.
     */
    public void clear() {
        history.clear();
        currentIndex = 0;
    }
}
