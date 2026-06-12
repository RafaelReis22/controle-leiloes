package com.leiloes.controller;

import com.leiloes.dto.input.LeilaoInput;
import com.leiloes.repository.UsuarioRepository;
import com.leiloes.service.LeilaoService;
import com.leiloes.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/leiloes")
public class LeilaoController {

    private final LeilaoService leilaoService;
    private final LoteService loteService;
    private final UsuarioRepository usuarioRepository;

    public LeilaoController(LeilaoService leilaoService, LoteService loteService, UsuarioRepository usuarioRepository) {
        this.leilaoService = leilaoService;
        this.loteService = loteService;
        this.usuarioRepository = usuarioRepository;
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
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "leiloes/form";
    }

    @PostMapping("/novo")
    public String cadastrar(@ModelAttribute("leilao") @Valid LeilaoInput input, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("lotes", loteService.listarTodos());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "leiloes/form";
        }
        
        try {
            leilaoService.cadastrar(input);
        } catch (Exception ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("lotes", loteService.listarTodos());
            model.addAttribute("usuarios", usuarioRepository.findAll());
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
