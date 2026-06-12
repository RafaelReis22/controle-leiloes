package com.leiloes.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Lance — representa uma oferta de um Usuário para um Leilão.
 */
@Entity
@Table(name = "lances")
public class Lance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_leilao", nullable = false)
    private Leilao leilao;

    

    public Lance() {}

    public Lance(LocalDateTime dataHora, BigDecimal valor, Usuario usuario, Leilao leilao) {
        this.dataHora = dataHora;
        this.valor = valor;
        this.usuario = usuario;
        this.leilao = leilao;
    }

   

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public LocalDateTime getDataHora() { 
        return dataHora; 
    }
    public void setDataHora(LocalDateTime dataHora) { 
        this.dataHora = dataHora; 
    }

    public BigDecimal getValor() { 
        return valor; 
    }
    public void setValor(BigDecimal valor) { 
        this.valor = valor; 
    }

    public Usuario getUsuario() { 
        return usuario; 
    }
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }

    public Leilao getLeilao() { 
        return leilao; 
    }
    public void setLeilao(Leilao leilao) { 
        this.leilao = leilao; 
    }
}
