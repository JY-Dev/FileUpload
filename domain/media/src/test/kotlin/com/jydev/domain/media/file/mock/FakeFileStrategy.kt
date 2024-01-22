package com.jydev.domain.media.file.mock

import com.jydev.domain.media.file.FileStrategy

class FakeFileStrategy : FileStrategy {
    var isSaveCall = false
        private set
    var isDeleteCall = false
        private set

    override fun save(command: FileStrategy.StoreFileCommand) {
        isSaveCall = true
    }

    override fun delete(command: FileStrategy.DeleteFileCommand) {
        isDeleteCall = true
    }
}