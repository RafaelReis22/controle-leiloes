package com.leiloes.dto.input;

import com.leiloes.domain.enums.TipoLeilao;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record LeilaoInput(
    @NotNull 
    TipoLeilao tipo,
    @NotNull 
    Long idLote,
    @NotNull
    Long idResponsavel,
    @NotNull 
    LocalDateTime dataInicio,
    @NotNull 
    LocalDateTime dataTermino
) {

}
