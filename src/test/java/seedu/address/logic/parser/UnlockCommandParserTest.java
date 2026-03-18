package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnlockCommand;

/**
 * Contains unit tests for {@code UnlockCommandParser}.
 */
public class UnlockCommandParserTest {

    private final UnlockCommandParser parser = new UnlockCommandParser();

    @Test
    public void parse_validArgs_returnsUnlockCommand() {
        String password = "nusStudent2026";
        UnlockCommand expectedCommand = new UnlockCommand(password);

        assertParseSuccess(parser, password, expectedCommand);

        assertParseSuccess(parser, "  " + password + "  \n\t", expectedCommand);
    }

    @Test
    public void parse_emptyArgs_returnsUnlockCommandWithEmptyPassword() {
        UnlockCommand expectedCommand = new UnlockCommand("");
        assertParseSuccess(parser, "     ", expectedCommand);
    }
}
