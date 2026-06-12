package com.leiloes.dto.output;

import java.math.BigDecimal;

public record LoteResumo(
    Long id,
    String descricao,
    BigDecimal precoMinimo,
    String nomeResponsavel,
    int quantidadeBens
) {

}
