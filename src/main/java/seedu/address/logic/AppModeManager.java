package seedu.address.logic;

import static java.util.Objects.requireNonNull;

/**
 * Single source of truth for the current app mode.
 */
public class AppModeManager {

    private AppMode appMode;

    /**
     * Initializes the app mode manager with the given initial state.
      *
     * @param initialState the initial mode of the application
     */
    public AppModeManager(AppMode initialState) {
        appMode = requireNonNull(initialState);
    }

    public AppMode getMode() {
        return appMode;
    }

    /**
     * Sets the app mode to locked.
     */
    public void lock() {
        appMode = AppMode.LOCKED;
    }

    /**
     * Sets the app mode to unlocked.
     */
    public void unlock() {
        appMode = AppMode.UNLOCKED;
    }

    /**
     * Transitions to the requested mode.
     *
     * @return {@code true} if a state change was applied, {@code false} if already in requested mode.
     */
    public boolean transitionTo(AppMode requestedMode) {
        requireNonNull(requestedMode);
        if (appMode == requestedMode) {
            return false;
        }
        appMode = requestedMode;
        return true;
    }
}
