package com.jydev.presentation.file

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

@Component
class MultipartFilePathResolver(
        @Value("\${file.media.base-root}") private val baseRoot: String
) {

    fun resolve(file: MultipartFile): Path {
        val fileName = file.originalFilename?.let { originalName ->
            val uuid = UUID.randomUUID().toString()
            val fileExtension = StringUtils.getFilenameExtension(originalName)

            if (fileExtension != null) "$uuid.$fileExtension" else uuid
        } ?: throw IllegalArgumentException("Original filename must not be null")

        return Path.of(baseRoot).run {
            val currentDateString = LocalDate.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE)

            this.resolve(currentDateString)
                    .resolve(abs(currentDateString.hashCode()).toString())
                    .resolve(fileName)
                    .normalize()
        }
    }
}