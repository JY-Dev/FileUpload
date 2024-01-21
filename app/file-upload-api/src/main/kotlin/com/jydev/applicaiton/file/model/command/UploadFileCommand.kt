package com.jydev.applicaiton.file.model.command

import com.jydev.media.file.StorageType
import java.io.InputStream

data class UploadFileCommand(
        val fileInputStream : InputStream,
        val filePath : String,
        val originalFileName : String,
        val fileContentType : String,
        val fileSize : Long,
        val storageType : StorageType
)
