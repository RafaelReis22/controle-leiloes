package com.leiloes.controller;

import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({LeilaoNaoEncontradoException.class, LoteNaoEncontradoException.class, UsuarioNaoEncontradoException.class})
    public String handleNaoEncontrado(RuntimeException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenerico(Exception ex, Model model) {
        log.error("Erro inesperado não tratado", ex);
        model.addAttribute("mensagem", "Ocorreu um erro inesperado. Tente novamente.");
        return "error/500";
    }
}
