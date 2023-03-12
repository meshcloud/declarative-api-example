package io.meshcloud.example.declarativeapi.service

class ObjectImportException(
  message: String,
  cause: Throwable? = null,
  val resultCode: ObjectImportResult.ResultCode? = null
) : Exception(message, cause)