package com.atividade.ConversorApplication.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/service")
public class Service {

    private Logger logger = LoggerFactory.getLogger(Service.class);

    //@Autowired
    //private AmazonS3 s3client;

    @Autowired
    private Configuracao configuracoes;


    @RequestMapping(value = "/consultar", method = RequestMethod.GET)
    public @ResponseBody
    String consultar() {

        return "Hello!";
    }


    @RequestMapping(value = "/lerArquivo", method = RequestMethod.GET)
    public @ResponseBody
    S3Object lerArquivo() {

        try {
            ArrayList<String> testes = new ArrayList<String>();
            ArrayList<String> testes2 = new ArrayList<String>();

            S3Object video = null, objectPortion = null, headerOverrideObject = null;

            AmazonS3  S3client = configuracoes.S3client();

            for (Bucket bucket : S3client.listBuckets()) {
                testes.add(bucket.getName());
            }

            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName("atividadeentrada").withPrefix("entrada/").withDelimiter("/");
            ListObjectsV2Result listing = S3client.listObjectsV2(req);
            for (String commonPrefix : listing.getCommonPrefixes()) {
                testes.add(commonPrefix);
            }
            for (S3ObjectSummary summary: listing.getObjectSummaries()) {
                testes2.add(summary.getKey());
            }

            ObjectListing a = S3client.listObjects("atividadeentrada", "entrada/");
            ObjectListing b = S3client.listObjects("atividadeentrada");


            video = S3client.getObject(new GetObjectRequest("atividadeentrada", "entrada/sample.dv"));

            if (video == null) {
                throw new RuntimeException("Video nao encontrado no S3.");
            } else
                return video;
        } catch (AmazonServiceException e) {
            logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            logger.info("Error Message:    " + e.getMessage());
            logger.info("HTTP Status Code: " + e.getStatusCode());
            logger.info("AWS Error Code:   " + e.getErrorCode());
            logger.info("Error Type:       " + e.getErrorType());
            logger.info("Request ID:       " + e.getRequestId());
            throw e;
        } catch (
                AmazonClientException e) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + e.getMessage());
            throw e;
        }

    }


    @RequestMapping(value = "/gravarArquivo/{videoconvertido}", method = RequestMethod.PUT)
    public @ResponseBody
    PutObjectResult gravarArquivo(@PathVariable("videoconvertido") String videoconvertido) {

        return configuracoes.S3client().putObject(new PutObjectRequest(configuracoes.bucketName, configuracoes.key, videoconvertido));

    }


}
