package com.leiloes.service;

import com.leiloes.domain.enums.TipoLeilao;
import com.leiloes.domain.model.Lance;
import com.leiloes.domain.model.Leilao;
import com.leiloes.domain.model.Lote;
import com.leiloes.domain.model.Usuario;
import com.leiloes.dto.input.LanceInput;
import com.leiloes.dto.output.LanceDetalhe;
import com.leiloes.exception.LanceInvalidoException;
import com.leiloes.exception.LeilaoNaoEmAndamentoException;
import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.repository.LanceRepository;
import com.leiloes.repository.LeilaoRepository;
import com.leiloes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanceServiceTest {

    @Mock LeilaoRepository leilaoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock LanceRepository lanceRepository;

    @InjectMocks LanceService lanceService;

    private Usuario responsavelLeilao;
    private Usuario responsavelLote;
    private Usuario lanceante;
    private Lote lote;

    @BeforeEach
    void setUp() {
        responsavelLeilao = criarUsuario(1L, "Leiloeiro", "leiloeiro@test.com", "11111111111");
        responsavelLote   = criarUsuario(2L, "Dono do Lote", "dono@test.com", "22222222222");
        lanceante         = criarUsuario(3L, "Lanceante", "lanceante@test.com", "33333333333");

        lote = new Lote();
        lote.setId(10L);
        lote.setDescricao("Lote de Teste");
        lote.setPrecoMinimo(new BigDecimal("100.00"));
        lote.setResponsavel(responsavelLote);
    }

    @Test
    void deveLancarExcecaoQuandoLeilaoNaoEncontrado() {
        when(leilaoRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(99L, lanceante.getId(), BigDecimal.valueOf(200))))
                .isInstanceOf(LeilaoNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoLeilaoEstiverEncerrado() {
        Leilao encerrado = criarLeilao(2L, TipoLeilao.ABERTO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));

        when(leilaoRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(encerrado));

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(2L, lanceante.getId(), BigDecimal.valueOf(200))))
                .isInstanceOf(LeilaoNaoEmAndamentoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoLeilaoAindaNaoComecou() {
        Leilao aguardando = criarLeilao(3L, TipoLeilao.ABERTO,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(leilaoRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(aguardando));

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(3L, lanceante.getId(), BigDecimal.valueOf(200))))
                .isInstanceOf(LeilaoNaoEmAndamentoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioEhResponsavelPeloLeilao() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(leilao.getId(), responsavelLeilao.getId(), BigDecimal.valueOf(200))))
                .isInstanceOf(LanceInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioEhResponsavelPeloLote() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(leilao.getId(), responsavelLote.getId(), BigDecimal.valueOf(200))))
                .isInstanceOf(LanceInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoValorAbaixoDoPrecoMinimo() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));

        // preço mínimo é 100, lance com 50
        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(leilao.getId(), lanceante.getId(), new BigDecimal("50.00"))))
                .isInstanceOf(LanceInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoValorNaoSuperaLanceAtualEmLeilaoAberto() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        Lance lanceAnterior = new Lance(LocalDateTime.now().minusMinutes(10), new BigDecimal("300.00"), lanceante, leilao);
        leilao.setLances(new ArrayList<>(List.of(lanceAnterior)));

        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));

        // 200 < 300 (lance atual) → inválido
        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(leilao.getId(), lanceante.getId(), new BigDecimal("200.00"))))
                .isInstanceOf(LanceInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lanceService.registrarLance(new LanceInput(leilao.getId(), 99L, BigDecimal.valueOf(200))))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveRegistrarLanceComSucessoEmLeilaoAberto() {
        Leilao leilao = criarLeilaoEmAndamento(TipoLeilao.ABERTO);
        when(leilaoRepository.findByIdForUpdate(leilao.getId())).thenReturn(Optional.of(leilao));
        when(usuarioRepository.findById(lanceante.getId())).thenReturn(Optional.of(lanceante));

        Lance salvo = new Lance(LocalDateTime.now(), new BigDecimal("200.00"), lanceante, leilao);
        salvo.setId(100L);
        when(lanceRepository.save(any(Lance.class))).thenReturn(salvo);

        LanceDetalhe resultado = lanceService.registrarLance(
                new LanceInput(leilao.getId(), lanceante.getId(), new BigDecimal("200.00")));

        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.nomeUsuario()).isEqualTo("Lanceante");
        assertThat(resultado.valor()).isEqualByComparingTo("200.00");
        verify(lanceRepository).save(any(Lance.class));
    }

    @Test
    void deveRegistrarLanceEmLeilaoFechadoSemSuperarLanceAtual() {
        // em leilões fechados os lances são secretos — não precisam superar o anterior
        Leilao leilaoFechado = criarLeilaoEmAndamento(TipoLeilao.FECHADO);
        Lance lanceAnterior = new Lance(LocalDateTime.now().minusMinutes(10), new BigDecimal("500.00"), lanceante, leilaoFechado);
        leilaoFechado.setLances(new ArrayList<>(List.of(lanceAnterior)));

        when(leilaoRepository.findByIdForUpdate(leilaoFechado.getId())).thenReturn(Optional.of(leilaoFechado));
        when(usuarioRepository.findById(lanceante.getId())).thenReturn(Optional.of(lanceante));

        Lance salvo = new Lance(LocalDateTime.now(), new BigDecimal("200.00"), lanceante, leilaoFechado);
        salvo.setId(101L);
        when(lanceRepository.save(any(Lance.class))).thenReturn(salvo);

        LanceDetalhe resultado = lanceService.registrarLance(
                new LanceInput(leilaoFechado.getId(), lanceante.getId(), new BigDecimal("200.00")));

        assertThat(resultado.id()).isEqualTo(101L);
        verify(lanceRepository).save(any(Lance.class));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Usuario criarUsuario(Long id, String nome, String email, String cpfCnpj) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setCpfCnpj(cpfCnpj);
        return u;
    }

    private Leilao criarLeilao(Long id, TipoLeilao tipo, LocalDateTime inicio, LocalDateTime termino) {
        Leilao l = new Leilao();
        l.setId(id);
        l.setTipo(tipo);
        l.setDataInicio(inicio);
        l.setDataTermino(termino);
        l.setResponsavel(responsavelLeilao);
        l.setLote(lote);
        l.setLances(new ArrayList<>());
        return l;
    }

    private Leilao criarLeilaoEmAndamento(TipoLeilao tipo) {
        return criarLeilao(
                tipo == TipoLeilao.ABERTO ? 1L : 5L,
                tipo,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1));
    }
}
