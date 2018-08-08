package com.atividade.ConversorApplication.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/service/")
public class Service {

    private Logger logger = LoggerFactory.getLogger(Service.class);

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    private Configuracao configuracao;


    @RequestMapping(value = "/consultar", method = RequestMethod.GET)
    public @ResponseBody
    String consultar() {

        return "Hello!";
    }


    @RequestMapping(value = "/lerArquivo", method = RequestMethod.GET)
    public @ResponseBody
    S3Object lerArquivo() {


        S3Object video = s3client.getObject(new GetObjectRequest(configuracao.bucketName, configuracao.key));

        if (video == null) {
            throw new RuntimeException("Video nao encontrado no S3.");
        } else
            return video;

    }


    @RequestMapping(value = "/gravarArquivo/{videoconvertido}", method = RequestMethod.PUT)
    public @ResponseBody
    S3Object gravarArquivo(@PathVariable("videoconvertido") S3Object videoconvertido) {

        /*ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(arquivo.getContentType());
        objectMetadata.setContentLength(arquivo.getSize());*/

      return  s3client.putObject(new PutObjectRequest(configuracao.bucketName, configuracao.key,videoconvertido));

    }


}
