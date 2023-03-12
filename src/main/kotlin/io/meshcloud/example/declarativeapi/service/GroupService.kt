package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.Group
import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.model.ApiGroup
import io.meshcloud.example.declarativeapi.model.IApiObject
import io.meshcloud.example.declarativeapi.repositories.GroupRepository
import io.meshcloud.example.declarativeapi.repositories.UserRepository
import io.meshcloud.web.meshobjects.ObjectDeletionResult
import org.springframework.stereotype.Component

@Component
class GroupService(
  private val userRepository: UserRepository,
  private val groupRepository: GroupRepository
): ExistingObjectProcessor<ApiGroup, Group>() {

  fun createOrUpdateGroup(
    apiGroup: ApiGroup,
    objectCollection: ObjectCollection? = null
  ): Group {

    val existingGroup = groupRepository.findByName(apiGroup.metadata.name)

    val expectedMembers = apiGroup.spec.members
      .mapNotNull { userRepository.findByUserId(it) }

    if (existingGroup != null) {
      objectCollection?.assignObject(existingGroup)
      existingGroup.members = expectedMembers

      return groupRepository.save(existingGroup)
    }

    val group = Group(
      name = apiGroup.metadata.name,
      members = expectedMembers
    )

    objectCollection?.assignObject(group)

    return groupRepository.save(group)
  }

  override fun matches(definition: ApiGroup, entity: Group): Boolean {
    return definition.metadata.name.equals(entity.name, ignoreCase = true)
  }

  override fun toMeaningfulIdentifierForEntity(entity: Group): String {
    return "meshGroup[${entity.name}]"
  }

  override fun provideExisting(meshObjectCollection: ObjectCollection): Set<Group> {
    return groupRepository.findAllByObjectCollectionId(meshObjectCollection.id)
  }

  override fun deleteEntity(entity: Group): ObjectDeletionResult {
    return try {
      groupRepository.delete(entity)
      ObjectDeletionResult(toMeaningfulIdentifierForEntity(entity), true)
    } catch (ex: Exception) {
      ObjectDeletionResult(toMeaningfulIdentifierForEntity(entity), false)
    }
  }

  override fun filter(definitions: List<IApiObject>): List<ApiGroup> {
    return definitions.filterIsInstance<ApiGroup>()
  }
}