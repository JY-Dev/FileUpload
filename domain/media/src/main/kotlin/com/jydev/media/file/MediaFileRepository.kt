package com.jydev.media.file

interface MediaFileRepository {
    val storageConfigClass: Class<out StorageConfiguration>

    fun save(
            storageConfiguration: StorageConfiguration,
            command : MediaFileStrategy.StoreFileCommand
    )

    fun delete(
            storageConfiguration: StorageConfiguration,
            command : MediaFileStrategy.DeleteFileCommand
    )

    fun resolveConfiguration(configuration : StorageConfiguration) : Boolean {
        return storageConfigClass.isInstance(configuration)
    }

    fun <T : StorageConfiguration> castConfiguration(configuration: StorageConfiguration, castConfiguration: Class<T>): T {
        if (!castConfiguration.isInstance(configuration)) {
            throw IllegalArgumentException("StorageConfiguration type mismatch")
        }
        return castConfiguration.cast(configuration)
    }
}