package com.hcc.socket.chat.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
public class Room {

    @Id
    private String id;

    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRoom> userRooms = new ArrayList<>();

    public List<User> getUsers() {
        return userRooms.stream()
                .map(UserRoom::getUser)
                .collect(Collectors.toList());
    }
}
