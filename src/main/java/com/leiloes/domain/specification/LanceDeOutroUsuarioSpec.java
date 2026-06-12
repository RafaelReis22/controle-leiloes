package com.leiloes.domain.specification;

import com.leiloes.domain.model.Leilao;

/**
 * Specification: verifica se o usuário do lance é diferente do responsável pelo lote e do responsável pelo leilão.
 */
public class LanceDeOutroUsuarioSpec implements Specification<Leilao> {

    private final Long idUsuarioLance;

    public LanceDeOutroUsuarioSpec(Long idUsuarioLance) {
        this.idUsuarioLance = idUsuarioLance;
    }

    @Override
    public boolean isSatisfiedBy(Leilao leilao) {
        if (leilao == null || idUsuarioLance == null) {
            return false;
        }

        // Não pode ser o responsável pelo leilão (leiloeiro)
        if (leilao.getResponsavel() != null && idUsuarioLance.equals(leilao.getResponsavel().getId())) {
            return false;
        }

        // Não pode ser o responsável pelo lote (dono dos bens)
        if (leilao.getLote() != null && leilao.getLote().getResponsavel() != null 
                && idUsuarioLance.equals(leilao.getLote().getResponsavel().getId())) {
            return false;
        }

        return true;
    }

    @Override
    public String mensagemDeErro() {
        return "O responsável pelo leilão ou pelo lote não pode efetuar lances.";
    }
}
