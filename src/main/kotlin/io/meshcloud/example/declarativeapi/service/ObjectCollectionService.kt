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

    return objectCollectionRepository.findByName(createRequest.name)
        ?: return objectCollectionRepository.save(createRequest.toObjectCollection())
  }

  private fun validateCreationRequestOrThrow(createRequest: ObjectCollectionCreationRequest) {
    if (createRequest.name.isBlank()) {
      throw ObjectImportException("The name field of a ObjectCollection must not be empty!")
    }
  }

  fun findObjectCollectionByName(name: String): ObjectCollection? {
    return objectCollectionRepository.findByName(name)
  }
}
