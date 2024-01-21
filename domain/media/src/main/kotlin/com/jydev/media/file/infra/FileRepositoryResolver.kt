package com.jydev.media.file.infra

import com.jydev.media.file.MediaFileRepository
import com.jydev.media.file.StorageConfiguration
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class FileRepositoryResolver(
        private val fileRepositories : List<MediaFileRepository>
) {

    fun resolve(storageConfiguration : StorageConfiguration): MediaFileRepository {

        val resolveFileRepositoryPredicate: (MediaFileRepository) -> Boolean = { fileRepository ->
            fileRepository.resolveConfiguration(
                    configuration = storageConfiguration
            )
        }

        return fileRepositories.find(resolveFileRepositoryPredicate)
                ?: throw IllegalArgumentException("Can't resolve storageConfiguration for FileRepository")
    }
}