package com.hcc.socket.chat.model.relationship;

import com.hcc.socket.chat.model.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class RelationshipId implements Serializable {

    @ManyToOne
    private User actor; // 관계의 주체

    @ManyToOne
    private User receiver; // 대상
}
