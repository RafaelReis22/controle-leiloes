package com.leiloes.dto.output;

import com.leiloes.domain.enums.StatusLeilao;
import com.leiloes.domain.enums.TipoLeilao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeilaoResumo(
    Long id,
    TipoLeilao tipo,
    String descricaoLote,
    BigDecimal precoMinimo,
    String nomeResponsavel,
    LocalDateTime dataInicio,
    LocalDateTime dataTermino,
    long totalLances
) {
    public StatusLeilao status() {
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(dataTermino))  return StatusLeilao.ENCERRADO;
        if (!agora.isBefore(dataInicio)) return StatusLeilao.EM_ANDAMENTO;
        return StatusLeilao.AGUARDANDO;
    }
}
