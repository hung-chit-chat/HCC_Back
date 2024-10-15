package com.hcc.socket.chat.service;

import com.hcc.socket.chat.model.relationship.Relationship;
import com.hcc.socket.chat.model.User;
import com.hcc.socket.chat.infra.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;

    public List<Relationship> getFriends(User actor) {
        return relationshipRepository.findByIdActor(actor);
    }

    public void addFriend(Relationship relationship) {
        Relationship saved = relationshipRepository.save(relationship);
    }
}
