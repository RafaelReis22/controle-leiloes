package com.leiloes.domain.model;

import jakarta.persistence.*;

/**
 * Entidade Usuario — Domain Model Pattern.
 *
 * Encapsula lógica para determinar se o documento é CPF ou CNPJ.
 * Apenas leitura: usuários são pré-cadastrados no sistema.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

  
    /** Retorna true se o documento é um CPF (11 dígitos) */
    public boolean isCpf() {
        return cpfCnpj.replaceAll("\\D", "").length() == 11;
    }

    /** Retorna true se o documento é um CNPJ (14 dígitos) */
    public boolean isCnpj() {
        return cpfCnpj.replaceAll("\\D", "").length() == 14;
    }

    

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

    public String getCpfCnpj() { 
        return cpfCnpj; 
    }
    public void setCpfCnpj(String cpfCnpj) { 
        this.cpfCnpj = cpfCnpj; 
    }

    public String getEmail() {
         return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
}
