package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Contains tests for the {@code AppModeManager} class.
 */
public class AppModeManagerTest {

    private AppModeManager modeManager;

    @BeforeEach
    public void setUp() {
        // Initialize with LOCKED mode as default
        modeManager = new AppModeManager(AppMode.LOCKED);
    }

    @Test
    public void constructor_withLockedMode_initializesCorrectly() {
        modeManager = new AppModeManager(AppMode.LOCKED);
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void constructor_withUnlockedMode_initializesCorrectly() {
        modeManager = new AppModeManager(AppMode.UNLOCKED);
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void constructor_withNullMode_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AppModeManager(null));
    }

    @Test
    public void getMode_returnsInitialMode() {
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void getMode_afterUnlock_returnsUnlockedMode() {
        modeManager.unlock();
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void getMode_afterLock_returnsLockedMode() {
        modeManager.unlock();
        modeManager.lock();
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void lock_fromLockedMode_staysLocked() {
        modeManager.lock();
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void lock_fromUnlockedMode_changesToLocked() {
        modeManager.unlock();
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
        modeManager.lock();
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void unlock_fromLockedMode_changesToUnlocked() {
        assertEquals(AppMode.LOCKED, modeManager.getMode());
        modeManager.unlock();
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void unlock_fromUnlockedMode_staysUnlocked() {
        modeManager.unlock();
        modeManager.unlock();
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void transitionTo_lockedToUnlocked_returnsTrue() {
        assertEquals(AppMode.LOCKED, modeManager.getMode());
        boolean changed = modeManager.transitionTo(AppMode.UNLOCKED);
        assertTrue(changed);
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void transitionTo_unlockedToLocked_returnsTrue() {
        modeManager.unlock();
        boolean changed = modeManager.transitionTo(AppMode.LOCKED);
        assertTrue(changed);
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void transitionTo_sameMode_returnsFalse() {
        boolean changed = modeManager.transitionTo(AppMode.LOCKED);
        assertFalse(changed);
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }

    @Test
    public void transitionTo_unlockedToSameMode_returnsFalse() {
        modeManager.unlock();
        boolean changed = modeManager.transitionTo(AppMode.UNLOCKED);
        assertFalse(changed);
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());
    }

    @Test
    public void transitionTo_withNullMode_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modeManager.transitionTo(null));
    }

    @Test
    public void multipleTransitions_sequentialChanges() {
        // Start in LOCKED
        assertEquals(AppMode.LOCKED, modeManager.getMode());

        // Transition to UNLOCKED
        assertTrue(modeManager.transitionTo(AppMode.UNLOCKED));
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());

        // Try to transition to same mode
        assertFalse(modeManager.transitionTo(AppMode.UNLOCKED));
        assertEquals(AppMode.UNLOCKED, modeManager.getMode());

        // Transition back to LOCKED
        assertTrue(modeManager.transitionTo(AppMode.LOCKED));
        assertEquals(AppMode.LOCKED, modeManager.getMode());
    }
}
