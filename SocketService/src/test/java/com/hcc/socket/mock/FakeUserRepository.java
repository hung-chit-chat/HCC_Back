package com.hcc.socket.mock;

import com.hcc.socket.chat.model.User;
import com.hcc.socket.chat.infra.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeUserRepository implements UserRepository {

    private final List<User> entities = new ArrayList<>();

    public List<User> getEntities() {
        return entities;
    }

    @Override
    public User save(User target) {
        if(target.getId() == null) {
            throw new NullPointerException();
        }

        entities.removeIf(entity -> Objects.equals(entity.getId(), target.getId()));
        entities.add(target);
        return target;
    }

    @Override
    public Optional<User> findById(String id) {
        return entities.stream().filter(entity -> entity.getId().equals(id)).findAny();
    }
}
