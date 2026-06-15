package com.leiloes.service;

import com.leiloes.domain.model.Bem;
import com.leiloes.domain.model.Lote;
import com.leiloes.domain.model.Usuario;
import com.leiloes.dto.input.LoteInput;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.BemRepository;
import com.leiloes.repository.LoteRepository;
import com.leiloes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock LoteRepository loteRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock BemRepository bemRepository;

    @InjectMocks LoteService loteService;

    private Usuario responsavel;
    private Bem bem1;
    private Bem bem2;

    @BeforeEach
    void setUp() {
        responsavel = new Usuario();
        responsavel.setId(1L);
        responsavel.setNome("Responsável");
        responsavel.setCpfCnpj("11111111111");
        responsavel.setEmail("resp@test.com");

        bem1 = new Bem();
        bem1.setId(1L);
        bem1.setDescricaoBreve("Pintura");

        bem2 = new Bem();
        bem2.setId(2L);
        bem2.setDescricaoBreve("Escultura");
    }

    // ── cadastrar ─────────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoQuandoListaDeBensNula() {
        LoteInput input = new LoteInput("Lote Arte", new BigDecimal("500.00"), 1L, null);

        assertThatThrownBy(() -> loteService.cadastrar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos um bem");
    }

    @Test
    void deveLancarExcecaoQuandoListaDeBensVazia() {
        LoteInput input = new LoteInput("Lote Arte", new BigDecimal("500.00"), 1L, List.of());

        assertThatThrownBy(() -> loteService.cadastrar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos um bem");
    }

    @Test
    void deveLancarExcecaoQuandoResponsavelNaoEncontrado() {
        LoteInput input = new LoteInput("Lote Arte", new BigDecimal("500.00"), 99L, List.of(1L));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loteService.cadastrar(input))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoBemNaoEncontrado() {
        LoteInput input = new LoteInput("Lote Arte", new BigDecimal("500.00"), 1L, List.of(1L, 99L));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        // retorna apenas 1 bem de 2 ids informados
        when(bemRepository.findAllById(List.of(1L, 99L))).thenReturn(List.of(bem1));

        assertThatThrownBy(() -> loteService.cadastrar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não foram encontrados");
    }

    @Test
    void deveCadastrarLoteComSucesso() {
        LoteInput input = new LoteInput("Lote Arte", new BigDecimal("500.00"), 1L, List.of(1L, 2L));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(bemRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(bem1, bem2));

        loteService.cadastrar(input);

        verify(loteRepository).save(any(Lote.class));
    }

    // ── buscar ────────────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoQuandoLoteNaoEncontradoAoBuscar() {
        when(loteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loteService.buscar(99L))
                .isInstanceOf(LoteNaoEncontradoException.class);
    }

    @Test
    void deveBuscarLoteExistente() {
        Lote lote = new Lote();
        lote.setId(10L);
        lote.setDescricao("Lote Arte");
        lote.setPrecoMinimo(new BigDecimal("500.00"));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));

        Lote resultado = loteService.buscar(10L);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getDescricao()).isEqualTo("Lote Arte");
    }
}
