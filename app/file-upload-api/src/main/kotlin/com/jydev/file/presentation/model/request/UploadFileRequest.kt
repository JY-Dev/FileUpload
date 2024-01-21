package com.jydev.file.presentation.model.request

import org.springframework.web.multipart.MultipartFile

data class UploadFileRequest(
        val file : MultipartFile,
        val fileName : String
)
