package com.example.fiap.videoslice.adapters.dto;

public record VideoResponseDto(
        String id,
        String name,
        String status,
        String path,
        Integer timeFrame,
        String frameFilePath
) {

}
