package com.hcc.socket.chat.infra;

import com.hcc.socket.chat.model.Room;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RoomRepository extends Repository<Room, String> {

    Room save(Room room);
    Optional<Room> findById(String roomId);
}
