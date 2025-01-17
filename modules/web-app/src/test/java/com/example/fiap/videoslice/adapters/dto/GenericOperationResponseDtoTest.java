package com.example.fiap.videoslice.adapters.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GenericOperationResponseDtoTest {

    private GenericOperationResponse genericOperationResponse;


    @BeforeEach
    void setUp() {
        genericOperationResponse = new GenericOperationResponse(false);
    }

    @Test
    void validateFields() {
        assertThat(genericOperationResponse.success()).isFalse();
    }
}