package com.jydev.file.application.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file.media.storage.local")
data class LocalMediaStorageProperties(
        val path : String,
        val baseUrl: String
)