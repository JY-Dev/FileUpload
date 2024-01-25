package com.jydev.applicaiton.file.usecase

import com.jydev.applicaiton.file.StorageConfigurationResolver
import com.jydev.applicaiton.file.model.command.DeleteFileCommand
import com.jydev.domain.media.file.FileStrategy
import com.jydev.domain.media.file.MediaFileAction
import com.jydev.domain.media.file.MediaFileMetaDataRepository
import com.jydev.domain.media.file.MediaFileProcessorFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Transactional
@Service
class DeleteFileUseCase(
    private val processorFactory: MediaFileProcessorFactory,
    private val storageConfigurationResolver: StorageConfigurationResolver,
    private val fileMetaDataRepository: MediaFileMetaDataRepository
) {

    operator fun invoke(command: DeleteFileCommand) {

        val fileMetaData = fileMetaDataRepository.findByIdOrNull(command.fileId)
                ?: throw IllegalArgumentException("FileMetaData not exist for fileUrl")

        val storageConfiguration = storageConfigurationResolver.resolve(fileMetaData.storageType)
        val deleteCommand = MediaFileAction.DeleteFileCommand(fileMetaData.filePath)
        val action = MediaFileAction.DeleteMediaFile(deleteCommand, storageConfiguration)

        processorFactory.create(action)
                .executeAction()

        fileMetaDataRepository.delete(fileMetaData)
    }
}