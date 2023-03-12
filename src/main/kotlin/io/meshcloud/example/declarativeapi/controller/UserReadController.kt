package io.meshcloud.example.declarativeapi.controller

import io.meshcloud.example.declarativeapi.entities.User
import io.meshcloud.example.declarativeapi.repositories.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserReadController(
  val userRepository: UserRepository
) {

  @GetMapping("/users")
  fun findAll(): ResponseEntity<List<User>> {
    return ResponseEntity.ok(userRepository.findAll())
  }
}