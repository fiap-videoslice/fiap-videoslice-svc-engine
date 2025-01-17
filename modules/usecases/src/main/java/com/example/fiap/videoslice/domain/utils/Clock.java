package com.example.fiap.videoslice.domain.utils;

import java.time.LocalDateTime;

/**
 * Obtenção de data/hora em um serviço isolado para facilitar os testes.
 */
public class Clock {
    public LocalDateTime localDateTime() {
        return LocalDateTime.now();
    }
}
