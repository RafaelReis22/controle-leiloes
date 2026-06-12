package com.leiloes.domain.model;

import jakarta.persistence.*;

/**
 * Entidade Categoria — classifica os bens leiloados.
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

   

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id;
    }
    
    public String getNome() { 
        return nome; 
    }

    public void setNome(String nome) { 
        this.nome = nome; 
    }
}
