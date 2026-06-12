package com.leiloes.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LanceDetalhe(
    Long id,
    String nomeUsuario,
    BigDecimal valor,
    LocalDateTime dataHora
) {
    
}
