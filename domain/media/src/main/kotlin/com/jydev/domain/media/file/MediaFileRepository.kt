package com.jydev.domain.media.file

interface MediaFileRepository {
    val storageConfigClass: Class<out StorageConfiguration>

    fun save(
        storageConfiguration: StorageConfiguration,
        command : FileStrategy.StoreFileCommand
    )

    fun delete(
        storageConfiguration: StorageConfiguration,
        command : FileStrategy.DeleteFileCommand
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