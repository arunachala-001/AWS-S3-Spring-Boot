package com.filestorage.springboot.simple_file_storage_system.controller;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.filestorage.springboot.simple_file_storage_system.model.Images;

import com.filestorage.springboot.simple_file_storage_system.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {

    private List<String> images = new ArrayList<String>();

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private final AmazonS3 amazonS3;

    @Autowired
    private FileService fileService;

    @PostMapping("/image/upload")
    public ResponseEntity<String> storeFilesinS3(@RequestParam("file") MultipartFile image) {
        try{
            String fileName = UUID.randomUUID().toString()+"-"+image.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            amazonS3.putObject(bucketName, fileName, image.getInputStream(),metadata);
            images.add(fileName);
            return ResponseEntity.ok().body("Image Uploaded successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) throws IOException {
        Resource resource = null;
        try{
            resource = fileService.dowload(fileName);
            String contentType = "application/octet-stream";

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+
                            resource.getFilename()).body(resource);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
