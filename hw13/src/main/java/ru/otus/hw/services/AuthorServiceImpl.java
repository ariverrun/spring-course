package ru.otus.hw.services;

import java.util.List;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
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

    private final AclServiceWrapperService aclServiceWrapperService;

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Author', 'READ')")
    public AuthorDto findById(long id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        return authorMapper.mapAuthorToDto(author);
    }

    @Override
    @Transactional
    public AuthorDto insert(CreateAuthorRequestDto dto) {
        var author = new Author(null, dto.fullName());
        var savedAuthor = authorRepository.save(author);
        
        aclServiceWrapperService.createPermission(savedAuthor, BasePermission.READ);
        aclServiceWrapperService.createPermission(savedAuthor, BasePermission.WRITE);
        aclServiceWrapperService.createPermission(savedAuthor, BasePermission.DELETE);

        return authorMapper.mapAuthorToDto(savedAuthor);
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Author', 'WRITE') or hasRole('ADMIN')")
    public AuthorDto update(long id, UpdateAuthorRequestDto dto) {
        var author = authorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(id)));
        author.setFullName(dto.fullName());
        return authorMapper.mapAuthorToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Author', 'DELETE') or hasRole('ADMIN')")
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
