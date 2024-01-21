package com.jydev.applicaiton.file.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file.media.storage.local")
data class LocalMediaStorageProperties(
        val path : String,
        val baseUrl: String
)