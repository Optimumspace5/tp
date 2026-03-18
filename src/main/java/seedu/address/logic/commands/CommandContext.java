package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.AppMode;
import seedu.address.model.Model;

/**
 * Represents the execution environment for a {@code Command}.
 * * <p>A {@code CommandContext} encapsulates the state of the application at the precise
 * moment of execution. This prevents "stale state" issues where a command might be
 * parsed in one mode but executed after the application state has changed.</p>
 */
public class CommandContext {
    private final Model model;
    private final AppMode appMode;

    /**
     * Constructs a {@code CommandContext} with the specified model and application mode.
     *
     * @param model The {@code Model} the command should operate on. Must not be null.
     * @param appMode The {@code AppMode} of the application at execution time. Must not be null.
     * @throws NullPointerException if {@code model} or {@code appMode} is null.
     */
    public CommandContext(Model model, AppMode appMode) {
        requireNonNull(model);
        requireNonNull(appMode);
        this.model = model;
        this.appMode = appMode;
    }

    /**
     * Returns the {@code Model} associated with this context.
     *
     * @return The application model.
     */
    public Model getModel() {
        return model;
    }

    /**
     * Returns the {@code AppMode} associated with this context.
     *
     * @return The current application mode (e.g., LOCKED or UNLOCKED).
     */
    public AppMode getAppMode() {
        return appMode;
    }
}
