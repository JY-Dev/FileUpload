package com.jydev.domain.media.file

import java.io.InputStream

sealed class MediaFileAction(open val storageConfiguration: StorageConfiguration) {
    abstract fun execute(fileRepository: MediaFileRepository)

    data class StoreMediaFile(
            val command: StoreFileCommand,
            override val storageConfiguration: StorageConfiguration
    ) : MediaFileAction(storageConfiguration) {
        override fun execute(fileRepository: MediaFileRepository) {
            fileRepository.save(storageConfiguration, command)
        }
    }

    data class DeleteMediaFile(
            val command: DeleteFileCommand,
            override val storageConfiguration: StorageConfiguration
    ) : MediaFileAction(storageConfiguration) {
        override fun execute(fileRepository: MediaFileRepository) {
            fileRepository.delete(storageConfiguration, command)
        }
    }

    data class StoreFileCommand(
            val fileInputStream: InputStream,
            val filePath: String,
            val fileContentType: String,
            val fileSize: Long
    )

    data class DeleteFileCommand(
            val filePath: String
    )
}