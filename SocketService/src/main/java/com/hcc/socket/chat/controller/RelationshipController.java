package com.hcc.socket.chat.controller;

import com.hcc.socket.chat.service.RelationshipService;
import com.hcc.socket.chat.model.relationship.Relationship;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.security.jwt.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;


    @GetMapping("getFriends")
    public ResponseEntity<List<Relationship>> getFriends() {
        UserData userData = (UserData) SecurityContextHolder.getContext().getAuthentication();
        User actor = User.builder().id(userData.getId()).build();

        List<Relationship> friends = relationshipService.getFriends(actor);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("addFriend")
    public ResponseEntity<String> addFriend(Relationship relationship) {
        relationshipService.addFriend(relationship);
        return ResponseEntity.ok("success");
    }

    
}
