package com.jydev

import com.jydev.media.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FileUploadApplication constructor(private val test : Test) {

}

fun main(args: Array<String>) {
    runApplication<FileUploadApplication>(*args)
}
