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
) : ExistingObjectProcessor<ApiGroup, Group>() {

  fun createOrUpdateGroup(
      apiGroup: ApiGroup,
      objectCollection: ObjectCollection? = null
  ): ObjectImportResult {

    val existingGroup = groupRepository.findByName(apiGroup.metadata.name)

    val resolvedMembers = apiGroup.spec.members
        .map { it to userRepository.findByUserId(it) }

    val expectedMembers = resolvedMembers.mapNotNull { it.second }

    val group = existingGroup?.apply {
      if (this.objectCollection != null
          && this.objectCollection?.name != objectCollection?.name) {
        throw ObjectImportException(
            resultCode = ObjectImportResult.ResultCode.OBJECT_COLLECTION_CONFLICT,
            message = "Cannot import group ${apiGroup.metadata.name}, as it already exists and is " +
            "assigned to a different object collection!")
      }

      existingGroup.members = expectedMembers
    } ?: Group(
        name = apiGroup.metadata.name,
        members = expectedMembers
    )

    objectCollection?.assignObject(group)

    groupRepository.save(group)

    val missingMembers = resolvedMembers.filter { it.second == null }.map { it.first }

    return ObjectImportResult(
        apiObject = group.name,
        status = ObjectImportResult.ImportStatus.SUCCESS,
        message = "The Group has been imported, but the following members are missing " +
            "as those users don't exist: ${missingMembers}"
    )
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