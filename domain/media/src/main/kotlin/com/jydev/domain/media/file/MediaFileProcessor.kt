package com.jydev.domain.media.file

class MediaFileProcessor internal constructor(
    private val action: MediaFileAction,
    private val fileRepository: MediaFileRepository
) {
    fun executeAction() {
        action.execute(fileRepository)
    }
}