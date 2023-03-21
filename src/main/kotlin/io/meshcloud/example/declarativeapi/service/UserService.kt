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
  ): ObjectImportResult {

    val existingUser = userRepository.findByUserId(apiUser.metadata.userId)

    val user = existingUser?.apply {
      if (existingUser.objectCollection != null
          && existingUser.objectCollection?.name != objectCollection?.name) {
        throw ObjectImportException(
            resultCode = ObjectImportResult.ResultCode.OBJECT_COLLECTION_CONFLICT,
            message = "Cannot import user ${apiUser.metadata.userId}, as it already exists and is " +
            "assigned to a different object collection!")
      }

      lastName = apiUser.spec.lastName
      firstName = apiUser.spec.firstName
      email = apiUser.spec.email
    } ?: User(
        userId = apiUser.metadata.userId,
        email = apiUser.spec.email,
        firstName = apiUser.spec.firstName,
        lastName = apiUser.spec.lastName
    )

    objectCollection?.assignObject(user)

    userRepository.save(user)

    return ObjectImportResult(
        apiObject = user.userId,
        status = ObjectImportResult.ImportStatus.SUCCESS
    )

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
