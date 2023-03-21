package io.meshcloud.example.declarativeapi.controller

import io.meshcloud.example.declarativeapi.entities.User
import io.meshcloud.example.declarativeapi.repositories.UserRepository
import io.meshcloud.example.declarativeapi.service.ObjectImportResult
import io.meshcloud.example.declarativeapi.service.ObjectImportService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DeclarativeImportController(
  val objectImportService: ObjectImportService
) {

  @PutMapping("/objects")
  fun import(
    @RequestBody body: String,
    @RequestParam(value = "objectCollection", required = false) objectCollectionName: String?
    ): ResponseEntity<*> {
    val importResult = objectImportService.import(body, objectCollectionName)

    return if (containsError(importResult)) {
      makeErrorResponse(importResult)
    } else {
      ResponseEntity.ok(importResult)
    }
  }

  /**
   * Determine the appropriate error code if an error was detected during import of the meshObjects.
   */
  private fun makeErrorResponse(importResult: List<ObjectImportResult>): ResponseEntity<*> {
    val errorCodes = importResult.mapNotNull { it.resultCode }.toSet()

    return if (errorCodes.contains(ObjectImportResult.ResultCode.SERVER_ERROR)) {
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(importResult)
    } else {
      // The other codes usually mean that some referenced entity was not found so most likely some wrong
      // input.
      ResponseEntity.unprocessableEntity().body(importResult)
    }
  }

  private fun containsError(importResult: List<ObjectImportResult>): Boolean {
    return importResult.any { it.status in listOf(ObjectImportResult.ImportStatus.FAILED, ObjectImportResult.ImportStatus.DELETE_FAILED) }
  }
}