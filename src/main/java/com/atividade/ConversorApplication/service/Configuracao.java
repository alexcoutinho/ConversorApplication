package com.atividade.ConversorApplication.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Component
public class Configuracao {

    private Logger logger = LoggerFactory.getLogger(Service.class);

    //@Value("${bucketName}")
    public String bucketName = "atividadeentrada";

    //@Value("${Access_key_ID}")
    public String key = "AKIAJGGFWB4OWLLCBSRA";


    //@Value("${Secret_access_key}")
    public String secret = "PwjYUbcNZ1YPdhJq/xNFan7jI0gtyedMttn9KueF";

    //@Bean
    public AmazonS3 S3client() {

        try {
            AWSCredentials credentials = new BasicAWSCredentials(this.key, this.secret);

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion("us-east-2")
                    .build();

            return s3Client;

        } catch (AmazonServiceException e) {
            logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            logger.info("Error Message:    " + e.getMessage());
            logger.info("HTTP Status Code: " + e.getStatusCode());
            logger.info("AWS Error Code:   " + e.getErrorCode());
            logger.info("Error Type:       " + e.getErrorType());
            logger.info("Request ID:       " + e.getRequestId());
            throw e;
        } catch (AmazonClientException e) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + e.getMessage());
            throw e;
        }

//        try {
//            client.getBucketLocation("your-eu-west-1-bucket");
//        } catch (AmazonS3Exception e) {
//            e.getAdditionalDetails().get("Region");
//        }


    }

}
