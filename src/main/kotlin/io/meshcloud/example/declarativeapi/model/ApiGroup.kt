package io.meshcloud.example.declarativeapi.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class ApiGroup(
  override val apiVersion: String,
  override val kind: ObjectKind = ObjectKind.Group,
  val metadata: Metadata,
  val spec: Spec
) : IApiObject {

  data class Metadata(val name: String)

  data class Spec(
    val members: List<String> = emptyList()
  )

  @JsonIgnore
  override val meaningfulIdentifier = "Group[${metadata.name}]"

}
