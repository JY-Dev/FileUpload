package com.jydev.media.file.infra

import com.jydev.media.file.MediaFileRepository
import com.jydev.media.file.MediaFileStrategy
import com.jydev.media.file.StorageConfiguration
import org.springframework.stereotype.Repository
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

@Repository
class LocalFileRepository : MediaFileRepository {

    private val localConfigClass = StorageConfiguration.Local::class.java
    override val storageConfigClass: Class<out StorageConfiguration> = localConfigClass

    override fun save(storageConfiguration: StorageConfiguration, command: MediaFileStrategy.StoreFileCommand) {

        validateStoreFile(command)

        val config = castConfiguration(storageConfiguration, localConfigClass)

        val filePath = Path(config.storagePath).resolve(command.filePath)
                .normalize()

        try {
            Files.createDirectories(filePath)
            Files.copy(command.fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        } catch (exception: IOException) {

            val deleteCommand = MediaFileStrategy.DeleteFileCommand(command.filePath)
            delete(storageConfiguration, deleteCommand)

            throw IllegalArgumentException(
                    "Store file for local fail : FileName [%s]".format(command.filePath, exception)
            )
        }
    }

    override fun delete(storageConfiguration: StorageConfiguration, command: MediaFileStrategy.DeleteFileCommand) {

        val config = castConfiguration(storageConfiguration, localConfigClass)

        try {
            val filePath = Path(config.storagePath).resolve(command.filePath)
                    .normalize()
            Files.deleteIfExists(filePath)
        } catch (e: IOException) {
            throw IllegalArgumentException(
                    "Delete file for local fail : FileName [%s]".format(command.filePath)
            )
        }
    }

    private fun validateStoreFile(command: MediaFileStrategy.StoreFileCommand) {

        val filePath: String = command.filePath

        if (filePath.contains("..")) {
            throw IllegalArgumentException("Invalid store file for Local")
        }
    }
}