package com.jydev.media.file

sealed class MediaFileAction {

    data class StoreMediaFile(
            val command: MediaFileStrategy.StoreFileCommand
    ) : MediaFileAction()

    data class DeleteMediaFile(
            val command: MediaFileStrategy.DeleteFileCommand
    ) : MediaFileAction()
}