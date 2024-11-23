package com.filestorage.springboot.simple_file_storage_system.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public Resource dowload(String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        byte[] bytes = s3Object.getObjectContent().readAllBytes();
        Resource resource = new ByteArrayResource(bytes);
        return resource;
    }
}
