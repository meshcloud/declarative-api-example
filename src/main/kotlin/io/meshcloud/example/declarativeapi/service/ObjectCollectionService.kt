package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.repositories.ObjectCollectionRepository
import org.springframework.stereotype.Service

@Service
class ObjectCollectionService(
  private val objectCollectionRepository: ObjectCollectionRepository
) {

  fun ensureExistsOrThrowOnIllegalUpdate(createRequest: ObjectCollectionCreationRequest): ObjectCollection {
    validateCreationRequestOrThrow(createRequest)

    val existingObjectCollection = objectCollectionRepository.findByName(createRequest.name)
      ?: return objectCollectionRepository.save(createRequest.toObjectCollection())

    ensureCorrectOwnerOrThrow(existingObjectCollection, createRequest.owner)
    return existingObjectCollection
  }

  private fun validateCreationRequestOrThrow(createRequest: ObjectCollectionCreationRequest) {
    if (createRequest.name.isBlank() || createRequest.owner.isBlank()) {
      throw ObjectImportException("The name and owner field of a ObjectCollection must not be empty!")
    }
  }

  private fun ensureCorrectOwnerOrThrow(collection: ObjectCollection, owner: String) {
    if (collection.owner != owner) {
      throw ObjectImportException(
        "Cannot access ObjectCollection ${collection.name}. " +
            "It belongs to a different owner: ${collection.owner}"
      )
    }
  }

  fun findObjectCollectionByName(name: String): ObjectCollection? {
    return objectCollectionRepository.findByName(name)
  }
}
