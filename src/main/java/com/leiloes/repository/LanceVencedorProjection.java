package com.leiloes.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LanceVencedorProjection {
    Long getId();
    String getNome();
    BigDecimal getValor();
    LocalDateTime getDataHora();
}
