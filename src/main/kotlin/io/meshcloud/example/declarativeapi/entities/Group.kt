package io.meshcloud.example.declarativeapi.entities

import javax.persistence.*

@Entity
data class Group (

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(unique = true)
  val name: String,

  @ManyToMany
  @JoinTable(
    name = "Group_User",
    joinColumns = [(JoinColumn(name = "groupId"))],
    inverseJoinColumns = [(JoinColumn(name = "userId"))],
    uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("groupId", "userId")))]
  )
  var members: List<User>
): AbstractObjectEntity()