package com.jarics.inkwisi.controllers;

import com.jarics.inkwisi.entities.Person;
import com.jarics.inkwisi.services.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/personV2")
public class PersonController {

    @Autowired
    VoiceService voiceService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public Person create(@RequestBody Person pPerson) {
        Person wPerson = voiceService.createPerson(pPerson);
        return wPerson;
    }

    @RequestMapping(value = "/define", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<Person> define(@RequestParam("name") String pName) {
        List<Person> wList = voiceService.define(pName);
        return wList;
    }

    @RequestMapping(value = "/define/voice", method = RequestMethod.POST)
    public Person defineVoice(@RequestParam("file") MultipartFile file) throws Exception {
        Person wPerson;
        UUID wFileName = UUID.randomUUID();
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            File wQuery = new File(wFileName.toString());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(wQuery));
            stream.write(bytes);
            stream.close();
            wPerson = voiceService.defineVoice(new FileInputStream(wQuery));

        } else {
            throw new Exception("You failed to upload because the file was empty.");
        }
        return wPerson;
    }

    @RequestMapping(value = "/learn/voice", method = RequestMethod.POST)
    public void learnVoice(@RequestParam("name") String pPersonName, @RequestParam("file") MultipartFile file) throws Exception {
        UUID wFileName = UUID.randomUUID();
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            File wQuery = new File(wFileName.toString());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(wQuery));
            stream.write(bytes);
            stream.close();
            voiceService.learn(pPersonName, new FileInputStream(wQuery));
        } else {
            throw new Exception("You failed to upload because the file was empty.");
        }
    }

}
