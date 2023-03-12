package io.meshcloud.example.declarativeapi.controller

import io.meshcloud.example.declarativeapi.entities.Group
import io.meshcloud.example.declarativeapi.repositories.GroupRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupReadController(
  val groupRepository: GroupRepository
) {

  @GetMapping("/groups")
  fun findAll(): ResponseEntity<List<Group>> {
      return ResponseEntity.ok(groupRepository.findAll())
  }
}