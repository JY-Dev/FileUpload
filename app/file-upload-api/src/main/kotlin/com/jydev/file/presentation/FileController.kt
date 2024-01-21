package com.jydev.file.presentation

import com.jydev.file.application.UploadFileUseCase
import com.jydev.file.application.model.command.UploadFileCommand
import com.jydev.file.presentation.model.response.UploadFileResponse
import com.jydev.media.file.StorageType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalArgumentException

@RestController
class FileController(
        private val multipartFilePathResolver: MultipartFilePathResolver,
        private val uploadFileUseCase : UploadFileUseCase
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("api/file")
    fun uploadFile(file: MultipartFile): UploadFileResponse {

        val filePath = multipartFilePathResolver.resolve(file)
        val contentType = file.contentType ?: throw IllegalArgumentException("File content type not exist")
        val originalFileName = file.originalFilename?: throw IllegalArgumentException("File OriginalName not exist")

        val command = UploadFileCommand(
                fileInputStream = file.inputStream,
                filePath = filePath.toString(),
                originalFileName = originalFileName,
                fileContentType = contentType,
                fileSize = file.size,
                storageType = StorageType.LOCAL
        )

        val model = uploadFileUseCase(command)

        return UploadFileResponse(model.fileUrl)
    }
}