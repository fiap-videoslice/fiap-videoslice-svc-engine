package com.example.fiap.videoslice.domain.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void isEmpty_nullString_returnsTrue() {
        boolean result = StringUtils.isEmpty(null);
        assertThat(result).isTrue();
    }

    @Test
    void isEmpty_emptyString_returnsTrue() {
        boolean result = StringUtils.isEmpty("");
        assertThat(result).isTrue();
    }

    @Test
    void isEmpty_stringWithOnlySpaces_returnsTrue() {
        boolean result = StringUtils.isEmpty("   ");
        assertThat(result).isTrue();
    }

    @Test
    void isEmpty_nonEmptyString_returnsFalse() {
        boolean result = StringUtils.isEmpty("nonEmptyString");
        assertThat(result).isFalse();
    }

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty("   "));
        assertFalse(StringUtils.isEmpty(" hello  "));
    }

    @Test
    public void testIsNotEmpty()
    {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertFalse(StringUtils.isNotEmpty("   "));
        assertTrue(StringUtils.isNotEmpty(" hello  "));
    }
}