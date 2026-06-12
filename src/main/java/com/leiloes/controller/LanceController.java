package com.leiloes.controller;

import com.leiloes.dto.input.LanceInput;
import com.leiloes.repository.UsuarioRepository;
import com.leiloes.service.LanceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/leiloes/{id}/lance")
public class LanceController {

    private final LanceService lanceService;
    private final UsuarioRepository usuarioRepository;

    public LanceController(LanceService lanceService, UsuarioRepository usuarioRepository) {
        this.lanceService = lanceService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String exibirFormularioLance(@PathVariable("id") Long idLeilao, Model model) {
        model.addAttribute("lance", new LanceInput(idLeilao, null, null));
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("idLeilao", idLeilao);
        return "lances/form";
    }

    @PostMapping
    public String registrarLance(@PathVariable("id") Long idLeilao,
                                 @ModelAttribute("lance") @Valid LanceInput input,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("idLeilao", idLeilao);
            return "lances/form";
        }
        
        try {
            lanceService.registrarLance(input);
        } catch (Exception ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("idLeilao", idLeilao);
            return "lances/form";
        }
        
        return "redirect:/leiloes/" + idLeilao;
    }
}
