package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.entities.User
import io.meshcloud.example.declarativeapi.model.ApiUser
import io.meshcloud.example.declarativeapi.model.IApiObject
import io.meshcloud.example.declarativeapi.repositories.UserRepository
import io.meshcloud.web.meshobjects.ObjectDeletionResult
import org.springframework.stereotype.Component

@Component
class UserService(
  private val userRepository: UserRepository
) : ExistingObjectProcessor<ApiUser, User>() {

  fun createOrUpdateUser(
    apiUser: ApiUser,
    objectCollection: ObjectCollection? = null
  ): User {

    val existingUser = userRepository.findByUserId(apiUser.metadata.userId)

    if (existingUser != null) {
      objectCollection?.assignObject(existingUser)

      existingUser.lastName = apiUser.spec.lastName
      existingUser.firstName = apiUser.spec.firstName
      existingUser.email = apiUser.spec.email
      existingUser.objectCollection = objectCollection

      return userRepository.save(existingUser)
    }

    val user = User(
      userId = apiUser.metadata.userId,
      email = apiUser.spec.email,
      firstName = apiUser.spec.firstName,
      lastName = apiUser.spec.lastName
    )

    objectCollection?.assignObject(user)

    return userRepository.save(user)
  }

  override fun matches(definition: ApiUser, entity: User): Boolean {
    return definition.metadata.userId.equals(entity.userId, ignoreCase = true)
  }

  override fun toMeaningfulIdentifierForEntity(entity: User): String {
    return "meshUser[${entity.userId}]"
  }

  override fun provideExisting(meshObjectCollection: ObjectCollection): Set<User> {
    return userRepository.findAllByObjectCollectionId(meshObjectCollection.id)
  }

  override fun deleteEntity(entity: User): ObjectDeletionResult {
    return try {
      userRepository.delete(entity)
      ObjectDeletionResult(toMeaningfulIdentifierForEntity(entity), true)
    } catch (ex: Exception) {
      ObjectDeletionResult(toMeaningfulIdentifierForEntity(entity), false)
    }
  }

  override fun filter(definitions: List<IApiObject>): List<ApiUser> {
    return definitions.filterIsInstance<ApiUser>()
  }

}
