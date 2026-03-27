package seedu.address.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@code PasswordUtil}.
 * Focuses on validation rules including ASCII-only requirements and whitespace handling.
 */
public class PasswordUtilTest {

    // Validation Logic Tests

    @Test
    public void isValidPassword_nullInput_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword(null));
    }

    @Test
    public void isValidPassword_emptyString_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword(""));
    }

    @Test
    public void isValidPassword_onlyWhitespace_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword("   "));
        assertFalse(PasswordUtil.isValidPassword("\n\t"));
    }

    @Test
    public void isValidPassword_alphanumeric_returnsTrue() {
        assertTrue(PasswordUtil.isValidPassword("p"));
        assertTrue(PasswordUtil.isValidPassword("password123"));
    }

    @Test
    public void isValidPassword_allPrintableAsciiSymbols_returnsTrue() {
        assertTrue(PasswordUtil.isValidPassword("!@#$%^&*()_+-=[]{}|;':\",./<>?`~"));
    }

    @Test
    public void isValidPassword_withLeadingTrailingWhitespace_returnsTrue() {
        // Leading/trailing whitespace is ignored for basic validation as it's auto-trimmed
        assertTrue(PasswordUtil.isValidPassword("  actualPassword123  "));
        assertTrue(PasswordUtil.isValidPassword("\tpassword\n"));
    }

    @Test
    public void isValidPassword_containsInternalSpace_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword("pass word"));
    }

    @Test
    public void isValidPassword_containsEmojis_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword("password😊"));
        assertFalse(PasswordUtil.isValidPassword("🔥"));
        assertFalse(PasswordUtil.isValidPassword("✨pass✨"));
    }

    @Test
    public void isValidPassword_containsAccentedCharacters_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword("pássword"));
        assertFalse(PasswordUtil.isValidPassword("reproducé"));
        assertFalse(PasswordUtil.isValidPassword("München"));
    }

    @Test
    public void isValidPassword_containsNonLatinScripts_returnsFalse() {
        assertFalse(PasswordUtil.isValidPassword("密码123"));
        assertFalse(PasswordUtil.isValidPassword("пароль"));
        assertFalse(PasswordUtil.isValidPassword("كلمة"));
        assertFalse(PasswordUtil.isValidPassword("パスワード"));
    }

    // Validation Error Message Tests

    @Test
    public void getValidationError_nullOrEmpty_returnsEmptyMessage() {
        assertEquals(PasswordUtil.MESSAGE_EMPTY, PasswordUtil.getValidationError(null));
        assertEquals(PasswordUtil.MESSAGE_EMPTY, PasswordUtil.getValidationError(""));
        assertEquals(PasswordUtil.MESSAGE_EMPTY, PasswordUtil.getValidationError("   "));
    }

    @Test
    public void getValidationError_containsSpaces_returnsNoSpacesMessage() {
        assertEquals(PasswordUtil.MESSAGE_NO_SPACES, PasswordUtil.getValidationError("k l"));
    }

    @Test
    public void getValidationError_nonAscii_returnsOnlyAsciiMessage() {
        assertEquals(PasswordUtil.MESSAGE_ONLY_ASCII, PasswordUtil.getValidationError("password😊"));
        assertEquals(PasswordUtil.MESSAGE_ONLY_ASCII, PasswordUtil.getValidationError("pássword"));
    }

    @Test
    public void getValidationError_validInput_returnsNull() {
        assertNull(PasswordUtil.getValidationError("p"));
        assertNull(PasswordUtil.getValidationError("password123"));
        assertNull(PasswordUtil.getValidationError("!@#$%"));
    }

    // Strict Validation Tests

    @Test
    public void isStrictlyValid_whitespaceHandling_returnsFalse() {
        assertFalse(PasswordUtil.isStrictlyValid("password "));
        assertFalse(PasswordUtil.isStrictlyValid(" password"));
        assertFalse(PasswordUtil.isStrictlyValid("\tpassword"));
    }

    @Test
    public void isStrictlyValid_nonAsciiInput_returnsFalse() {
        assertFalse(PasswordUtil.isStrictlyValid("pássword"));
        assertFalse(PasswordUtil.isStrictlyValid("😊"));
    }

    @Test
    public void isStrictlyValid_cleanValidPassword_returnsTrue() {
        assertTrue(PasswordUtil.isStrictlyValid("password123"));
        assertTrue(PasswordUtil.isStrictlyValid("Admin#Secure!"));
    }
}
