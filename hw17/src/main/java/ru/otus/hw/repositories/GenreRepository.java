package ru.otus.hw.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.otus.hw.models.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    @Query("SELECT g FROM Genre g WHERE g.id IN :ids")
    List<Genre> findAllByIds(@Param("ids") Set<Long> ids);    
}