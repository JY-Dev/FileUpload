package com.jydev.domain.media.file

import java.io.InputStream

interface FileStrategy {
    fun save(command : StoreFileCommand)
    fun delete(command: DeleteFileCommand)

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