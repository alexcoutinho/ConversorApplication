package com.atividade.ConversorApplication.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@RestController
@RequestMapping("/service")
public class Service {

    private Logger logger = LoggerFactory.getLogger(Service.class);

    //@Autowired
    //private AmazonS3 s3client;

    @Autowired
    private Configuracao configuracoes;


    @RequestMapping(value = "/consultar/{video}", method = RequestMethod.GET)
    public @ResponseBody
    S3Object consultar(@PathVariable("video") String video) {

        return configuracoes.S3client().getObject(new GetObjectRequest(configuracoes.bucketName, configuracoes.entradaS3 + video));
    }


    @RequestMapping(value = "/lerArquivo", method = RequestMethod.POST)
    public @ResponseBody
    String lerArquivo(@RequestBody MultipartFile videoParaConversao) {
        try {

            if (videoParaConversao == null) {

                S3Object videoS3 = consultar("sample.dv");

                Object objetoRetorno = converterArquivo(videoS3.getObjectContent().getHttpRequest().getURI().toString());

                Integer job = new ArrayList<Integer>(((LinkedHashMap) objetoRetorno).values()).get(0);

                progresso(job);

                //String video = new ArrayList<String>(((LinkedHashMap) objetoRetorno).values()).get(1);

                return "https://atividadeentrada.s3.us-east-2.amazonaws.com/saida/videoconvertido.mp4";


            } else {

                PutObjectResult resultado = gravarArquivo(videoParaConversao);

                S3Object videoS3 = consultar(videoParaConversao.getOriginalFilename());

                Object objetoRetorno = converterArquivo(videoS3.getObjectContent().getHttpRequest().getURI().toString());

                Integer job = new ArrayList<Integer>(((LinkedHashMap) objetoRetorno).values()).get(0);

                progresso(job);

                String video = new ArrayList<String>(((LinkedHashMap) objetoRetorno).values()).get(1);

                return video;


            }


        } catch (
                AmazonServiceException e) {
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


    @RequestMapping(value = "/gravarArquivo/{video}", method = RequestMethod.PUT)
    public @ResponseBody
    PutObjectResult gravarArquivo(@PathVariable("video") MultipartFile video) {
        try {

            ObjectMetadata metadata = new ObjectMetadata();

            metadata.setContentLength(video.getBytes().length);
            metadata.setContentType(video.getContentType());

            return configuracoes.S3client().putObject(new PutObjectRequest(configuracoes.bucketName, configuracoes.entradaS3 + video.getOriginalFilename(), video.getInputStream(), metadata));

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }


    }


    @RequestMapping(value = "/converterArquivo/{arquivoS3}", method = RequestMethod.POST)
    public @ResponseBody
        //PutObjectResult converterArquivo(@PathVariable("arquivoS3") PutObjectResult arquivoS3) {
    Object converterArquivo(@PathVariable("arquivoS3") String arquivoS3) {
        try {

            URL url = new URL("https://app.zencoder.com/api/v2/jobs");

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestProperty("Content-Type", configuracoes.ContentType);
            con.setRequestProperty("Zencoder-Api-Key", configuracoes.keyZencoder);
            con.setDoOutput(true);

//            con.setRequestProperty("live_stream", "true");
            //con.setRequestProperty("input", arquivoS3);
//            con.setRequestProperty("credentials", "s3");
//            con.setRequestProperty("url", "https://atividadeentrada.s3.us-east-2.amazonaws.com/saida/videoconvertido.mp4");

            con.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

//        String msg = "{\"input\": \"" + arquivoS3 + "\" , \"live_stream\": \"true\" , \"credentials\": \"S3\" }";


            String msg = "{\"input\": \"" + arquivoS3 + "\", \"outputs\":[{\"credentials\": \"s3\" , \"url\": \"https://atividadeentrada.s3.us-east-2.amazonaws.com/saida/videoconvertido.mp4\"}]}";
            //String msg = "{\"credentials\": \"S3\"}";

//            "outputs":[{"id":"1951451283"}]
            //\"url\": \"https://atividadeentrada.s3.us-east-2.amazonaws.com/saida/videoconvertido.mp4\"
//"url": "https://atividadeentrada.s3.us-east-2.amazonaws.com/saida/videoconvertido.mp4",


            writer.write(msg);
            writer.flush();
            writer.close();

            con.connect();

            String result;
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();

            while (result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();

            con.disconnect();

            Object objetoRetorno = configuracoes.fromJson(result, Object.class);

            return objetoRetorno;


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
            //throws e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //throws e;
        }


    }


    @RequestMapping(value = "/progresso/{jobId}", method = RequestMethod.GET)
    public @ResponseBody
    String progresso(@PathVariable("jobId") Integer jobId) {

        try {
            URL url = new URL("https://app.zencoder.com/api/v2/jobs/" + jobId + "/progress");


            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestProperty("Content-Type", configuracoes.ContentType);
            con.setRequestProperty("Zencoder-Api-Key", configuracoes.keyZencoder);
            con.setDoOutput(true);


            con.setRequestMethod("GET");

            con.connect();

            String result;
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result2 = bis.read();

            while (result2 != -1) {
                buf.write((byte) result2);
                result2 = bis.read();
            }
            result = buf.toString();

            con.disconnect();


            Object objetoRetorno = configuracoes.fromJson(result, Object.class);

            String retorno = new ArrayList<String>(((LinkedHashMap) objetoRetorno).values()).get(0);


            if (retorno.equals("processing") || retorno.equals("waiting")) {
                progresso(jobId);
                retorno = null;
            }


            return retorno;


            //return "";//configuracoes.S3client().getObject(new GetObjectRequest(configuracoes.bucketName, configuracoes.entradaS3 + video));


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
            //throws e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //throws e;
        }
    }

}

