# language: pt
Funcionalidade: Consulta de Pagamento

  Cenário: Consultar lista de opções de pagamento
    Dado que o cliente deseja consultar opcoes de pagamento
    Quando o cliente faz a requisição das opcoes
    Então a resposta é uma lista de opcoes

  Delineação do Cenário: Verificar tipo de opções de pagamento
    Dado que o cliente deseja consultar opcoes de pagamento
    Quando o cliente faz a requisição das opcoes
    Então a lista retornada possui ao menos um item do tipo "MERCADO_PAGO"

    Exemplos:
      | DINHEIRO       |
      | MERCADO_PAGO   |
      | CARTAO_MAQUINA |
