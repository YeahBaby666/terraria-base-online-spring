package com.engine.service;

import com.engine.model.GameRoom;
import com.engine.repository.GameRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;

    public GameRoomService(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
    }

    @Transactional
    public GameRoom createNewRoom(String name, String password, String ownerUsername) {
        
        // Valores base
        String defaultLogic = "// Server Logic\nfunction onUpdate(s){s.tick = (s.tick||0)+1;}\nfunction onInput(i,s){}";
        String defaultStructure = "<div id='game-view' style='width:100%; height:100%; background:#111;'><canvas id='c'></canvas><div style='position:absolute; top:10px; left:10px; color:white;'>Score: <span id='score-val'>0</span></div></div>";
        String defaultRenderScript = "function onRender(state) {\n  // Lógica de dibujado\n  const c = document.getElementById('c');\n  const ctx = c.getContext('2d');\n  ctx.fillStyle = '#111'; ctx.fillRect(0,0,c.width,c.height);\n  // ... dibujar jugadores\n}";
        String defaultInputScript = "document.getElementById('c').onmousedown = (e) => { \n  // Enviar input simple\n  SIM_emit({ action: 'click', x: e.offsetX });\n};";


        GameRoom newRoom = GameRoom.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .serverLogic(defaultLogic)
                .clientStructureHtml(defaultStructure)
                .clientRenderScript(defaultRenderScript)
                .clientInputScript(defaultInputScript)
                .ownerId(ownerUsername)
                .password(password)
                .isPublic(true)
                .gameStateData(null)
                .build();

        return gameRoomRepository.save(newRoom);
    }
    
    /**
     * Guarda el estado completo de la sala (Lógica, Diseño y Scripts).
     */
    @Transactional
    public void updateRoomContent(String roomId, String serverLogic, String structure, String renderScript, String inputScript) {
        gameRoomRepository.findById(roomId).ifPresent(room -> {
            room.setServerLogic(serverLogic);
            room.setClientStructureHtml(structure);
            room.setClientRenderScript(renderScript);
            room.setClientInputScript(inputScript);
            gameRoomRepository.save(room);
        });
    }

    // [ ... findRoomsByOwner(), findAllPublicRooms(), findRoomById() ... ]
    
    public List<GameRoom> findRoomsByOwner(String username) {
        return gameRoomRepository.findByOwnerId(username);
    }

    public List<GameRoom> findAllPublicRooms() {
        return gameRoomRepository.findAll();
    }
    
    public Optional<GameRoom> findRoomById(String roomId) {
        return gameRoomRepository.findById(roomId);
    }
}