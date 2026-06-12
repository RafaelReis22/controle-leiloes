package com.leiloes.exception;

public class LoteNaoEncontradoException extends RuntimeException {
    public LoteNaoEncontradoException(Long id) {
        super("Lote não encontrado: " + id);
    }
}
