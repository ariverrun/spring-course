package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
            .map(a -> authorMapper.mapAuthorToDto(a))
            .toList();
    }

    @Override
    public AuthorDto findById(long id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        return authorMapper.mapAuthorToDto(author);
    }

    @Override
    @Transactional
    public AuthorDto insert(CreateAuthorRequestDto dto) {
        var author = new Author(0, dto.fullName());
        return authorMapper.mapAuthorToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public AuthorDto update(long id, UpdateAuthorRequestDto dto) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        author.setFullName(dto.fullName());
        return authorMapper.mapAuthorToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
