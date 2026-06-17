package com.leiloes.controller;

import com.leiloes.dto.input.LanceInput;
import com.leiloes.exception.LanceInvalidoException;
import com.leiloes.exception.LeilaoNaoEmAndamentoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.service.LanceService;
import com.leiloes.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/leiloes/{id}/lance")
public class LanceController {

    private static final Logger log = LoggerFactory.getLogger(LanceController.class);

    private final LanceService lanceService;
    private final UsuarioService usuarioService;

    public LanceController(LanceService lanceService, UsuarioService usuarioService) {
        this.lanceService = lanceService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String exibirFormularioLance(@PathVariable("id") Long idLeilao, Model model) {
        model.addAttribute("lance", new LanceInput(idLeilao, null, null));
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("idLeilao", idLeilao);
        return "lances/form";
    }

    @PostMapping
    public String registrarLance(@PathVariable("id") Long idLeilao,
                                 @ModelAttribute("lance") @Valid LanceInput input,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("idLeilao", idLeilao);
            return "lances/form";
        }

        try {
            lanceService.registrarLance(input);
        } catch (LanceInvalidoException | LeilaoNaoEmAndamentoException | UsuarioNaoEncontradoException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("idLeilao", idLeilao);
            return "lances/form";
        } catch (Exception ex) {
            log.error("Erro inesperado ao registrar lance para leilão id={}", idLeilao, ex);
            model.addAttribute("erro", "Ocorreu um erro inesperado. Tente novamente.");
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("idLeilao", idLeilao);
            return "lances/form";
        }

        return "redirect:/leiloes/" + idLeilao;
    }
}
