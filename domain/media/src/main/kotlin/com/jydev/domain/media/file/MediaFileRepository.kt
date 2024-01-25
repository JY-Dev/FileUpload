package com.jydev.domain.media.file

interface MediaFileRepository {
    val storageConfigClass: Class<out StorageConfiguration>

    fun save(
        storageConfiguration: StorageConfiguration,
        command : MediaFileAction.StoreFileCommand
    )

    fun delete(
        storageConfiguration: StorageConfiguration,
        command : MediaFileAction.DeleteFileCommand
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