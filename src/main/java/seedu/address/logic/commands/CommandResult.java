package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.AppMode;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** The application should exit. */
    private final boolean exit;

    /** The application will show password setup. */
    private final boolean showSetup;

    /** The requested mode change, if any. */
    private final AppMode requestedMode;

    /** The person list index to be selected by the UI, if any. */
    private final Index selectedIndex;

    /**
     * Constructs a {@code CommandResult} without requesting a Mode or Index change.
     */
    public CommandResult(String feedbackToUser, boolean showSetup, boolean exit) {
        this(feedbackToUser, showSetup, exit, null, null);
    }

    /**
     * Constructs a {@code CommandResult} with the requested mode and Index change.
     */
    public CommandResult(String feedbackToUser, boolean showSetup,
                         boolean exit, AppMode requestedMode, Index selectedIndex) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.exit = exit;
        this.showSetup = showSetup;
        this.requestedMode = requestedMode;
        this.selectedIndex = selectedIndex;
    }

    /**
     * Constructs a {@code CommandResult} that requests the UI to select the given index.
     */
    public CommandResult(String feedbackToUser, Index selectedIndex) {
        this(feedbackToUser, false, false, null, selectedIndex);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the requested mode change.
     */
    public CommandResult(String feedbackToUser, boolean showSetup,
                         boolean exit, AppMode requestedMode) {
        this(feedbackToUser, showSetup, exit, requestedMode, null);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowSetup() {
        return showSetup;
    }

    public boolean isExit() {
        return exit;
    }

    public Optional<AppMode> getRequestedMode() {
        return Optional.ofNullable(requestedMode);
    }

    public Optional<Index> getSelectedIndex() {
        return Optional.ofNullable(selectedIndex);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showSetup == otherCommandResult.showSetup
                && exit == otherCommandResult.exit
                && Objects.equals(requestedMode, otherCommandResult.requestedMode)
                && Objects.equals(selectedIndex, otherCommandResult.selectedIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showSetup, exit, requestedMode, selectedIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showSetup", showSetup)
                .add("exit", exit)
                .add("requestedMode", requestedMode)
                .add("selectedIndex", selectedIndex)
                .toString();
    }

}
