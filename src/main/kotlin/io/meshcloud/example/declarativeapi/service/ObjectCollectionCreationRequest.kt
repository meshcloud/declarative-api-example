package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.ObjectCollection

data class ObjectCollectionCreationRequest(
    val name: String
) {

  fun toObjectCollection(): ObjectCollection {
    return ObjectCollection(name = this.name)
  }
}
