package com.atividade.ConversorApplication.controller;

import com.atividade.ConversorApplication.model.Video;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class VideoController {

    private Map<String, Video> videos;

    public VideoController() {
        videos = new HashMap<String, Video>();

        Video c1 = new Video("1", "Workshop Rest", "24hs");
        Video c2 = new Video("2", "Workshop Spring MVC", "24hs");
        Video c3 = new Video("3", "Desenvolvimento Web com JSF 2", "60hs");

        videos.put("1", c1);
        videos.put("2", c2);
        videos.put("3", c3);

    }

}