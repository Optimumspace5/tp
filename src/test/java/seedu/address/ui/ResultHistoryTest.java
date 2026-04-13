package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ResultHistoryTest {

    @Test
    public void formatFeedbackEntry_formatsWithPromptPrefix() {
        assertEquals("> Result OUTPUT", ResultHistory.formatFeedbackEntry("Result OUTPUT"));
    }

    @Test
    public void formatCommandResultEntry_formatsAsTwoLines() {
        assertEquals("> CMD entered\nResult OUTPUT",
                ResultHistory.formatCommandResultEntry("CMD entered", "Result OUTPUT"));
    }

    @Test
    public void capEntries_withinLimit_keepsAllEntries() {
        List<String> entries = List.of("a", "b", "c");
        assertEquals(entries, ResultHistory.capEntries(entries, 3));
    }

    @Test
    public void capEntries_exceedsLimit_keepsMostRecentEntries() {
        List<String> entries = List.of("a", "b", "c", "d");
        assertEquals(List.of("c", "d"), ResultHistory.capEntries(entries, 2));
    }
}
