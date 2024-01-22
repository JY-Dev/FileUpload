package com.jydev.domain.media.file

sealed class StorageConfiguration(val baseUrl : String) {
    data class S3(val s3Bucket : String, val cloudFrontUrl : String) : StorageConfiguration(cloudFrontUrl)
    data class Local(val storagePath : String, val localUrl : String) : StorageConfiguration(localUrl)
}