package com.jydev.domain.media.file

class MediaFileProcessor internal constructor(
    private val action: MediaFileAction,
    private val fileStrategy: FileStrategy
) {
    fun executeAction() {
        when(action) {
            is MediaFileAction.StoreMediaFile -> {
                fileStrategy.save(command = action.command)
            }

            is MediaFileAction.DeleteMediaFile -> {
                fileStrategy.delete(command = action.command)
            }
        }
    }
}