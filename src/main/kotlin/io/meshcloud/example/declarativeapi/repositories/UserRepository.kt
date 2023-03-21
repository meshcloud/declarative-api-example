package io.meshcloud.example.declarativeapi.repositories

import io.meshcloud.example.declarativeapi.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

  fun findByUserId(userId: String): User?

  fun findAllByObjectCollectionId(collectionId: Long): Set<User>

}