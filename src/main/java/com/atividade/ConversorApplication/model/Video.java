package com.atividade.ConversorApplication.model;

public class Video {

    private String contentType= "application/json";
    private String zencoderApiKey = "df1fabedff3aad8826adabba7cbc24ae";

    private String input = "http://atividadeentrada.s3.amazonaws.com/sample.dv";

    public Video(String contentType, String zencoderApiKey, String input) {
        this.contentType = contentType;
        this.zencoderApiKey = zencoderApiKey;
        this.input = input;
    }

}
