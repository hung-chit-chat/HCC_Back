package com.hcc.socket.mock;

import com.hcc.socket.chat.infra.RoomRepository;
import com.hcc.socket.chat.model.Room;
import com.hcc.socket.comm.IdentifierFactory;
import com.hcc.socket.comm.UUIDFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeRoomRepository implements RoomRepository {

    private final IdentifierFactory identifierFactory = new UUIDFactory();
    private final List<Room> entities = new ArrayList<>();

    public List<Room> getEntities() {
        System.out.println(entities);
        return entities;
    }

    @Override
    public Room save(Room target) {
        if(target.getId() == null) {
            throw new NullPointerException();
        }

        entities.removeIf(entity -> Objects.equals(entity.getId(), target.getId()));
        entities.add(target);
        return target;
    }

    @Override
    public Optional<Room> findById(String id) {
        return entities.stream().filter(entity -> entity.getId().equals(id)).findAny();
    }
}
