package com.leiloes.controller;

import com.leiloes.dto.input.LoteInput;
import com.leiloes.repository.BemRepository;
import com.leiloes.repository.UsuarioRepository;
import com.leiloes.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService loteService;
    private final UsuarioRepository usuarioRepository;
    private final BemRepository bemRepository;

    public LoteController(LoteService loteService, UsuarioRepository usuarioRepository, BemRepository bemRepository) {
        this.loteService = loteService;
        this.usuarioRepository = usuarioRepository;
        this.bemRepository = bemRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listarTodos());
        return "lotes/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("lote", new LoteInput(null, null, null, null));
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("bens", bemRepository.findAll());
        return "lotes/form";
    }

    @PostMapping("/novo")
    public String cadastrar(@ModelAttribute("lote") @Valid LoteInput input, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("bens", bemRepository.findAll());
            return "lotes/form";
        }
        
        try {
            loteService.cadastrar(input);
        } catch (Exception ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("bens", bemRepository.findAll());
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
