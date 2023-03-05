package com.pandora.fileservice.service

import com.pandora.fileservice.exceptions.ApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class BucketServiceImpl : BucketService {

    @Value("\${bucket.rootPath}")
    private val rootPath = ""

    override fun init(): String {
        return rootPath
    }

    override fun store(file: MultipartFile, userId: String, subjectId: String, topicId: String): String {
        if (file.contentType != "text/markdown") {
            throw ApiException("Format Not Supported Exception", null, HttpStatus.NOT_ACCEPTABLE)
        }

        if (file.isEmpty) {
            throw ApiException("Empty File Exception", null, HttpStatus.NOT_ACCEPTABLE)
        }

        val directories = Paths.get(rootPath).resolve("$userId/$subjectId").normalize().toAbsolutePath()

        Files.createDirectories(directories)

        if (!directories.parent.parent.equals(Paths.get(rootPath).normalize().toAbsolutePath())) {
            throw ApiException("Storing Outside Root Exception", null, HttpStatus.FORBIDDEN)
        }

        val destinationFile = Paths.get(rootPath).resolve("$userId/$subjectId/$topicId.md").normalize().toAbsolutePath()

        val inputStream = file.inputStream
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING)
        return topicId
    }

    override fun loadResource(filename: String): Resource {
        val filePath = Paths.get(rootPath).resolve(filename)

        val resource = UrlResource(filePath.toUri())

        if (!resource.exists()) {
            throw ApiException("Resource Does Not Exist", null, HttpStatus.NOT_FOUND)
        }

        if (!resource.isReadable){
            throw ApiException("Cannot Read File Exception", null, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return resource
    }

    override fun delete(filename: String): String {
        FileSystemUtils.deleteRecursively(Paths.get(rootPath).resolve(filename))
        return filename
    }
}
