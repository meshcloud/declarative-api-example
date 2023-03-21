package io.meshcloud.example.declarativeapi.controller

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.repositories.ObjectCollectionRepository
import io.meshcloud.example.declarativeapi.service.ObjectCollectionCreationRequest
import io.meshcloud.example.declarativeapi.service.ObjectCollectionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ObjectCollectionController(
  val objectCollectionRepository: ObjectCollectionRepository,
  val objectCollectionService: ObjectCollectionService
) {

  @GetMapping("/objectCollections")
  fun findAll(): ResponseEntity<List<ObjectCollection>> {
    return ResponseEntity.ok(objectCollectionRepository.findAll())
  }

  @PostMapping("/objectCollections")
  fun create(
    @RequestBody request: ObjectCollectionCreationRequest
  ): ResponseEntity<*> {
    val objectCollection = objectCollectionService.ensureExistsOrThrowOnIllegalUpdate(request)

    return ResponseEntity(objectCollection, HttpStatus.CREATED)
  }
}
