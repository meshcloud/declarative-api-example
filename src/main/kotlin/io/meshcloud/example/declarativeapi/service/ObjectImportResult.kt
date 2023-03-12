package io.meshcloud.example.declarativeapi.service

data class ObjectImportResult(
  val apiObject: String,
  val status: ImportStatus,
  val resultCode: ResultCode? = null,
  val message: String? = null,
  val remarks: List<String>? = null
) {
  enum class ImportStatus {
    SUCCESS,
    FAILED,
    DELETED,
    DELETE_FAILED,
  }

  enum class ResultCode {
    USER_NOT_FOUND,
    GROUP_NOT_FOUND,
    SERVER_ERROR
  }
}