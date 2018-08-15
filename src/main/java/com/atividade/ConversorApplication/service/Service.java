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

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    S3Object lerArquivo(@RequestHeader("videoParaConversao") String videoParaConversao) {

        try {

            if (videoParaConversao == null) {
                S3Object video = configuracoes.S3client().getObject(new GetObjectRequest(configuracoes.bucketName, configuracoes.defeaultKeyS3));

                return video;

            } else {

                PutObjectResult resultado = gravarArquivo(videoParaConversao);
                S3Object video = configuracoes.S3client().getObject(new GetObjectRequest(configuracoes.bucketName, "saida/sample.mp4"));

                return video;


            }


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


        try {

            URL url = new URL("https://app.zencoder.com/api/v2/jobs");

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            if (con.getResponseCode() != 200) {
                throw new RuntimeException("HTTPS código erro : " + con.getResponseCode());
            }

            con.setRequestMethod("POST");

            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(15000);
            con.setRequestProperty("Zencoder-Api-Key", configuracoes.keyZencoder);
            con.setRequestProperty("Content-Type", configuracoes.ContentType);
            con.setRequestProperty("input", videoconvertido);

            con.getOutputStream();

            configuracoes.S3client().putObject(new PutObjectRequest(configuracoes.bucketName, configuracoes.key, videoconvertido));


        } catch (MalformedURLException e) {
            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }


}
