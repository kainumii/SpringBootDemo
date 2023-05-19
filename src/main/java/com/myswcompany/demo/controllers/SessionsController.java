package com.myswcompany.demo.controllers;

import com.myswcompany.demo.exceptions.ErrorDetails;
import com.myswcompany.demo.exceptions.ResourceNotFoundException;
import com.myswcompany.demo.exceptions.ContentNotAllowedException;
import com.myswcompany.demo.models.Session;
import com.myswcompany.demo.models.Speaker;
import com.myswcompany.demo.repositories.SessionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;


import java.rmi.ServerException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SessionsController {
    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/sessions")
    public List<Session> listAllSessions()
    {
        List<Session> sessions = sessionRepository.findAll(Sort.by(Sort.Direction.ASC, "sessionName"));
        return sessions;
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<Session> getSessionById(
            @PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Session s = sessionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Session not found with this id: " + id));
        return ResponseEntity.ok().body(s);
    }

    // POST
    // http://localhost:8080/api/v1/sessions
    //
    // Body JSON e.x.
    // {
    //        "session_name": "Spring Boot Session",
    //        "session_description": "",
    //        "session_length": 300
    //  }
    @PostMapping("/sessions")
    public ResponseEntity<Session> createSession(
            @Valid
            @RequestBody Session session) throws ContentNotAllowedException, ServerException {

        if(session.getSession_name().startsWith("a"))
            throw new ContentNotAllowedException("Session name starting with letter \"a\" is not allowed!!");

        Session postedSession = sessionRepository.saveAndFlush(session);
        if(postedSession == null) {
            throw new ServerException("Something went wrong");
        }
        else {
            return new ResponseEntity<>(postedSession, HttpStatus.CREATED);
        }
    }

    @PutMapping("/sessions/{id}")
    public ResponseEntity<Session> updateSession(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody Session session)
    {
        return sessionRepository.findById(id).map(sess -> {
            sess.setSession_name(session.getSession_name());
            sess.setSession_description(session.getSession_description());
            sess.setSession_length(session.getSession_length());

            var joo = sessionRepository.saveAndFlush(sess);

            return new ResponseEntity<>(joo, HttpStatus.OK);
        }).orElseGet(() -> {
            Session created = sessionRepository.saveAndFlush(session);

            return new ResponseEntity<>(created, HttpStatus.CREATED);
        });
    }

    @DeleteMapping("/sessions/{id}")
    public void deleteSession(
            @PathVariable Long id) throws ResourceNotFoundException {
        Session s = sessionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Session not found with this id: " + id));

        sessionRepository.delete(s);
    }

    @ExceptionHandler(ContentNotAllowedException.class)
    public ResponseEntity<?> handleContentNotAllowedException(ContentNotAllowedException ex, WebRequest req)
    {
        ErrorDetails details = new ErrorDetails(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest req)
    {
        String desc = req.getDescription(false);
        ErrorDetails details = new ErrorDetails(new Date(), ex.getMessage(), desc);

        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }
}
