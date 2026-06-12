package com.leiloes.service;

import com.leiloes.domain.model.Lance;
import com.leiloes.domain.model.Leilao;
import com.leiloes.domain.model.Usuario;
import com.leiloes.domain.specification.LanceAcimaDoMinimoSpec;
import com.leiloes.domain.specification.LanceDeOutroUsuarioSpec;
import com.leiloes.domain.specification.LanceMaiorQueLanceAtualSpec;
import com.leiloes.domain.specification.LeilaoEmAndamentoSpec;
import com.leiloes.domain.specification.Specification;
import com.leiloes.dto.input.LanceInput;
import com.leiloes.dto.output.LanceDetalhe;
import com.leiloes.exception.LanceInvalidoException;
import com.leiloes.exception.LeilaoNaoEmAndamentoException;
import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.LanceRepository;
import com.leiloes.repository.LeilaoRepository;
import com.leiloes.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LanceService {

    private final LeilaoRepository leilaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LanceRepository lanceRepository;
    private final Specification<Leilao> emAndamento = new LeilaoEmAndamentoSpec();

    public LanceService(LeilaoRepository leilaoRepository, UsuarioRepository usuarioRepository, LanceRepository lanceRepository) {
        this.leilaoRepository = leilaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.lanceRepository = lanceRepository;
    }

    @Transactional
    public LanceDetalhe registrarLance(LanceInput input) {
        Leilao leilao = leilaoRepository.findByIdForUpdate(input.idLeilao())
                .orElseThrow(() -> new LeilaoNaoEncontradoException(input.idLeilao()));

        // 1. Specification: leilão em andamento?
        if (!emAndamento.isSatisfiedBy(leilao)) {
            throw new LeilaoNaoEmAndamentoException(emAndamento.mensagemDeErro());
        }

        // 2. Specification: usuário não é o responsável pelo leilão ou lote?
        Specification<Leilao> deOutroUsuario = new LanceDeOutroUsuarioSpec(input.idUsuario());
        if (!deOutroUsuario.isSatisfiedBy(leilao)) {
            throw new LanceInvalidoException(deOutroUsuario.mensagemDeErro());
        }

        // 3. Specification: valor acima do mínimo?
        Specification<Leilao> acimaDoMinimo = new LanceAcimaDoMinimoSpec(input.valor());
        if (!acimaDoMinimo.isSatisfiedBy(leilao)) {
            throw new LanceInvalidoException(acimaDoMinimo.mensagemDeErro());
        }

        // 4. Specification: em leilões abertos, valor maior que o lance atual?
        Specification<Leilao> maiorQueLanceAtual = new LanceMaiorQueLanceAtualSpec(input.valor());
        if (!maiorQueLanceAtual.isSatisfiedBy(leilao)) {
            throw new LanceInvalidoException(maiorQueLanceAtual.mensagemDeErro());
        }


        Usuario usuario = usuarioRepository.findById(input.idUsuario())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(input.idUsuario()));

        Lance lance = new Lance(LocalDateTime.now(), input.valor(), usuario, leilao);
        Lance salvo = lanceRepository.save(lance);

        return new LanceDetalhe(
                salvo.getId(),
                usuario.getNome(),
                salvo.getValor(),
                salvo.getDataHora()
        );
    }
}
