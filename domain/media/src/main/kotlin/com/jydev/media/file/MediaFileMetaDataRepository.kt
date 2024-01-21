package com.jydev.media.file

import org.springframework.data.jpa.repository.JpaRepository

interface MediaFileMetaDataRepository : JpaRepository<MediaFileMetaData, Long> {
    fun getMediaFileMetaDataByFileUrl(fileUrl : String) : MediaFileMetaData?

}