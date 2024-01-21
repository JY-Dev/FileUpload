package com.jydev.applicaiton.file.usecase

import com.jydev.applicaiton.file.StorageConfigurationResolver
import com.jydev.applicaiton.file.model.command.DeleteFileCommand
import com.jydev.media.file.MediaFileAction
import com.jydev.media.file.MediaFileMetaDataRepository
import com.jydev.media.file.MediaFileStrategy
import com.jydev.media.file.infra.MediaFileProcessorFactory
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
        val deleteCommand = MediaFileStrategy.DeleteFileCommand(fileMetaData.filePath)
        val action = MediaFileAction.DeleteMediaFile(deleteCommand)

        processorFactory.create(action, storageConfiguration)
                .executeAction()

        fileMetaDataRepository.delete(fileMetaData)
    }
}