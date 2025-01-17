package com.example.fiap.videoslice.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StatusVideoTest {

    @Test
    void testValoresEsperados() {
        // Verifica se todos os valores esperados estão presentes
        assertEquals(4, StatusVideo.values().length);
    }

    @Test
    void testComparacoes() {
        assertEquals(StatusVideo.PROCESSED_OK, StatusVideo.PROCESSED_OK);
        assertNotEquals(StatusVideo.IN_PROCESS, StatusVideo.PROCESSED_ERROR);
    }
}