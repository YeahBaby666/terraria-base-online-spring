package com.engine.repository;

import com.engine.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, String> {
    // Para listar las salas del usuario logueado
    List<GameRoom> findByOwnerId(String ownerId);

    // Si tu GameRoom tiene is_public=true, también necesitarías:
    // List<GameRoom> findByIsPublicTrue(); 

    /**
     * Busca una sala de juego por el nombre de la sala (nombreSala).
     * Spring Data JPA genera automáticamente: SELECT * FROM game_room WHERE name = ?
     * * @param nombreSala El nombre de usuario que es dueño de la sala.
     * @return Un Optional que contiene la GameRoom si se encuentra, o Optional.empty() si no.
     */
    Optional<GameRoom> findByName(String nombreSala);
}