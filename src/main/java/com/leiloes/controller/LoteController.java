package com.leiloes.controller;

import com.leiloes.dto.input.LoteInput;
import com.leiloes.exception.UsuarioNaoEncontradoException;
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
@RequestMapping("/lotes")
public class LoteController {

    private static final Logger log = LoggerFactory.getLogger(LoteController.class);

    private final LoteService loteService;
    private final UsuarioService usuarioService;

    public LoteController(LoteService loteService, UsuarioService usuarioService) {
        this.loteService = loteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listarTodos());
        return "lotes/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("lote", new LoteInput(null, null, null, null));
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("bens", loteService.listarBens());
        return "lotes/form";
    }

    @PostMapping("/novo")
    public String cadastrar(@ModelAttribute("lote") @Valid LoteInput input, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("bens", loteService.listarBens());
            return "lotes/form";
        }

        try {
            loteService.cadastrar(input);
        } catch (UsuarioNaoEncontradoException | IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("bens", loteService.listarBens());
            return "lotes/form";
        } catch (Exception ex) {
            log.error("Erro inesperado ao cadastrar lote", ex);
            model.addAttribute("erro", "Ocorreu um erro inesperado. Tente novamente.");
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("bens", loteService.listarBens());
            return "lotes/form";
        }

        return "redirect:/lotes";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable("id") Long id, Model model) {
        model.addAttribute("lote", loteService.buscar(id));
        return "lotes/detalhes";
    }
}
