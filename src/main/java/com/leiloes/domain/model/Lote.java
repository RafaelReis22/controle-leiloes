package com.leiloes.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Lote — Domain Model Pattern.
 *
 * Agrupa um ou mais bens para ser leiloado com preço mínimo.
 */
@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "preco_minimo", nullable = false)
    private BigDecimal precoMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false)
    private Usuario responsavel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lotes_bens",
        joinColumns = @JoinColumn(name = "id_lote"),
        inverseJoinColumns = @JoinColumn(name = "id_bem"))
    private List<Bem> bens = new ArrayList<>();



    public void adicionarBem(Bem bem) {
        if (bem != null) {
            this.bens.add(bem);
        }
    }

    public int quantidadeDeBens() {
        return bens.size();
    }

    

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDescricao() { 
        return descricao; 
    }
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public BigDecimal getPrecoMinimo() { 
        return precoMinimo; 
    }

    public void setPrecoMinimo(BigDecimal precoMinimo) { 
        this.precoMinimo = precoMinimo; 
    }

    public Usuario getResponsavel() { 
        return responsavel; 
    }

    public void setResponsavel(Usuario responsavel) { 
        this.responsavel = responsavel; 
    }

    public List<Bem> getBens() { 
        return bens; 
    }
    
    public void setBens(List<Bem> bens) { 
        this.bens = bens; 
    }
}
