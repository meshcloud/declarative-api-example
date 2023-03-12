package io.meshcloud.example.declarativeapi.entities

import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractObjectEntity {

  @ManyToOne
  @JoinColumn(name = "'objectCollectionId'")
  open var objectCollection: ObjectCollection? = null
}