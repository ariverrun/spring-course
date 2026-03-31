package ru.otus.hw.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import ru.otus.hw.models.Genre;

public interface GenreRepository extends CrudRepository<Genre, String> {
    
    List<Genre> findAllById(Set<String> ids);    
}