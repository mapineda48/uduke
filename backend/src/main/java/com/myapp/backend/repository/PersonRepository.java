package com.myapp.backend.repository;

import com.myapp.backend.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAllByOrderByLastNameAscFirstNameAsc();
}
