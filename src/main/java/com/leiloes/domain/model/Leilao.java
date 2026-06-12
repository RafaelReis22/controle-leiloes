package com.leiloes.domain.model;

import com.leiloes.domain.enums.StatusLeilao;
import com.leiloes.domain.enums.TipoLeilao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Entidade Leilao — Domain Model Pattern (classe mais importante do sistema).
 */
@Entity
@Table(name = "leiloes")
public class Leilao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLeilao tipo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_termino", nullable = false)
    private LocalDateTime dataTermino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false)
    private Usuario responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote lote;

    @OneToMany(mappedBy = "leilao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("valor DESC, dataHora ASC")
    private List<Lance> lances = new ArrayList<>();

   

    public StatusLeilao getStatus() {
        LocalDateTime agora = LocalDateTime.now();
        if (!agora.isBefore(dataTermino)) return StatusLeilao.ENCERRADO;   // agora >= dataTermino
        if (!agora.isBefore(dataInicio))  return StatusLeilao.EM_ANDAMENTO;
        return StatusLeilao.AGUARDANDO;
    }

    public boolean estaEmAndamento() {
        return getStatus() == StatusLeilao.EM_ANDAMENTO;
    }

    public boolean estaEncerrado() {
        return getStatus() == StatusLeilao.ENCERRADO;
    }

    public List<Lance> getLancesVisiveis() {
        StatusLeilao status = getStatus();
        if (tipo == TipoLeilao.ABERTO || status == StatusLeilao.ENCERRADO) {
            return Collections.unmodifiableList(lances);
        }
        return Collections.emptyList();
    }

    public Optional<Lance> getLanceVencedor() {
        if (getStatus() != StatusLeilao.ENCERRADO) return Optional.empty();
        return lances.stream().findFirst();
    }

    

    public Long getId() { 
        return id; 
    } 
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public TipoLeilao getTipo() { 
        return tipo; 
    } 
    
    public void setTipo(TipoLeilao tipo) { 
        this.tipo = tipo; 
    }

    public LocalDateTime getDataInicio() { 
        return dataInicio; 
    } 
    
    public void setDataInicio(LocalDateTime dataInicio) { 
        this.dataInicio = dataInicio; 
    }

    public LocalDateTime getDataTermino() { 
        return dataTermino; 
    }

    public void setDataTermino(LocalDateTime dataTermino) { 
        this.dataTermino = dataTermino; 
    }

    public Usuario getResponsavel() { 
        return responsavel; 
    }

    public void setResponsavel(Usuario responsavel) { 
        this.responsavel = responsavel; 
    }

    public Lote getLote() { 
        return lote; 
    }

    public void setLote(Lote lote) { 
        this.lote = lote; 
    }

    public List<Lance> getLances() { 
        return lances; 
    }
    
    public void setLances(List<Lance> lances) { 
        this.lances = lances; 
    }
}
