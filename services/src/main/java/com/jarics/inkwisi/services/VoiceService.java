package com.jarics.inkwisi.services;

import com.jarics.inkwisi.entities.Person;
import com.jarics.inkwisi.repositories.PersonRepository;
import org.openimaj.audio.features.MFCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class VoiceService {
    @Autowired
    PersonRepository personRepository;

    @Deprecated
    public List<Person> define(String pName){
        List<Person> wList = personRepository.findByLastName(pName);
        return wList;
    }

    @Deprecated
    public Person createPerson(Person pPerson){
        Person wPerson = personRepository.save(pPerson);
        return wPerson;
    }

    //TODO create the extraction and store it in elasticsearch?
    //TODO store MFCC in elasticsearch
    //TODO what's the difference between AudioClasifier and MFCC.calculateMFCC?
    public Person learn(String pPerson, InputStream pFs){
        VoiceFeatureExtractor wVoiceFeatureExtractor = new VoiceFeatureExtractor();
        MFCC wMfcc = wVoiceFeatureExtractor.extract(pFs);
//        wMfcc.calculateMFCC();
        Person wPerson = new Person();
        wPerson.setLastName(pPerson);
//        wPerson.addFeature(wMfcc);
        personRepository.save(wPerson);
        return wPerson;
    }

    //TODO lookup MFCC in elasticsearch (using a k-nearest)
    public Person defineVoice(InputStream is){
        VoiceFeatureExtractor wVoiceFeatureExtractor = new VoiceFeatureExtractor();
        MFCC wMfcc = wVoiceFeatureExtractor.extract(is);
        //loop on all persons and match MFCC
        Person wPerson = new Person();
        wPerson.setLastName("under construction");
//        wPerson.setName(pPerson);
//        personRepository.save(wPerson);
        return wPerson;
    }
}
