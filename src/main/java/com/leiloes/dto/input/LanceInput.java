package com.leiloes.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record LanceInput(
        @NotNull
        Long idLeilao,
        @NotNull
        Long idUsuario,
        @NotNull @Positive
        BigDecimal valor) {
}
