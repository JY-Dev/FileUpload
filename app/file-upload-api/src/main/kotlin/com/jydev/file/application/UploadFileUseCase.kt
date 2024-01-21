package com.jydev.file.application

import com.jydev.file.application.model.UploadFileModel
import com.jydev.file.application.model.command.UploadFileCommand
import com.jydev.media.file.MediaFileAction
import com.jydev.media.file.MediaFileMetaData
import com.jydev.media.file.MediaFileMetaDataRepository
import com.jydev.media.file.MediaFileStrategy
import com.jydev.media.file.infra.MediaFileProcessorFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.URISyntaxException

@Transactional
@Service
class UploadFileUseCase(
        private val processorFactory: MediaFileProcessorFactory,
        private val storageConfigurationResolver: StorageConfigurationResolver,
        private val fileMetaDataRepository: MediaFileMetaDataRepository
) {

    operator fun invoke(command: UploadFileCommand): UploadFileModel {
        val storageConfiguration = storageConfigurationResolver.resolve(command.storageType)
        val storeFileCommand = createStoreFileCommand(command)
        val storeAction = MediaFileAction.StoreMediaFile(storeFileCommand)

        processorFactory.create(storeAction, storageConfiguration)
                .executeAction()

        val fileUrl = constructFileURI(storageConfiguration.baseUrl, command.filePath).toString()

        val fileMetaData = MediaFileMetaData(
                storageType = command.storageType,
                originalFileName = command.originalFileName,
                fileSize = command.fileSize,
                fileUrl = fileUrl
        )

        fileMetaDataRepository.save(fileMetaData)

        return UploadFileModel(fileUrl)
    }

    private fun createStoreFileCommand(command: UploadFileCommand) = MediaFileStrategy.StoreFileCommand(
            fileInputStream = command.fileInputStream,
            filePath = command.filePath,
            fileContentType = command.fileContentType,
            fileSize = command.fileSize
    )

    private fun constructFileURI(baseUrl: String, filePath: String): URI = try {
        URI(baseUrl).resolve(filePath)
    } catch (exception : URISyntaxException) {
        throw IllegalArgumentException("URI Parsing Error URL : %s".format(baseUrl), exception)
    }
}