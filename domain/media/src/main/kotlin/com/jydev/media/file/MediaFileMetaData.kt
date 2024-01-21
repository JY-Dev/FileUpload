package com.jydev.media.file

import jakarta.persistence.*

@Entity
@Table(name = "MEDIA_FILE_META_DATA")
class MediaFileMetaData(
        storageType: StorageType,
        originalFileName: String,
        fileSize: Long,
        fileUrl: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_TYPE", nullable = false)
    var storageType: StorageType = storageType
        private set

    @Column(name = "FILE_SIZE", nullable = false)
    var fileSize: Long = fileSize
        private set

    @Column(name = "ORIGINAL_FILE_NAME", nullable = false)
    var originalFileName: String = originalFileName
        private set

    @Column(name = "FILE_URL", unique = true, nullable = false)
    var fileUrl: String = fileUrl
        private set
}
