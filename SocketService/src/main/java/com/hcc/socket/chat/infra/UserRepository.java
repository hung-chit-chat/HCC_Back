package com.hcc.socket.chat.infra;


import com.hcc.socket.chat.model.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User, String> {

    Optional<User> findById(String id);

    User save(User user);
}
