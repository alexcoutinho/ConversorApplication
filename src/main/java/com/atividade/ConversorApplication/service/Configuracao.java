package com.atividade.ConversorApplication.service;

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

    //@Value("${bucketName}")
    public String bucketName = "atividadeentrada";

    //@Value("${Access_key_ID}")
    public String key = "AKIAIYAUEK6T6ZUWYHTA";

    //@Value("${Secret_access_key}")
    public String secret = "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";

    //@Bean
    public AmazonS3 S3client() {

        AWSCredentials credentials = new BasicAWSCredentials(this.key, this.secret);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("us-east-2")
                .build();



//        try {
//            client.getBucketLocation("your-eu-west-1-bucket");
//        } catch (AmazonS3Exception e) {
//            e.getAdditionalDetails().get("Region");
//        }


        return s3Client;
    }


}
