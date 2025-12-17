package com.engine.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_rooms")
public class GameRoom {

    @Id
    @Column(nullable = false)
    private String id; 

    @Column(nullable = false)
    private String name;

    @Column(name = "server_logic", columnDefinition = "TEXT")
    private String serverLogic;

    // ELIMINADO: client_html. Sustituido por los siguientes tres campos:
    
    // Contiene HTML puro, CSS, y la etiqueta <canvas>
    @Column(name = "client_structure_html", columnDefinition = "TEXT")
    private String clientStructureHtml; 
    
    // Contiene la función onRender(state) en bruto
    @Column(name = "client_render_script", columnDefinition = "TEXT")
    private String clientRenderScript; 

    // Contiene la lógica JS para manejar eventos e inputs (ej: canvas.onmousemove)
    @Column(name = "client_input_script", columnDefinition = "TEXT")
    private String clientInputScript; 

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "password")
    private String password;
    
    @Column(name = "is_public", columnDefinition = "boolean default true")
    private Boolean isPublic;
    
    @Column(name = "game_state_data", columnDefinition = "TEXT")
    private String gameStateData; 
}