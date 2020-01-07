package com.example.gestor_tab.enumClasses;

public enum TablePosition {
    NOME(0),
    ID(1),
    PAGAMENTO(2),
    TIPO(3),
    SEGUNDA(4),
    TERCA(5),
    QUARTA(6),
    QUINTA(7),
    SEXTA(8),
    SABADO(9),
    DOMINGO(10),
    EXTRAS(11),
    COORDENADAS(12),
    ATIVO(13),
    INICIO_INITAVIDADE(14),
    FIM_INITAVIDADE(15);

    private final int value;

    TablePosition(final int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
