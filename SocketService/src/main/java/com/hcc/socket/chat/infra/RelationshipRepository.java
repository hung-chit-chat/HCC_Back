package com.hcc.socket.chat.infra;

import com.hcc.socket.chat.model.relationship.Relationship;
import com.hcc.socket.chat.model.relationship.RelationshipId;
import com.hcc.socket.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipId> {

    List<Relationship> findByIdActor(User actor);
}
