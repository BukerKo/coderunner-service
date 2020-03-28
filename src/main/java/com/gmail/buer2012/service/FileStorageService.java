package com.gmail.buer2012.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FileStorageService {

    private static Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${aws.enabled:#{false}}")
    private boolean enabled;
    @Value("${aws.accessKey:#{null}}")
    private String accessKey;
    @Value("${aws.secretKey:#{null}}")
    private String secretKey;
    @Value("${aws.bucketName:#{null}}")
    private String bucketName;
    @Value("${aws.bucketAddress:#{null}}")
    private String bucketAddress;
    @Value("${storage.uploadDir:#{'saved'}}")
    private String uploadDir;

    public String storeFile(File file) throws IOException {
        String[] nameParts = Objects.requireNonNull(file.getName()).split("\\.");
        String fileName = StringUtils
            .cleanPath(UUID.randomUUID().toString() + "." + nameParts[nameParts.length - 1]);
        if(enabled) {
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength(file.length());

            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(this.accessKey, this.secretKey)))
                    .build();

            s3.putObject(new PutObjectRequest(this.bucketName, fileName, new FileInputStream(file), md)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            log.info("Stored {} to S3", fileName);
            return this.bucketAddress + "/" + fileName;
        }
        else {
            try {
                Path directory = Paths.get(uploadDir).toAbsolutePath().normalize();
                Path targetLocation = directory.resolve(fileName);
                Files.copy(new FileInputStream(file), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("Stored {} to local storage", fileName);
                return targetLocation.toString();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void deleteFile(String path) {
        if(enabled) {
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(this.accessKey, this.secretKey)))
                .build();
            s3.deleteObject(this.bucketName, path.substring(path.lastIndexOf('/')+1));
        }
        else {
            File file = new File(path);
            file.delete();
        }
    }
}


