package com.example.fiap.archburgers.integration;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {

    private Response response;

    @Dado("que o cliente deseja consultar opcoes de pagamento")
    public void que_o_cliente_deseja_consultar_opcoes_pagamento() {
        RestAssured.baseURI = "http://localhost:8090";
        RestAssured.basePath = "/pagamento/opcoes";
    }

    @Quando("o cliente faz a requisição das opcoes")
    public void o_cliente_faz_a_requisicao_das_opcoes() {
        response = given().contentType(ContentType.JSON).when().get();
    }

    @Entao("a resposta é uma lista de opcoes")
    public void a_resposta_e_uma_lista_de_opcoes() {
        response.then().body(
                "", isA(List.class),
                "", hasSize(greaterThan(2)));
    }

    @Entao("a lista retornada possui ao menos um item do tipo {string}")
    public void a_lista_retornada_possui_ao_menos_um_item_do_tipo(String tipo) {
//        response.body().path("[0].codigo")

        List<Map<String,Object>>lista =response.jsonPath().getList("id");

        Map<String,Object> formaPagamento=lista.get(0);// for first index
        String codigo=(String)formaPagamento.get("codigo");

        assertEquals(tipo, codigo);
    }
}
