package com.jydev.domain.media.file

sealed class MediaFileAction {

    data class StoreMediaFile(
            val command: FileStrategy.StoreFileCommand
    ) : MediaFileAction()

    data class DeleteMediaFile(
            val command: FileStrategy.DeleteFileCommand
    ) : MediaFileAction()
}