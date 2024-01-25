package com.jydev.domain.media.file

import org.springframework.stereotype.Component

@Component
class MediaFileProcessorFactory(
        private val fileRepositoryResolver: MediaFileRepositoryResolver
) {

    fun create(
        mediaFileAction: MediaFileAction
    ): MediaFileProcessor {
        val storageConfiguration = mediaFileAction.storageConfiguration
        val repository = fileRepositoryResolver.resolve(storageConfiguration)

        return MediaFileProcessor(
                action = mediaFileAction,
                fileRepository = repository
        )
    }
}