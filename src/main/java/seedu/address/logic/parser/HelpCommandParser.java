package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code HelpCommand} object.
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    @Override
    public HelpCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            return new HelpCommand();
        }

        String[] tokens = trimmedArgs.split("\\s+");
        if (tokens.length > 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        return new HelpCommand(tokens[0]);
    }
}
