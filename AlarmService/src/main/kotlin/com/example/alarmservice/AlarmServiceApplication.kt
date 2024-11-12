package com.example.alarmservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class AlarmServiceApplication

fun main(args: Array<String>) {
	runApplication<AlarmServiceApplication>(*args)
}
