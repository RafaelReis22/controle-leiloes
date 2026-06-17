package com.leiloes.domain.specification;

import com.leiloes.domain.model.Leilao;
import java.math.BigDecimal;

/**
 * Specification: verifica se o valor do lance é igual ou superior ao preço mínimo do lote.
 *
 * Satisfeita quando: valorDoLance >= precoMinimoDoLote.
 */
public class LanceAcimaDoMinimoSpec implements Specification<Leilao> {

    private final BigDecimal valorDoLance;

    public LanceAcimaDoMinimoSpec(BigDecimal valorDoLance) {
        this.valorDoLance = valorDoLance;
    }

    @Override
    public boolean isSatisfiedBy(Leilao leilao) {
        if (leilao == null || leilao.getLote() == null || leilao.getLote().getPrecoMinimo() == null
                || this.valorDoLance == null) {
            return false;
        }
        return this.valorDoLance.compareTo(leilao.getLote().getPrecoMinimo()) >= 0;
    }

    @Override
    public String mensagemDeErro() {
        return "O valor do lance deve ser igual ou superior ao preço mínimo do lote.";
    }
}
