package com.jydev.media.file

class MediaFileProcessor internal constructor(
        private val action: MediaFileAction,
        private val storeStrategy: MediaFileStrategy
) {
    fun executeAction() {
        when(action) {
            is MediaFileAction.StoreMediaFile -> {
                storeStrategy.save(command = action.command)
            }

            is MediaFileAction.DeleteMediaFile -> {
                storeStrategy.delete(command = action.command)
            }
        }
    }
}