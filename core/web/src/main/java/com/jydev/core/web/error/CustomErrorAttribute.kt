package com.jydev.core.web.error

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class CustomErrorAttribute : DefaultErrorAttributes() {

    override fun getErrorAttributes(webRequest: WebRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {

        val request = (webRequest as ServletWebRequest).nativeRequest as HttpServletRequest
        val requestURI = request.getAttribute("jakarta.servlet.forward.request_uri") as String

        val errorOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        val extendedErrorAttributes = super.getErrorAttributes(webRequest, errorOptions)

        val errorMessage = extendedErrorAttributes["message"] as String

        val currentTimeUtc = Instant.now()
        val zonedDateTimeUtc = ZonedDateTime.ofInstant(currentTimeUtc, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")
        val timeString = zonedDateTimeUtc.format(formatter)

        val errorResponse = ErrorResponse(
            request.method,
            requestURI,
            errorMessage,
            timeString
        )

        return errorResponse.toMap()
            .toMutableMap()
    }
}