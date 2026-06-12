package com.leiloes.exception;

public class LeilaoNaoEncontradoException extends RuntimeException {
    public LeilaoNaoEncontradoException(Long id) {
        super("Leilão não encontrado: " + id);
    }
}
