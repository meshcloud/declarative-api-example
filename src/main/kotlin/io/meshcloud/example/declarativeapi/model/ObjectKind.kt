package io.meshcloud.example.declarativeapi.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class ObjectKind(val value: String) {

  @JsonProperty("user")
  User("user"),

  @JsonProperty("group")
  Group("group");

  companion object {
    fun findKind(data: Map<String, Any>): ObjectKind {
      val value = data["kind"] ?: throw InvalidObjectKindException("Kind can't be null or undefined.")

      if (value !is String) {
        throw InvalidObjectKindException("Kind $value does not exist. Please use another one.")
      }

      return fromValue(value)
    }

    private fun fromValue(value: String): ObjectKind {
      return values().singleOrNull { it.value == value }
        ?: throw InvalidObjectKindException("No matching kind for [$value]")
    }
  }
}
