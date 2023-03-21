package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.entities.ObjectCollectionAssignmentException
import io.meshcloud.example.declarativeapi.model.ApiGroup
import io.meshcloud.example.declarativeapi.model.ApiUser
import io.meshcloud.example.declarativeapi.model.IApiObject
import io.meshcloud.example.declarativeapi.model.ObjectKind
import org.springframework.stereotype.Component

@Component
class ObjectImportService(
    private val mappingService: ObjectMapperService,
    private val objectCollectionService: ObjectCollectionService,
    private val userService: UserService,
    private val groupService: GroupService
) {

  fun import(
      body: String,
      objectCollectionName: String? = null
  ): List<ObjectImportResult> {

    val objectCollection = try {
      findObjectCollectionIfExistsOrThrow(objectCollectionName)
    } catch (e: IllegalArgumentException) {
      return buildSingleResultWithError("ObjectCollection", e.message)
    }

    val objects = mappingService.mapToObjectList(body)

    return deleteObjectsIfCollectionUsed(objects, objectCollection) +
        createOrUpdateObjects(objects, objectCollection)
  }

  private fun deleteObjectsIfCollectionUsed(
      objects: List<IApiObject>,
      objectCollection: ObjectCollection?
  ): List<ObjectImportResult> {
    if (objectCollection == null) {
      return mutableListOf()
    }

    return exitingObjectProcessors().flatMap { processor ->
      processor.deleteNotSpecifiedEntities(objects, objectCollection).map {
        ObjectImportResult(
            apiObject = it.meaningfulIdentifier,
            status = if (it.success)
              ObjectImportResult.ImportStatus.DELETED
            else
              ObjectImportResult.ImportStatus.DELETE_FAILED,
            message = it.message
        )
      }
    }
  }

  private fun exitingObjectProcessors(): List<ExistingObjectProcessor<out IApiObject, *>> = listOf(
      userService,
      groupService
  )

  private fun createOrUpdateObjects(
      objects: List<IApiObject>,
      objectCollection: ObjectCollection?
  ): List<ObjectImportResult> {

    return objects.map { obj ->
      try {
        when (obj.kind) {
          ObjectKind.User -> userService.createOrUpdateUser(
              obj as ApiUser,
              objectCollection
          )
          ObjectKind.Group -> groupService.createOrUpdateGroup(
              obj as ApiGroup,
              objectCollection
          )
        }
      } catch (e: ObjectCollectionAssignmentException) {
        ObjectImportResult(
            apiObject = obj.meaningfulIdentifier,
            status = ObjectImportResult.ImportStatus.FAILED,
            message = e.message,
        )
      } catch (e: ObjectImportException) {
        ObjectImportResult(
            apiObject = obj.meaningfulIdentifier,
            status = ObjectImportResult.ImportStatus.FAILED,
            message = e.message,
            resultCode = e.resultCode
        )
      } catch (e: Exception) {
        ObjectImportResult(
            apiObject = obj.meaningfulIdentifier,
            status = ObjectImportResult.ImportStatus.FAILED,
            message = e.message,
            resultCode = ObjectImportResult.ResultCode.SERVER_ERROR
        )
      }
    }
  }

  private fun findObjectCollectionIfExistsOrThrow(
      objectCollectionName: String?
  ): ObjectCollection? {
    if (objectCollectionName == null) {
      return null
    }

    val name = objectCollectionName.trim()
    if (name.isBlank()) {
      throw IllegalArgumentException("ObjectCollection name cannot be blank. (Can be null)")
    }

    return objectCollectionService.findObjectCollectionByName(name)
        ?: throw IllegalArgumentException("ObjectCollection $name does not exist.")
  }

  private fun buildSingleResultWithError(objectName: String, message: String?): List<ObjectImportResult> {
    return listOf(
        ObjectImportResult(
            apiObject = objectName,
            status = ObjectImportResult.ImportStatus.FAILED,
            message = message
        )
    )
  }
}