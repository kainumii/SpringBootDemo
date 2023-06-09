package com.myswcompany.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.myswcompany.demo.exceptions.ErrorDetails;
import com.myswcompany.demo.exceptions.ResourceNotFoundException;
import com.myswcompany.demo.models.Speaker;
import com.myswcompany.demo.repositories.SpeakerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.rmi.ServerException;
import java.util.Date;
import java.util.List;

// POST is used to create new resource.
// POST is not idempotent means calling the same request multiple times may create same resources multiple times.

// PUT and PATCH used to update resource.
// PUT should only be used if you’re replacing a resource entirety.
// PUT replaces target resource with the request payload.
// PUT is idempotent means calling the same request multiple times will produce same result.

// PATCH request also modifies an existing resource but it only contains the data that needs to be changed.
// No need to provide data which is unchanged.
// PATCH method is the correct choice for partially updating an existing resource.

@RestController
@RequestMapping("/api/v1")
public class SpeakersController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SpeakerRepository speakerRepository;

    @GetMapping("/speakers")
    public List<Speaker> listAllSpeakers()
    {
        List<Speaker> speakers = speakerRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName"));
        return speakers;
    }

    @GetMapping
    @RequestMapping("/speakers/{id}")
    public ResponseEntity<Speaker> getSpeakerById(
            @PathVariable Long id) throws ResourceNotFoundException
    {
        Speaker speaker = speakerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Speaker not found: " + id));
        return ResponseEntity.ok().body(speaker);
    }

    @PostMapping("/speakers")
    public ResponseEntity<Speaker> createSpeaker(@Valid @RequestBody Speaker speaker) throws ServerException {
        Speaker postedSpeaker = speakerRepository.saveAndFlush(speaker);
        if(postedSpeaker == null) {
            throw new ServerException("Something went wrong.");
        }
        else {
            return new ResponseEntity<>(postedSpeaker, HttpStatus.CREATED);
        }
    }

    // PATCH is used when we want to apply the partial update to the resource and
    // does not want to update the entire resource.
    //
    // Postman: Headers -> Content-Type: application/json-patch+json
    // e.x. http://localhost:8080/api/v1/speakers/44
    // [
    //    {
    //    "op":"replace",
    //    "path":"/first_name",
    //    "value":"moi vaan"
    //    }
    //]
    //
    // JSON Patch operations
    // represented by a single op object
    // each operation must have one path member
    // the value of the path and from member is a JSON pointer. it refers to a location within the target document
    // 1. add operation
    // 2. remove operation
    // 3. replace operation
    // 4. move operation
    // 5. copy operation
    // 6. test operation

    // @PatchMapping as s PATCH handler method
    // 1. find correct speaker record by calling findById(id) method
    // 2. if speaker is found, invoke applyPatchToSpeaker method
    // 3. invoke saveAndFlush to save record to db
    // 4. return 200 OK response to the client with the patched Speaker details in response

    // in applyPatchToSpeaker method
    // 1. JsonPatch holds list of operations to be applied to the target Speaker
    // 2. convert target Speaker into the "com.fasterxml.jackson.databind.JsonNode" and
    // pass it to the "JsonPatch.apply" method to apply the patch.
    // 3. call objectMapper.treeToValue method, which binds the data in the patched
    // com.fasterxml.jackson.databind.JsonNode to the Speaker type. This is our patched Speaker instance
    // 4. return the patched Speaker instance
    @PatchMapping(value = "/speakers/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Speaker> updateSpeaker(
            @PathVariable Long id,
            @Valid
            @RequestBody JsonPatch patch) throws ResourceNotFoundException {
        try
        {
            Speaker currentSpeaker = speakerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Speaker not found: " + id));

            Speaker patched =  applyPatchToSpeaker(patch, currentSpeaker);
            speakerRepository.saveAndFlush(patched);

            return ResponseEntity.ok(patched);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JsonPatchException | JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Speaker applyPatchToSpeaker(JsonPatch patch, Speaker target) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(target, JsonNode.class));
        Speaker s = objectMapper.treeToValue(patched, Speaker.class);

        return s;
    }

    // When a client needs to replace an existing Resource entirely, they can use PUT.
    // When they're doing a partial update, they can use HTTP PATCH.
    // Another important aspect to consider here is idempotence.
    // PUT is idempotent; PATCH can be idempotent but isn't required to be.

    // @Valid annotation has to just before the request body parameter!!
    // Otherwise, validation wont work!!
    @PutMapping(value = "/speakers/{id}")
    public ResponseEntity<Speaker> saveSpeaker(
            @PathVariable Long id,
            @Valid @RequestBody Speaker new_speaker)  {
        // because this is a PUT, we expect all attributes to be passed in. A PATCH would only need what has changed.
        // if the URI isn't found, it will create a new speaker and store it in the database:

        return speakerRepository.findById(id)
                .map(speaker -> {
                    speaker.setFirst_name(new_speaker.getFirst_name());
                    speaker.setLast_name(new_speaker.getLast_name());
                    speaker.setTitle(new_speaker.getTitle());
                    speaker.setCompany(new_speaker.getCompany());
                    speaker.setSpeaker_bio(new_speaker.getSpeaker_bio());

                    var joo = speakerRepository.saveAndFlush(speaker);

                    return new ResponseEntity<>(joo, HttpStatus.OK);
                }).orElseGet(() -> {
                    Speaker joo = speakerRepository.saveAndFlush(new_speaker);
                    return new ResponseEntity<>(joo, HttpStatus.CREATED);
                });
    }

    @DeleteMapping(value = "/speakers/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) throws ResourceNotFoundException{

        Speaker s = speakerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Speaker not found with this id: " + id));

       speakerRepository.delete(s);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Throwable ex, WebRequest req)
    {
        String desc = req.getDescription(false);
        ErrorDetails details = new ErrorDetails(new Date(), ex.getMessage(), desc);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(details);
    }
}
