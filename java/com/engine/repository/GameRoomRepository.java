package com.engine.repository;

import com.engine.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, String> {
    // Para listar las salas del usuario logueado
    List<GameRoom> findByOwnerId(String ownerId);

    // Si tu GameRoom tiene is_public=true, también necesitarías:
    // List<GameRoom> findByIsPublicTrue(); 
}