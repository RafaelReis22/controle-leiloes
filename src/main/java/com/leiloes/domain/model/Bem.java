package com.leiloes.domain.model;

import jakarta.persistence.*;

/**
 * Entidade Bem — item físico que compõe um lote leiloado.
 * Pertence a uma Categoria.
 */
@Entity
@Table(name = "bens")
public class Bem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao_breve", nullable = false, length = 200)
    private String descricaoBreve;

    @Column(name = "descricao_completa", columnDefinition = "TEXT")
    private String descricaoCompleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;


    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDescricaoBreve() { 
        return descricaoBreve; 
    }
    public void setDescricaoBreve(String descricaoBreve) { 
        this.descricaoBreve = descricaoBreve; 
    }

    public String getDescricaoCompleta() { 
        return descricaoCompleta; 
    }
    public void setDescricaoCompleta(String descricaoCompleta) { 
        this.descricaoCompleta = descricaoCompleta; 
    }

    public Categoria getCategoria() { 
        return categoria; 
    }
    public void setCategoria(Categoria categoria) { 
        this.categoria = categoria; 
    }
}
