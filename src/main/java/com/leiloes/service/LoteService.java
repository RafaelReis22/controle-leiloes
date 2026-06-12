package com.leiloes.service;

import com.leiloes.domain.model.Bem;
import com.leiloes.domain.model.Lote;
import com.leiloes.domain.model.Usuario;
import com.leiloes.dto.input.LoteInput;
import com.leiloes.dto.output.LoteResumo;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.BemRepository;
import com.leiloes.repository.LoteRepository;
import com.leiloes.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final BemRepository bemRepository;

    public LoteService(LoteRepository loteRepository, UsuarioRepository usuarioRepository, BemRepository bemRepository) {
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.bemRepository = bemRepository;
    }

    @Transactional
    public void cadastrar(LoteInput input) {
        if (input.idsBens() == null || input.idsBens().isEmpty()) {
            throw new IllegalArgumentException("O lote deve possuir pelo menos um bem.");
        }

        Usuario responsavel = usuarioRepository.findById(input.idResponsavel())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(input.idResponsavel()));

        Lote lote = new Lote();
        lote.setDescricao(input.descricao());
        lote.setPrecoMinimo(input.precoMinimo());
        lote.setResponsavel(responsavel);

        List<Bem> bens = bemRepository.findAllById(input.idsBens());
        long idsDistintos = input.idsBens().stream().distinct().count();
        if (bens.size() != idsDistintos) {
            throw new IllegalArgumentException("Um ou mais bens informados não foram encontrados.");
        }
        for (Bem bem : bens) {
            lote.adicionarBem(bem);
        }

        loteRepository.save(lote);
    }

    public List<LoteResumo> listarTodos() {
        return loteRepository.listarTodosComoResumo();
    }

    public Lote buscar(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new LoteNaoEncontradoException(id));
    }
}
