package com.example.alarmservice.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.NoArgsConstructor

@Entity
@Table(name = "notification")
class NotificationEntity(
    @Id
    val id: String,

    @Column(nullable = false)
    val memberId: String,

    @Column(nullable = false)
    val notificationType: String
) : BaseEntity() {

}