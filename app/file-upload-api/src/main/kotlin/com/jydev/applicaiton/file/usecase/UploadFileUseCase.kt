package com.jydev.applicaiton.file.usecase

import com.jydev.applicaiton.file.StorageConfigurationResolver
import com.jydev.applicaiton.file.model.UploadFileModel
import com.jydev.applicaiton.file.model.command.UploadFileCommand
import com.jydev.domain.media.file.MediaFileAction
import com.jydev.domain.media.file.MediaFileMetaData
import com.jydev.domain.media.file.MediaFileMetaDataRepository
import com.jydev.domain.media.file.MediaFileProcessorFactory
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
        val storeAction = MediaFileAction.StoreMediaFile(storeFileCommand, storageConfiguration)

        processorFactory.create(storeAction)
            .executeAction()

        val fileUrl = constructFileURI(storageConfiguration.baseUrl, command.filePath).toString()

        val fileMetaData = MediaFileMetaData(
            storageType = command.storageType,
            originalFileName = command.originalFileName,
            fileSize = command.fileSize,
            fileUrl = fileUrl,
            filePath = command.filePath
        )

        fileMetaDataRepository.save(fileMetaData)

        return UploadFileModel(
            fileId = fileMetaData.id,
            fileUrl = fileUrl
        )
    }

    private fun createStoreFileCommand(command: UploadFileCommand) = MediaFileAction.StoreFileCommand(
        fileInputStream = command.fileInputStream,
        filePath = command.filePath,
        fileContentType = command.fileContentType,
        fileSize = command.fileSize
    )

    private fun constructFileURI(baseUrl: String, filePath: String): URI = try {
        URI(baseUrl).resolve(filePath)
    } catch (exception: URISyntaxException) {
        throw IllegalArgumentException("URI Parsing Error URL : %s".format(baseUrl), exception)
    }
}