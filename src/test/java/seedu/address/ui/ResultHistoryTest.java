package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
