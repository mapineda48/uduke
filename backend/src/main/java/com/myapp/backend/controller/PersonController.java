package com.myapp.backend.controller;

import com.myapp.backend.model.dto.CreatePersonRequest;
import com.myapp.backend.model.dto.PersonDto;
import com.myapp.backend.service.impl.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonServiceImpl personService;

    public PersonController(PersonServiceImpl personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<PersonDto> findAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public PersonDto findById(@PathVariable("id") Long id) {
        return personService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDto> create(
            @Valid @RequestPart("person") CreatePersonRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(request, photo));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PersonDto update(
            @PathVariable("id") Long id,
            @Valid @RequestPart("person") CreatePersonRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return personService.update(id, request, photo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        personService.delete(id);
    }
}
