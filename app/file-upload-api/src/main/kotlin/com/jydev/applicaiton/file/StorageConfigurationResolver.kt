package com.jydev.applicaiton.file

import com.jydev.applicaiton.file.config.LocalMediaStorageProperties
import com.jydev.applicaiton.file.config.S3MediaStorageProperties
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