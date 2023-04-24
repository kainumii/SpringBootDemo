package com.myswcompany.demo.controllers;

import com.myswcompany.demo.exceptions.ResourceNotFoundException;
import com.myswcompany.demo.models.Session;
import com.myswcompany.demo.models.Speaker;
import com.myswcompany.demo.repositories.SpeakerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SpeakersController {
    @Autowired
    private SpeakerRepository speakerRepository;

    @GetMapping("/speakers")
    public List<Speaker> listAllSpeakers()
    {
        return speakerRepository.findAll();
    }

    @GetMapping
    @RequestMapping("/speakers/{id}")
    public ResponseEntity<Speaker> getSpeakerById(
            @PathVariable Long id) throws ResourceNotFoundException
    {
        Speaker speaker = speakerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Speaker not found: " + id));
        return ResponseEntity.ok().body(speaker);
    }

    @PostMapping
    public ResponseEntity<Speaker> createSpeaker(@RequestBody Speaker speaker)
    {
        speakerRepository.saveAndFlush(speaker);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/speakers/{id}")
    public ResponseEntity<Speaker> updateSpeaker(
            @PathVariable Long id, @RequestBody Speaker speaker) throws ResourceNotFoundException {
        //because this is a PUT, we expect all attributes to be passed in. A PATCH would only need what has changed.
        //TODO: Add validation that all attributes are passed in, otherwise return a 400 bad payload


        Speaker existingSpeaker = speakerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Speaker not found: " + id));




        existingSpeaker.setFirst_name(speaker.getFirst_name());
        existingSpeaker.setLast_name(speaker.getLast_name());
        existingSpeaker.setCompany(speaker.getCompany());
        existingSpeaker.setTitle(speaker.getTitle());

        Session firstSession = new Session();
        firstSession.setSession_name("C# .NET");
        firstSession.setSession_length(500);
        firstSession.setSession_description("qwerty ..");
        List<Session> lst = new ArrayList<>();
        lst.add(firstSession);

        existingSpeaker.setSessions(lst);

        Speaker updatedSpeaker = speakerRepository.saveAndFlush(existingSpeaker);

        return ResponseEntity.ok(updatedSpeaker);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        //Also need to check for children records before deleting.
        speakerRepository.deleteById(id);
    }



}
