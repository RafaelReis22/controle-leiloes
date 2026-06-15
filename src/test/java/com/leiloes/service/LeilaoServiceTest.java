package com.leiloes.service;

import com.leiloes.domain.enums.StatusLeilao;
import com.leiloes.domain.enums.TipoLeilao;
import com.leiloes.domain.model.Leilao;
import com.leiloes.domain.model.Lote;
import com.leiloes.domain.model.Usuario;
import com.leiloes.dto.input.LeilaoInput;
import com.leiloes.dto.output.LanceDetalhe;
import com.leiloes.dto.output.LeilaoDetalhe;
import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.LanceRepository;
import com.leiloes.repository.LanceVencedorProjection;
import com.leiloes.repository.LeilaoRepository;
import com.leiloes.repository.LoteRepository;
import com.leiloes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeilaoServiceTest {

    @Mock LeilaoRepository leilaoRepository;
    @Mock LoteRepository loteRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock LanceRepository lanceRepository;

    @InjectMocks LeilaoService leilaoService;

    private Lote lote;
    private Usuario responsavel;

    @BeforeEach
    void setUp() {
        responsavel = new Usuario();
        responsavel.setId(1L);
        responsavel.setNome("Responsável");
        responsavel.setCpfCnpj("11111111111");
        responsavel.setEmail("resp@test.com");

        Usuario donoLote = new Usuario();
        donoLote.setId(2L);
        donoLote.setNome("Dono do Lote");
        donoLote.setCpfCnpj("22222222222");
        donoLote.setEmail("dono@test.com");

        lote = new Lote();
        lote.setId(10L);
        lote.setDescricao("Lote de Arte");
        lote.setPrecoMinimo(new BigDecimal("500.00"));
        lote.setResponsavel(donoLote);
    }

    // ── cadastrar ─────────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoQuandoDataInicioAposDataTermino() {
        LeilaoInput input = new LeilaoInput(
                TipoLeilao.ABERTO, 10L, 1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> leilaoService.cadastrar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("data de início deve ser anterior");
    }

    @Test
    void deveLancarExcecaoQuandoDataInicioNoPassado() {
        LeilaoInput input = new LeilaoInput(
                TipoLeilao.ABERTO, 10L, 1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> leilaoService.cadastrar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("data de início deve ser no futuro");
    }

    @Test
    void deveLancarExcecaoQuandoLoteNaoEncontrado() {
        LeilaoInput input = new LeilaoInput(
                TipoLeilao.ABERTO, 99L, 1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        when(loteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leilaoService.cadastrar(input))
                .isInstanceOf(LoteNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoResponsavelNaoEncontrado() {
        LeilaoInput input = new LeilaoInput(
                TipoLeilao.ABERTO, 10L, 99L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leilaoService.cadastrar(input))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveCadastrarLeilaoComSucesso() {
        LeilaoInput input = new LeilaoInput(
                TipoLeilao.ABERTO, 10L, 1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        when(loteRepository.findById(10L)).thenReturn(Optional.of(lote));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        leilaoService.cadastrar(input);

        verify(leilaoRepository).save(any(Leilao.class));
    }

    // ── buscarDetalhe ─────────────────────────────────────────────────────────

    @Test
    void deveLancarExcecaoQuandoLeilaoNaoEncontradoAoBuscarDetalhe() {
        when(leilaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leilaoService.buscarDetalhe(99L))
                .isInstanceOf(LeilaoNaoEncontradoException.class);
    }

    @Test
    void deveBuscarDetalheLeilaoAbertoEmAndamento_LancesVisiveis() {
        Leilao leilao = criarLeilao(1L, TipoLeilao.ABERTO,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        when(leilaoRepository.findById(1L)).thenReturn(Optional.of(leilao));

        LanceDetalhe lance = new LanceDetalhe(1L, "Fulano", new BigDecimal("600.00"), LocalDateTime.now().minusMinutes(10));
        when(lanceRepository.findDetalhesByLeilaoId(1L)).thenReturn(List.of(lance));

        LeilaoDetalhe detalhe = leilaoService.buscarDetalhe(1L);

        assertThat(detalhe.status()).isEqualTo(StatusLeilao.EM_ANDAMENTO);
        assertThat(detalhe.lancesVisiveis()).hasSize(1);
        assertThat(detalhe.lanceVencedor()).isNull();
        verify(lanceRepository).findDetalhesByLeilaoId(1L);
        verify(lanceRepository, never()).findVencedor(any());
    }

    @Test
    void deveBuscarDetalheLeilaoFechadoEmAndamento_LancesOcultos() {
        Leilao leilao = criarLeilao(2L, TipoLeilao.FECHADO,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        when(leilaoRepository.findById(2L)).thenReturn(Optional.of(leilao));

        LeilaoDetalhe detalhe = leilaoService.buscarDetalhe(2L);

        assertThat(detalhe.status()).isEqualTo(StatusLeilao.EM_ANDAMENTO);
        assertThat(detalhe.lancesVisiveis()).isEmpty();
        assertThat(detalhe.lanceVencedor()).isNull();
        verify(lanceRepository, never()).findDetalhesByLeilaoId(any());
        verify(lanceRepository, never()).findVencedor(any());
    }

    @Test
    void deveBuscarDetalheLeilaoEncerradoComVencedor() {
        Leilao leilao = criarLeilao(3L, TipoLeilao.ABERTO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        when(leilaoRepository.findById(3L)).thenReturn(Optional.of(leilao));

        LanceDetalhe lance = new LanceDetalhe(1L, "Vencedor", new BigDecimal("1000.00"), LocalDateTime.now().minusMinutes(90));
        when(lanceRepository.findDetalhesByLeilaoId(3L)).thenReturn(List.of(lance));

        LanceVencedorProjection projecao = mock(LanceVencedorProjection.class);
        when(projecao.getId()).thenReturn(1L);
        when(projecao.getNome()).thenReturn("Vencedor");
        when(projecao.getValor()).thenReturn(new BigDecimal("1000.00"));
        when(projecao.getDataHora()).thenReturn(LocalDateTime.now().minusMinutes(90));
        when(lanceRepository.findVencedor(3L)).thenReturn(Optional.of(projecao));

        LeilaoDetalhe detalhe = leilaoService.buscarDetalhe(3L);

        assertThat(detalhe.status()).isEqualTo(StatusLeilao.ENCERRADO);
        assertThat(detalhe.lancesVisiveis()).hasSize(1);
        assertThat(detalhe.lanceVencedor()).isNotNull();
        assertThat(detalhe.lanceVencedor().nomeUsuario()).isEqualTo("Vencedor");
        assertThat(detalhe.lanceVencedor().valor()).isEqualByComparingTo("1000.00");
    }

    @Test
    void deveBuscarDetalheLeilaoEncerradoSemVencedor_QuandoNaoHouveramLances() {
        Leilao leilao = criarLeilao(4L, TipoLeilao.FECHADO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        when(leilaoRepository.findById(4L)).thenReturn(Optional.of(leilao));
        when(lanceRepository.findDetalhesByLeilaoId(4L)).thenReturn(List.of());
        when(lanceRepository.findVencedor(4L)).thenReturn(Optional.empty());

        LeilaoDetalhe detalhe = leilaoService.buscarDetalhe(4L);

        assertThat(detalhe.status()).isEqualTo(StatusLeilao.ENCERRADO);
        assertThat(detalhe.lancesVisiveis()).isEmpty();
        assertThat(detalhe.lanceVencedor()).isNull();
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Leilao criarLeilao(Long id, TipoLeilao tipo, LocalDateTime inicio, LocalDateTime termino) {
        Leilao l = new Leilao();
        l.setId(id);
        l.setTipo(tipo);
        l.setDataInicio(inicio);
        l.setDataTermino(termino);
        l.setResponsavel(responsavel);
        l.setLote(lote);
        return l;
    }
}
