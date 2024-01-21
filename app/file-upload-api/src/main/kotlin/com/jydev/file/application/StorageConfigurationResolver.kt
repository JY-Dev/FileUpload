package com.jydev.file.application

import com.jydev.file.application.config.LocalMediaStorageProperties
import com.jydev.file.application.config.S3MediaStorageProperties
import com.jydev.media.file.StorageConfiguration
import com.jydev.media.file.StorageType
import org.springframework.stereotype.Component

@Component
class StorageConfigurationResolver(
        private val s3MediaStorageProperties: S3MediaStorageProperties,
        private val localMediaStorageProperties: LocalMediaStorageProperties
) {

    fun resolve(storageType : StorageType) : StorageConfiguration = when (storageType) {
        StorageType.S3 -> StorageConfiguration.S3(s3MediaStorageProperties.bucket, s3MediaStorageProperties.baseUrl)
        StorageType.LOCAL -> StorageConfiguration.Local(localMediaStorageProperties.path, localMediaStorageProperties.baseUrl)
    }

}