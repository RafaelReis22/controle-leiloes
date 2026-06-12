package com.leiloes.exception;

public class LeilaoEncerradoException extends RuntimeException {
    public LeilaoEncerradoException(String msg) {
        super(msg);
    }

    public LeilaoEncerradoException(Long id) {
        super("Leilão encerrado: " + id);
    }
}
