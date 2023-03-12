package io.meshcloud.example.declarativeapi.repositories

import io.meshcloud.example.declarativeapi.entities.Group
import org.springframework.data.jpa.repository.JpaRepository
import javax.print.DocFlavor.STRING

interface GroupRepository: JpaRepository<Group, Long> {

  fun findByName(name: String): Group?

  fun findAllByObjectCollectionId(collectionId: Long): Set<Group>

}