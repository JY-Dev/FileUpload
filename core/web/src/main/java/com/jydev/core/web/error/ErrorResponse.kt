package com.jydev.core.web.error

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class ErrorResponse(
    val httpMethod: String,
    val requestUrl: String,
    val cause: String,
    val occurrenceTime: String
)

fun ErrorResponse.toMap(): Map<String, Any> = mutableMapOf<String, Any>().apply {
    put("httpMethod", httpMethod)
    put("requestUrl", requestUrl)
    put("cause", cause)
    put("occurrenceTime", occurrenceTime)
}
