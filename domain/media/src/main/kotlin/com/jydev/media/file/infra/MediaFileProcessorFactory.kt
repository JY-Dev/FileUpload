package com.jydev.media.file.infra

import com.jydev.media.file.MediaFileAction
import com.jydev.media.file.MediaFileProcessor
import com.jydev.media.file.MediaFileStrategy
import com.jydev.media.file.StorageConfiguration
import org.springframework.stereotype.Component

@Component
class MediaFileProcessorFactory(
        private val fileRepositoryResolver: FileRepositoryResolver
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
                storeStrategy = mediaFileStrategy
        )
    }
}