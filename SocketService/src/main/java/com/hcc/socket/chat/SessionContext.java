package com.hcc.socket.chat;

import com.hcc.socket.chat.infra.RoomRepository;
import com.hcc.socket.chat.model.Room;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.chat.service.ChatService;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SessionContext {

    @Getter
    private final Map<String, WebSocketSession> cachedSessions; // <userId, webSocketSession>
    @Getter
    private final Map<String, Room> cachedRooms;
    
    private final RoomRepository roomRepository;

    public SessionContext(RoomRepository roomRepository) {
        this.cachedSessions = new ConcurrentHashMap<>();
        this.cachedRooms = new ConcurrentHashMap<>();
        this.roomRepository = roomRepository;
    }

    public void putUser(String userId, WebSocketSession session) {
        cachedSessions.put(userId, session);
    }

    public WebSocketSession getSessionByUserId(String userId) {
        return cachedSessions.get(userId);
    }

    public void removeSessionByUserId(String userId){
        cachedSessions.remove(userId);
    }

    public List<WebSocketSession> getSessionsByRoomId(String roomId) {
        Room room = cachedRooms.get(roomId);
        if(room == null) {
            // todo: 방에 새 인원 추가될시 캐시된 유저리스트 업데이트 안됨. 처리해야함.
            room = roomRepository.findById(roomId).orElseThrow();
            cachedRooms.put(room.getId(), room);
        }
        List<User> users = room.getUsers();
        return users.stream()
                .map(User::getId) // 유저의 ID를 추출
                .map(cachedSessions::get) // 세션 맵에서 ID에 해당하는 세션을 가져옴
                .filter(Objects::nonNull) // null 세션은 제외
                .collect(Collectors.toList()); // 리스트로 반환

    }
}
