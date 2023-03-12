package io.meshcloud.web.meshobjects

data class ObjectDeletionResult (
    val meaningfulIdentifier: String,
    val success: Boolean,
    val message: String? = null
)