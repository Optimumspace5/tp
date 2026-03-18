package seedu.address.logic.parser;

import seedu.address.logic.commands.UnlockCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UnlockCommand} object.
 */
public class UnlockCommandParser implements Parser<UnlockCommand> {

    /**
     * Parses the given {@code String} of arguments to create an {@code UnlockCommand}.
     * The parser handles the extraction and trimming of the password.
     *
     * @param args The raw command arguments entered by the user.
     * @return An {@code UnlockCommand} initialized with the provided password.
     * @throws ParseException if the parsing process fails.
     */
    @Override
    public UnlockCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        return new UnlockCommand(trimmedArgs);
    }
}
