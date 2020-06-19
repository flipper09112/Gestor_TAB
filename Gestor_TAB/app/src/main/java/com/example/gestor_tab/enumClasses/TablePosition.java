package com.example.gestor_tab.enumClasses;

public enum TablePosition {
    NOME(0),
    ID(1),
    MORADA(2),
    NPORTA(3),
    ALCUNHA(4),
    PAGAMENTO(5),
    TIPO(6),
    SEGUNDA(7),
    TERCA(8),
    QUARTA(9),
    QUINTA(10),
    SEXTA(11),
    SABADO(12),
    DOMINGO(13),
    EXTRAS(14),
    COORDENADAS(15),
    ATIVO(16),
    INICIO_INITAVIDADE(17),
    FIM_INITAVIDADE(18),
    DESCRICAO_SEGUNDA(19),
    DESCRICAO_TERCA(20),
    DESCRICAO_QUARTA(21),
    DESCRICAO_QUINTA(22),
    DESCRICAO_SEXTA(23),
    DESCRICAO_SABADO(24),
    DESCRICAO_DOMINGO(25)
    ;

    private final int value;

    TablePosition(final int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
