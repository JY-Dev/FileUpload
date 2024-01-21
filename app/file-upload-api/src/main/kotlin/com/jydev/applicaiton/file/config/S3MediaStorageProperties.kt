package com.jydev.applicaiton.file.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "file.media.storage.s3")
data class S3MediaStorageProperties(
        val bucket: String,
        val baseUrl: String
)