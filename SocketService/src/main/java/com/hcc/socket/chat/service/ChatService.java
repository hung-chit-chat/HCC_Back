package com.hcc.socket.chat.service;

import com.hcc.socket.chat.infra.RoomRepository;
import com.hcc.socket.chat.model.Room;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.comm.IdentifierFactory;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final IdentifierFactory identifierFactory;
    private final RoomRepository roomRepository;

    public void createRoom(User user, @Nullable List<String> invitedUserIds) {
        Room room = user.createRoom(identifierFactory, invitedUserIds);
        Room savedRoom = roomRepository.save(room);
    }
}
