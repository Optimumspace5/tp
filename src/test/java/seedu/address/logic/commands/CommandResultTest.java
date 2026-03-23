package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.AppMode;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // same mode request value -> returns true
        assertTrue(new CommandResult("feedback", false, false, AppMode.LOCKED)
            .equals(new CommandResult("feedback", false, false, AppMode.LOCKED)));
        assertTrue(new CommandResult("feedback", false, false, AppMode.UNLOCKED)
            .equals(new CommandResult("feedback", false, false, AppMode.UNLOCKED)));

        // different mode request value -> returns false
        assertFalse(new CommandResult("feedback", false, false, AppMode.LOCKED)
            .equals(new CommandResult("feedback", false, false, AppMode.UNLOCKED)));

        // with and without mode -> returns false
        assertFalse(new CommandResult("feedback", false, false, AppMode.LOCKED)
            .equals(new CommandResult("feedback", false, false)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // same mode request value -> returns same hashcode
        assertEquals(new CommandResult("feedback", false, false, AppMode.LOCKED).hashCode(),
            new CommandResult("feedback", false, false, AppMode.LOCKED).hashCode());
        assertEquals(new CommandResult("feedback", false, false, AppMode.UNLOCKED).hashCode(),
            new CommandResult("feedback", false, false, AppMode.UNLOCKED).hashCode());

        // different mode request value -> returns different hashcode
        assertNotEquals(new CommandResult("feedback", false, false, AppMode.LOCKED).hashCode(),
            new CommandResult("feedback", false, false, AppMode.UNLOCKED).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit()
                + ", requestedMode=" + commandResult.getRequestedMode().orElse(null) + "}";
        assertEquals(expected, commandResult.toString());

        CommandResult lockCommandResult = new CommandResult("feedback", false, false, AppMode.LOCKED);
        CommandResult unlockCommandResult = new CommandResult("feedback", false, false, AppMode.UNLOCKED);
        assertTrue(lockCommandResult.toString().contains("LOCKED"));
        assertTrue(unlockCommandResult.toString().contains("UNLOCKED"));
    }
}
