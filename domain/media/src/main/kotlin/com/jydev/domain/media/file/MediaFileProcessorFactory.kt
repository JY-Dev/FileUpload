package com.jydev.domain.media.file

import org.springframework.stereotype.Component

@Component
class MediaFileProcessorFactory(
        private val fileRepositoryResolver: MediaFileRepositoryResolver
) {

    fun create(
        mediaFileAction: MediaFileAction,
        storageConfiguration: StorageConfiguration
    ): MediaFileProcessor {

        val repository = fileRepositoryResolver.resolve(storageConfiguration)

        val mediaFileStrategy = MediaFileStrategy(
                fileRepository = repository,
                storageConfiguration = storageConfiguration
        )

        return MediaFileProcessor(
                action = mediaFileAction,
                fileStrategy = mediaFileStrategy
        )
    }
}