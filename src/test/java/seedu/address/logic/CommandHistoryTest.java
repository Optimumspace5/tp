package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private static final int HISTORY_CAP = 100;
    private static final int COMMANDS_ADDED = HISTORY_CAP + 1;

    private CommandHistory history;

    @BeforeEach
    public void setUp() {
        history = new CommandHistory();
    }

    @Test
    public void add_validCommand_success() {
        history.add("command 1");
        history.add("command 2");

        assertEquals("command 2", history.getPrevious());
        assertEquals("command 1", history.getPrevious());
    }

    @Test
    public void add_emptyOrNullCommand_ignored() {
        history.add("command 1");
        history.add("");
        history.add("  ");
        history.add(null);

        assertEquals("command 1", history.getPrevious());
        assertEquals("command 1", history.getPrevious());
    }

    @Test
    public void getPrevious_emptyHistory_returnsEmptyString() {
        assertEquals("", history.getPrevious());
    }

    @Test
    public void getNext_emptyHistory_returnsEmptyString() {
        assertEquals("", history.getNext());
    }

    @Test
    public void getPreviousAndNext_mixed_success() {
        history.add("cmd1");
        history.add("cmd2");
        history.add("cmd3");

        assertEquals("cmd3", history.getPrevious());
        assertEquals("cmd2", history.getPrevious());
        assertEquals("cmd1", history.getPrevious());

        // Exceeding history bounds keeps returning the oldest command
        assertEquals("cmd1", history.getPrevious());

        assertEquals("cmd2", history.getNext());
        assertEquals("cmd3", history.getNext());

        // Exceeding history bounds forwards returns empty string
        // to signify jumping "past" the most recent into the current typing buffer
        assertEquals("", history.getNext());
    }

    @Test
    public void clear_clearsHistory_success() {
        history.add("cmd1");
        history.add("cmd2");

        history.clear();

        assertEquals("", history.getPrevious());
        assertEquals("", history.getNext());
    }

    @Test
    public void add_exceedsMaxEntries_dropsOldestCommand() {
        for (int i = 1; i <= COMMANDS_ADDED; i++) {
            history.add("cmd" + i);
        }

        // Newest command should still be available first.
        assertEquals("cmd" + COMMANDS_ADDED, history.getPrevious());

        // Traversing to the oldest retained command should skip the dropped one (cmd1).
        String oldestRetained = "";
        for (int i = 0; i < HISTORY_CAP - 1; i++) {
            oldestRetained = history.getPrevious();
        }
        assertEquals("cmd2", oldestRetained);
        assertEquals("cmd2", history.getPrevious());
    }
}
