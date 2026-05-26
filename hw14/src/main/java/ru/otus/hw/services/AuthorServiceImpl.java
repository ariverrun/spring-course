package ru.otus.hw.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.document.AuthorDocument;
import ru.otus.hw.repositories.document.AuthorDocumentRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDocumentRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public AuthorDto findById(String id) {
        return authorRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
    }

    @Override
    public AuthorDto insert(CreateAuthorRequestDto dto) {
        return mapToDto(authorRepository.save(new AuthorDocument(null, dto.fullName())));
    }

    @Override
    public AuthorDto update(String id, UpdateAuthorRequestDto dto) {
        AuthorDocument author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        author.setFullName(dto.fullName());
        return mapToDto(authorRepository.save(author));
    }

    @Override
    public void deleteById(String id) {
        authorRepository.deleteById(id);
    }

    private AuthorDto mapToDto(AuthorDocument author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
