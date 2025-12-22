package com.engine.controller;

import com.engine.model.GameRoom;
import com.engine.repository.UserRepository;
import com.engine.service.GameRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    private final GameRoomService gameRoomService;
    private final UserRepository userRepository;
    @Value("${api.baseurl}") // Lee la propiedad del .properties
    private String ApiBaseUrl;

    public WebController(GameRoomService gameRoomService, UserRepository userRepository) {
        this.gameRoomService = gameRoomService;
        this.userRepository = userRepository;
    }

    /**
     * Handles the index page (lobby/dashboard) and loads room lists.
     * Accessible by anonymous users.
     */
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            try {
                String username = userDetails.getUsername();
                List<GameRoom> myRooms = gameRoomService.findRoomsByOwner(username);
                model.addAttribute("myRooms", myRooms);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error cargando tus salas.");
            }
        }

        List<GameRoom> publicRooms = gameRoomService.findAllPublicRooms();
        model.addAttribute("publicRooms", publicRooms);

        return "index";
    }

    /**
     * Processes the room creation form. Requires authentication.
     */
    @PostMapping("/create_room")
    public String createRoom(@RequestParam String name,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to create a room.");
            return "redirect:/";
        }

        try {
            String ownerUsername = userDetails.getUsername();
            GameRoom newRoom = gameRoomService.createNewRoom(name, password, ownerUsername);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Room '" + name + "' created successfully. Now go edit!");
            return "redirect:/editor?room=" + newRoom.getName();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating room: " + e.getMessage());
            return "redirect:/";
        }
    }

    /**
     * Serves the editor view. Loads room data (Logic, Design, Owner) from DB.
     */
    @GetMapping("/editor")
    public String editor(@RequestParam(required = false) String room, Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        model.addAttribute("roomId", room);
        // Pasamos el valor al modelo con el nombre que Thymeleaf espera
        model.addAttribute("ApiBaseUrl", ApiBaseUrl);

        String username = (userDetails != null) ? userDetails.getUsername() : null;
        model.addAttribute("currentUsername", username);

        if (room != null && !room.isEmpty()) {
            Optional<GameRoom> gameRoom = gameRoomService.findRoomByName(room);
            if (gameRoom.isPresent()) {
                GameRoom gr = gameRoom.get();
                model.addAttribute("roomOwner", gr.getOwnerId());
                model.addAttribute("serverLogic", gr.getServerLogic());

                // NUEVOS CAMPOS CARGADOS PARA LAS TRES ÁREAS DE TEXTO DEL EDITOR
                model.addAttribute("clientStructureHtml", gr.getClientStructureHtml());
                model.addAttribute("clientRenderScript", gr.getClientRenderScript());
                model.addAttribute("clientInputScript", gr.getClientInputScript());

                // --- CAMBIO CLAVE: Enviar el estado real ---
                // Si es null, enviamos un objeto vacío válido para evitar errores en JS
                String stateJson = (gr.getGameStateData() != null) ? gr.getGameStateData() : "{}";
                model.addAttribute("gameStateData", stateJson);

            } else {
                model.addAttribute("errorMessage", "Sala no encontrada.");
            }
        } else {
            // Valores por defecto si se accede a /editor sin ?room=ID
            model.addAttribute("serverLogic", "// Logic not loaded.");
            model.addAttribute("clientStructureHtml", "<div>Design not loaded.</div>");
            model.addAttribute("clientRenderScript", "// function onRender(state) {}");
            model.addAttribute("clientInputScript", "// document.getElementById('c').onmousedown = ...;");
        }

        /*
         * System.out.println("Usuario actual en editor: " + username);
         * System.out.println("Propietario de la sala: " +
         * model.getAttribute("roomOwner"));
         * System.out.println("Nombre de la sala: " + room);
         * System.out.println("Lógica del servidor cargada: " +
         * model.getAttribute("serverLogic"));
         * System.out.println("Estructura HTML cargada: " +
         * model.getAttribute("clientStructureHtml"));
         * System.out.println("Script de renderizado cargado: " +
         * model.getAttribute("clientRenderScript"));
         * System.out.println("Script de input cargado: " +
         * model.getAttribute("clientInputScript"));
         * System.out.println("-----");
         * 
         */
        return "editor";
    }

    /**
     * Serves the client play view. Loads the three raw design components for direct
     * execution.
     * REQUIERE: clientStructureHtml, clientRenderScript, clientInputScript
     */
    @GetMapping("/play")
    public String play(@RequestParam(required = false) String room, Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        model.addAttribute("roomId", room);
        // Pasamos el valor al modelo con el nombre que Thymeleaf espera
        model.addAttribute("ApiBaseUrl", ApiBaseUrl);

        String username = (userDetails != null) ? userDetails.getUsername() : null;
        model.addAttribute("currentUsername", username);

        // 1. Cargar los TRES COMPONENTES DEL DISEÑO (Crudos)
        if (room != null && !room.isEmpty()) {
            Optional<GameRoom> gameRoom = gameRoomService.findRoomByName(room);
            if (gameRoom.isPresent()) {
                GameRoom gr = gameRoom.get();

                // PASAMOS LOS TRES SCRIPTS DE FORMA SEPARADA PARA SU INYECCIÓN DIRECTA EN
                // PLAY.HTML
                model.addAttribute("clientStructureHtml", gr.getClientStructureHtml());
                model.addAttribute("clientRenderScript", gr.getClientRenderScript());
                model.addAttribute("clientInputScript", gr.getClientInputScript());

            } else {
                model.addAttribute("errorMessage", "Sala no encontrada.");
            }
        }

        return "play";
    }
}