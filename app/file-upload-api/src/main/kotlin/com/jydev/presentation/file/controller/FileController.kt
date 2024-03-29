package com.jydev.presentation.file.controller

import com.jydev.applicaiton.file.usecase.DeleteFileUseCase
import com.jydev.applicaiton.file.usecase.UploadFileUseCase
import com.jydev.applicaiton.file.model.command.DeleteFileCommand
import com.jydev.applicaiton.file.model.command.UploadFileCommand
import com.jydev.presentation.file.MultipartFilePathResolver
import com.jydev.presentation.file.model.response.UploadFileResponse
import com.jydev.domain.media.file.StorageType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalArgumentException

@RestController
class FileController(
        private val multipartFilePathResolver: MultipartFilePathResolver,
        private val uploadFileUseCase : UploadFileUseCase,
        private val deleteFileUseCase : DeleteFileUseCase
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/files")
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

        return UploadFileResponse(
                fileId = model.fileId,
                fileUrl = model.fileUrl
        )
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/files/{fileId}")
    fun deleteFile(@PathVariable fileId: Long) {

        val command = DeleteFileCommand(
                fileId = fileId
        )

        deleteFileUseCase.invoke(command)
    }
}