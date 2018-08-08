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
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Configuration
@RequestMapping("/configuracao")
public class Configuracao {

    @Value("${application.bucketName}")
    public String bucketName;

    @Value("${application.Access_key_ID}")
    public String key;

    @Value("${application.Secret_access_key}")
    public String secret;

    @Bean
    public AmazonS3 s3client() {

        AWSCredentials credentials = new BasicAWSCredentials(this.key, this.secret);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        return s3Client;
    }


}
