package com.leiloes.domain.specification;

import com.leiloes.domain.model.Leilao;
import java.time.LocalDateTime;

/**
 * Specification: verifica se um leilão está em andamento no momento atual.
 *
 * Satisfeita quando: dataInicio <= agora < dataTermino.
 */
public class LeilaoEmAndamentoSpec implements Specification<Leilao> {

    @Override
    public boolean isSatisfiedBy(Leilao leilao) {
        if (leilao == null || leilao.getDataInicio() == null || leilao.getDataTermino() == null) {
            return false;
        }
        LocalDateTime agora = LocalDateTime.now();
        return !agora.isBefore(leilao.getDataInicio())
            && agora.isBefore(leilao.getDataTermino());
    }

    @Override
    public String mensagemDeErro() {
        return "O leilão não está em andamento.";
    }
}
