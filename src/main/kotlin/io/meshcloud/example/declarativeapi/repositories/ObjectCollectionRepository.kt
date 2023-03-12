package io.meshcloud.example.declarativeapi.repositories

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import org.springframework.data.jpa.repository.JpaRepository

interface ObjectCollectionRepository : JpaRepository<ObjectCollection, Long> {
  fun findByName(name: String): ObjectCollection?
}