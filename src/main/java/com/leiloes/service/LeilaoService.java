package com.leiloes.service;

import com.leiloes.domain.enums.StatusLeilao;
import com.leiloes.domain.enums.TipoLeilao;
import com.leiloes.domain.model.Leilao;
import com.leiloes.domain.model.Lote;
import com.leiloes.domain.model.Usuario;
import com.leiloes.dto.input.LeilaoInput;
import com.leiloes.dto.output.LanceDetalhe;
import com.leiloes.dto.output.LeilaoDetalhe;
import com.leiloes.dto.output.LeilaoResumo;
import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.LanceRepository;
import com.leiloes.repository.LeilaoRepository;
import com.leiloes.repository.LoteRepository;
import com.leiloes.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class LeilaoService {

    private final LeilaoRepository leilaoRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final LanceRepository lanceRepository;

    public LeilaoService(LeilaoRepository leilaoRepository, LoteRepository loteRepository,
                         UsuarioRepository usuarioRepository, LanceRepository lanceRepository) {
        this.leilaoRepository = leilaoRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.lanceRepository = lanceRepository;
    }

    @Transactional
    public void cadastrar(LeilaoInput input) {
        LocalDateTime agora = LocalDateTime.now();
        if (input.dataInicio() == null || input.dataTermino() == null
                || !input.dataInicio().isBefore(input.dataTermino())) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de término.");
        }
        if (input.dataInicio().isBefore(agora)) {
            throw new IllegalArgumentException("A data de início deve ser no futuro.");
        }

        Lote lote = loteRepository.findById(input.idLote())
                .orElseThrow(() -> new LoteNaoEncontradoException(input.idLote()));

        Usuario responsavel = usuarioRepository.findById(input.idResponsavel())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(input.idResponsavel()));

        Leilao leilao = new Leilao();
        leilao.setTipo(input.tipo());
        leilao.setLote(lote);
        leilao.setResponsavel(responsavel);
        leilao.setDataInicio(input.dataInicio());
        leilao.setDataTermino(input.dataTermino());

        leilaoRepository.save(leilao);
    }

    @Transactional(readOnly = true)
    public List<LeilaoResumo> listarTodos() {
        return leilaoRepository.listarTodosComoResumo();
    }

    @Transactional(readOnly = true)
    public List<LeilaoResumo> listarEmAndamento() {
        return leilaoRepository.listarEmAndamentoComoResumo(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public LeilaoDetalhe buscarDetalhe(Long id) {
        Leilao leilao = leilaoRepository.findById(id)
                .orElseThrow(() -> new LeilaoNaoEncontradoException(id));

        StatusLeilao status = leilao.getStatus();
        boolean encerrado = status == StatusLeilao.ENCERRADO;

        List<LanceDetalhe> lancesVisiveis;
        if (leilao.getTipo() == TipoLeilao.ABERTO || encerrado) {
            lancesVisiveis = lanceRepository.findDetalhesByLeilaoId(id);
        } else {
            lancesVisiveis = Collections.emptyList();
        }

        LanceDetalhe lanceVencedor = null;
        if (encerrado) {
            lanceVencedor = lanceRepository.findVencedor(id)
                    .map(p -> new LanceDetalhe(p.getId(), p.getNome(), p.getValor(), p.getDataHora()))
                    .orElse(null);
        }

        return new LeilaoDetalhe(
                leilao.getId(),
                leilao.getTipo(),
                status,
                leilao.getLote().getDescricao(),
                leilao.getLote().getPrecoMinimo(),
                leilao.getResponsavel().getNome(),
                leilao.getDataInicio(),
                leilao.getDataTermino(),
                lancesVisiveis,
                lanceVencedor
        );
    }
}
