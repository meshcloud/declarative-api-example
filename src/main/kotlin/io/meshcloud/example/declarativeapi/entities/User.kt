package io.meshcloud.example.declarativeapi.entities

import javax.persistence.*

@Entity
data class User (

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(unique = true)
  val userId: String,

  var email: String,

  var firstName: String,

  var lastName: String
): AbstractObjectEntity()