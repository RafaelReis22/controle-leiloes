package com.leiloes.dto.output;

import com.leiloes.domain.enums.StatusLeilao;
import com.leiloes.domain.enums.TipoLeilao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LeilaoDetalhe(
    Long id,
    TipoLeilao tipo,
    StatusLeilao status,
    String descricaoLote,
    BigDecimal precoMinimo,
    String nomeResponsavel,
    LocalDateTime dataInicio,
    LocalDateTime dataTermino,
    List<LanceDetalhe> lancesVisiveis,
    LanceDetalhe lanceVencedor
) {
    
}
