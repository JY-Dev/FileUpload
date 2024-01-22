package com.jydev.domain.media.file

class MediaFileStrategy internal constructor(
    private val fileRepository: MediaFileRepository,
    private val storageConfiguration: StorageConfiguration
) : FileStrategy {

    override fun save(command: FileStrategy.StoreFileCommand) {
        fileRepository.save(
            storageConfiguration = storageConfiguration,
            command = command
        )
    }

    override fun delete(command: FileStrategy.DeleteFileCommand) {
        fileRepository.delete(
            storageConfiguration = storageConfiguration,
            command = command
        )
    }

}