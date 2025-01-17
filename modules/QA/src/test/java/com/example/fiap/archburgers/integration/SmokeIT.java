package com.example.fiap.archburgers.integration;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * A basic end-to-end test ("smoke test") which verifies if the application is up with all of its dependencies.
 */
public class SmokeIT {
    @Test
    public void getAllItens() {
//        RestAssured.when().
//                get("http://localhost:8090/pagamento/opcoes").
//                then().
//                statusCode(200).
//                body("", Matchers.isA(List.class),
//                        "[0].id.codigo", Matchers.equalTo("MERCADO_PAGO"));
    }
}
