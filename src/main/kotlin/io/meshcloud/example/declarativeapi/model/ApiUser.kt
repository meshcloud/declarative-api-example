package io.meshcloud.example.declarativeapi.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class ApiUser(
  override val apiVersion: String,
  override val kind: ObjectKind = ObjectKind.User,
  val metadata: Metadata,
  val spec: Spec
) : IApiObject {

  data class Metadata(val userId: String)

  data class Spec(
    val email: String,
    val firstName: String,
    val lastName: String
  )

  @JsonIgnore
  override val meaningfulIdentifier = "User[${metadata.userId}]"

}
