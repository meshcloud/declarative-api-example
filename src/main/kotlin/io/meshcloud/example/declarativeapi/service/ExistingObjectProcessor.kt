package io.meshcloud.example.declarativeapi.service

import io.meshcloud.example.declarativeapi.entities.ObjectCollection
import io.meshcloud.example.declarativeapi.model.IApiObject
import io.meshcloud.web.meshobjects.ObjectDeletionResult

/**
 * TODO a lot of those methods seem to be only used internally and thus could be declared
 *   protected. This will however break some tests. But you should not be required to test
 *   internal of a class. Unit test only the input -> output, not internal functions. The
 *   many exposed methods make it really hard to understand how this class and its descendants
 *   work and whats the entry point for meaningful functionality is.
 */
abstract class ExistingObjectProcessor<O : IApiObject, E> {

  /**
   * Returns all existing entities of the correct type in the meshObjectCollection.
   */
  abstract fun provideExisting(meshObjectCollection: ObjectCollection): Set<E>

  /**
   * Return true, if the provided meshObject definition will match the given entity,
   * in the sense that importing the definition could lead to this entity.
   * False otherwise.
   */
  abstract fun matches(definition: O, entity: E): Boolean

  /**
   * This mimics the meaningfulIdentifier field on IApiObjects.
   * Another idea would be to implement a cast to IApiObject,
   * but this seems to be an overkill to just get the meaningfulIdentifier.
   */
  abstract fun toMeaningfulIdentifierForEntity(entity: E): String

  /**
   * Filters out all entities, that are in the given meshObjectCollection, but
   * do not match any of the provided definitions. So by a declarative deletion approach
   * they will need to be removed.
   */
  internal fun filterEntities(definitions: List<IApiObject>, meshObjectCollection: ObjectCollection): List<E> {
    val flattenedDefinitions = flatten(filter(definitions))
    return provideExisting(meshObjectCollection).filter { entity ->
      flattenedDefinitions.all { meshObject ->
        !matches(
          meshObject,
          entity
        )
      }
    }
  }

  /**
   * Filters the list of objects and returns only those objects which match the type being considered
   */
  abstract fun filter(definitions: List<IApiObject>): List<O>

  /**
   * Flatten nested meshObjects. For example, customer(User/Group)Binding which can have several subjects
   */
  open fun flatten(definitions: List<O>): List<O> {
    return definitions
  }

  /**
   * Finds out what entities need to be removed and then removes them.
   */
  fun deleteNotSpecifiedEntities(
    definitions: List<IApiObject>,
    meshObjectCollection: ObjectCollection
  ): List<ObjectDeletionResult> {
    return filterEntities(definitions, meshObjectCollection).map { deleteEntity(it) }.toList()
  }

  /**
   * Concrete implementation of entity deletion.
   */
  abstract fun deleteEntity(entity: E): ObjectDeletionResult
}
