package io.meshcloud.example.declarativeapi.entities

import javax.persistence.*

@Entity
data class ObjectCollection (

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(length = 100, unique = true)
  var name: String,
) {
  fun assignObject(objectEntity: AbstractObjectEntity) {
    val existingCollection = objectEntity.objectCollection
    if (existingCollection == null) {
      objectEntity.objectCollection = this
    } else {
      existingCollection.takeIf { it.id != this.id }?.let {
        throw ObjectCollectionAssignmentException("ObjectCollection ${this.name}: " +
            "Cannot assign object with other ObjectCollection: ${it.name}")
      }
    }
  }
}