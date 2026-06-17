package com.leiloes.controller;

import com.leiloes.dto.input.LeilaoInput;
import com.leiloes.exception.LoteNaoEncontradoException;
import com.leiloes.exception.UsuarioNaoEncontradoException;
import com.leiloes.service.LeilaoService;
import com.leiloes.service.LoteService;
import com.leiloes.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/leiloes")
public class LeilaoController {

    private static final Logger log = LoggerFactory.getLogger(LeilaoController.class);

    private final LeilaoService leilaoService;
    private final LoteService loteService;
    private final UsuarioService usuarioService;

    public LeilaoController(LeilaoService leilaoService, LoteService loteService, UsuarioService usuarioService) {
        this.leilaoService = leilaoService;
        this.loteService = loteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("leiloes", leilaoService.listarTodos());
        return "leiloes/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("leilao", new LeilaoInput(null, null, null, null, null));
        model.addAttribute("lotes", loteService.listarTodos());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "leiloes/form";
    }

    @PostMapping("/novo")
    public String cadastrar(@ModelAttribute("leilao") @Valid LeilaoInput input, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("lotes", loteService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "leiloes/form";
        }

        try {
            leilaoService.cadastrar(input);
        } catch (LoteNaoEncontradoException | UsuarioNaoEncontradoException | IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("lotes", loteService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "leiloes/form";
        } catch (Exception ex) {
            log.error("Erro inesperado ao cadastrar leilão", ex);
            model.addAttribute("erro", "Ocorreu um erro inesperado. Tente novamente.");
            model.addAttribute("lotes", loteService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "leiloes/form";
        }

        return "redirect:/leiloes";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable("id") Long id, Model model) {
        model.addAttribute("leilao", leilaoService.buscarDetalhe(id));
        return "leiloes/detalhes";
    }
}
