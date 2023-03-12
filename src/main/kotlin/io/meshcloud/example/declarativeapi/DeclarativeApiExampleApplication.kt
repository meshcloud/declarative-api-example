package io.meshcloud.example.declarativeapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeclarativeApiExampleApplication

fun main(args: Array<String>) {
	runApplication<DeclarativeApiExampleApplication>(*args)
}
