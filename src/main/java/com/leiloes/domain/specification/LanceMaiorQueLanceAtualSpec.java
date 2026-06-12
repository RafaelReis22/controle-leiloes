package com.leiloes.domain.specification;

import com.leiloes.domain.enums.TipoLeilao;
import com.leiloes.domain.model.Lance;
import com.leiloes.domain.model.Leilao;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Specification: verifica se o valor do lance é superior ao maior lance atual em leilões abertos.
 */
public class LanceMaiorQueLanceAtualSpec implements Specification<Leilao> {

    private final BigDecimal valorDoLance;

    public LanceMaiorQueLanceAtualSpec(BigDecimal valorDoLance) {
        this.valorDoLance = valorDoLance;
    }

    @Override
    public boolean isSatisfiedBy(Leilao leilao) {
        if (leilao == null || valorDoLance == null) {
            return false;
        }

        // Em leilões FECHADOS, os lances são secretos e não precisam superar os anteriores
        if (leilao.getTipo() == TipoLeilao.FECHADO) {
            return true;
        }

        // Se não houver lances anteriores, o lance é válido (a Spec de preço mínimo do lote fará o resto do trabalho)
        if (leilao.getLances() == null || leilao.getLances().isEmpty()) {
            return true;
        }

        BigDecimal maiorLanceAtual = leilao.getLances().stream()
                .map(Lance::getValor)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        return valorDoLance.compareTo(maiorLanceAtual) > 0;
    }

    @Override
    public String mensagemDeErro() {
        return "O valor do lance deve ser superior ao lance atual do leilão.";
    }
}
