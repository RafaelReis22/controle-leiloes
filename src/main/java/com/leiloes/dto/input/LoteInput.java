package com.leiloes.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record LoteInput(
        @NotBlank
        String descricao,
        @NotNull @Positive
        BigDecimal precoMinimo,
        @NotNull
        Long idResponsavel,
        List<Long> idsBens) {
}
