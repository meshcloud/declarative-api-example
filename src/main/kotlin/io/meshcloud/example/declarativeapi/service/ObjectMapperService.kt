package io.meshcloud.example.declarativeapi.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.meshcloud.example.declarativeapi.model.ApiGroup
import io.meshcloud.example.declarativeapi.model.ApiUser
import io.meshcloud.example.declarativeapi.model.IApiObject
import io.meshcloud.example.declarativeapi.model.ObjectKind
import org.springframework.stereotype.Component

@Component
class ObjectMapperService {

  fun mapToObjectList(body: String): List<IApiObject> {
    return convertJsonToMap(body).map {
          mapToObject(it as HashMap<String, Any>)
        }.sortedWith(IApiObject.ORDER)
  }

  private fun mapToObject(data: HashMap<String, Any>): IApiObject {
    val clazz = when (ObjectKind.findKind(data)) {
      ObjectKind.User -> ApiUser::class.java
      ObjectKind.Group -> ApiGroup::class.java
    }

    return objectMapper.convertValue(data, clazz)
  }

  private fun convertJsonToMap(json: String): List<Map<String, Any>> {
    val list = objectReader.readValue(json, List::class.java)

    return list.filterIsInstance<HashMap<String, Any>>()
  }

  companion object {
    private val objectMapper = ObjectMapper().apply {
          registerModules(KotlinModule.Builder().build())
          registerModule(JavaTimeModule())
          configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

    private val objectReader = ObjectMapper()
  }
}