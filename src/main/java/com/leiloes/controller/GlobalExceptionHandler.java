package com.leiloes.controller;

import com.leiloes.exception.LeilaoNaoEncontradoException;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({LeilaoNaoEncontradoException.class, LoteNaoEncontradoException.class, UsuarioNaoEncontradoException.class})
    public String handleNaoEncontrado(RuntimeException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "error/404";
    }
}
