package com.jydev.media.file

import java.io.InputStream

class MediaFileStrategy internal constructor(
        private val fileRepository: MediaFileRepository,
        private val storageConfiguration: StorageConfiguration
) {

    fun save(command : StoreFileCommand) {
        fileRepository.save(
                storageConfiguration = storageConfiguration,
                command = command
        )
    }

    fun delete(command: DeleteFileCommand) {
        fileRepository.delete(
                storageConfiguration = storageConfiguration,
                command = command
        )
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