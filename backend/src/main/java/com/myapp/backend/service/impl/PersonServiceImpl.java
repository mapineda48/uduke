package com.myapp.backend.service.impl;

import com.myapp.backend.model.dto.CreatePersonRequest;
import com.myapp.backend.model.dto.PersonDto;
import com.myapp.backend.model.entity.Person;
import com.myapp.backend.repository.PersonRepository;
import com.myapp.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PersonServiceImpl {

    private static final String CONTAINER = "people";

    private final PersonRepository personRepository;
    private final StorageService storageService;

    public PersonServiceImpl(PersonRepository personRepository, StorageService storageService) {
        this.personRepository = personRepository;
        this.storageService = storageService;
    }

    public List<PersonDto> findAll() {
        return personRepository.findAllByOrderByLastNameAscFirstNameAsc()
            .stream()
            .map(this::toDto)
            .toList();
    }

    public PersonDto findById(Long id) {
        return personRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
    }

    public PersonDto create(CreatePersonRequest request, MultipartFile photo) {
        Person person = Person.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .phone(request.phone())
            .birthDate(request.birthDate())
            .build();

        if (photo != null && !photo.isEmpty()) {
            person.setPhotoUrl(uploadPhoto(photo));
        }

        return toDto(personRepository.save(person));
    }

    public PersonDto update(Long id, CreatePersonRequest request, MultipartFile photo) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        person.setEmail(request.email());
        person.setPhone(request.phone());
        person.setBirthDate(request.birthDate());

        if (photo != null && !photo.isEmpty()) {
            deleteOldPhoto(person.getPhotoUrl());
            person.setPhotoUrl(uploadPhoto(photo));
        }

        return toDto(personRepository.save(person));
    }

    public void delete(Long id) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        deleteOldPhoto(person.getPhotoUrl());
        personRepository.delete(person);
    }

    private String uploadPhoto(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
            ? originalName.substring(originalName.lastIndexOf('.'))
            : "";
        String blobName = UUID.randomUUID() + ext;
        try {
            return storageService.upload(CONTAINER, blobName,
                file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload photo");
        }
    }

    private void deleteOldPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            String blobName = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            try {
                storageService.delete(CONTAINER, blobName);
            } catch (Exception ignored) {
            }
        }
    }

    private PersonDto toDto(Person p) {
        return new PersonDto(
            p.getId(), p.getFirstName(), p.getLastName(), p.getEmail(),
            p.getPhone(), p.getBirthDate(), p.getPhotoUrl(), p.getCreatedAt()
        );
    }
}
