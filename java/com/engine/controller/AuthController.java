package com.engine.controller;

import com.engine.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Login page es el mismo index
    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, 
                           @RequestParam String password, 
                           RedirectAttributes redirectAttributes) {
        try {
            userService.registrarUsuario(username, password);
            // Mensaje de éxito para mostrar en el HTML
            redirectAttributes.addFlashAttribute("successMessage", "¡Cuenta creada con éxito! Ahora inicia sesión.");
            // Redirige al login (que es el index) pero pasando parámetros para activar la vista correcta si quieres
            return "redirect:/login"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar: " + e.getMessage());
            return "redirect:/login?error=register";
        }
    }
}