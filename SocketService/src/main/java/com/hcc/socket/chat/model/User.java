package com.hcc.socket.chat.model;

import com.hcc.socket.comm.IdentifierFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "Users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    String id;

    @Transient
    WebSocketSession session;

    String connServer;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRoom> userRooms = new ArrayList<>();

    // `rooms` 리스트를 통해 해당 사용자가 속한 방을 가져올 수 있음
    public List<Room> getRooms() {
        return userRooms.stream()
                .map(UserRoom::getRoom)
                .collect(Collectors.toList());
    }

    // Room 생성 메서드
    public Room createRoom(IdentifierFactory identifierFactory, @Nullable List<String> invitedUserIds) {
        Room room = Room.builder()
                .id(identifierFactory.generate())
                .build();
        UserRoom userRoom = UserRoom.builder()
                .user(this)
                .room(room)
                .build();
        this.userRooms.add(userRoom);
        room.getUserRooms().add(userRoom); // Room에도 반대쪽 관계 추가

        if(invitedUserIds != null) {
            invitedUserIds.forEach(invitedUserId -> {
                User invitedUser = User.builder().id(invitedUserId).build();
                UserRoom userRoom2 = UserRoom.builder()
                        .user(invitedUser)
                        .room(room)
                        .build();
                room.getUserRooms().add(userRoom2);
            });
        }
        return room;
    }
}
