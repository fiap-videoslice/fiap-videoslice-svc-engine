package com.example.fiap.videoslice.adapters.apiutils;

import com.example.fiap.videoslice.apiutils.WebUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebUtilsTest {

    @Test
    public void testErrorResponse() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String detail = "Invalid request";

        ResponseEntity<Object> responseEntity = WebUtils.errorResponse(httpStatus, detail);

        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(detail, ((ProblemDetail) responseEntity.getBody()).getDetail());
    }

    @Test
    public void testOkResponse() {
        String responseValue = "Success";

        ResponseEntity<String> responseEntity = WebUtils.okResponse(responseValue);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(responseValue, responseEntity.getBody());
    }


}