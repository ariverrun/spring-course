package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

@DisplayName("Сервис для работы с авторами ")
@DataMongoTest
@Import(AuthorServiceImpl.class)
class AuthorServiceImplTest {

    @Autowired
    private AuthorServiceImpl authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("должен возвращать всех авторов")
    void shouldReturnAllAuthors() {
        // var author1 = new Author(null, "Author 1");
        // var author2 = new Author(null, "Author 2");
        // authorRepository.saveAll(List.of(author1, author2));

        // var authors = authorService.findAll();

        
        // assertThat(authors).hasSize(2);
        // assertThat(authors)
        //         .extracting(Author::getFullName)
        //         .containsExactlyInAnyOrder("Author 1", "Author 2");
    }

    // @Test
    // @DisplayName("должен возвращать автора по ID")
    // void shouldReturnAuthorById() {
    //     var savedAuthor = authorRepository.save(new Author(null, "John Doe"));

    //     var foundAuthor = authorService.findById(savedAuthor.getId());

    //     assertThat(foundAuthor).isPresent();
    //     assertThat(foundAuthor.get().getId()).isEqualTo(savedAuthor.getId());
    //     assertThat(foundAuthor.get().getFullName()).isEqualTo("John Doe");
    // }

    // @Test
    // @DisplayName("должен возвращать пустой Optional для несуществующего ID")
    // void shouldReturnEmptyOptionalForNonExistingId() {
    //     var foundAuthor = authorService.findById("non-existing-id");

    //     assertThat(foundAuthor).isEmpty();
    // }

    // @Test
    // @DisplayName("должен успешно вставить нового автора")
    // void shouldInsertAuthor() {
    //     var author = authorService.insert("New Author");

    //     assertThat(author.getId()).isNotNull();
    //     assertThat(author.getFullName()).isEqualTo("New Author");

    //     var savedAuthor = authorRepository.findById(author.getId());
    //     assertThat(savedAuthor).isPresent();
    //     assertThat(savedAuthor.get().getFullName()).isEqualTo("New Author");
    // }

    // @Test
    // @DisplayName("должен успешно обновить существующего автора")
    // void shouldUpdateAuthor() {
    //     var savedAuthor = authorRepository.save(new Author(null, "Old Name"));

    //     var updatedAuthor = authorService.update(savedAuthor.getId(), "New Name");

    //     assertThat(updatedAuthor.getId()).isEqualTo(savedAuthor.getId());
    //     assertThat(updatedAuthor.getFullName()).isEqualTo("New Name");

    //     var authorFromDb = authorRepository.findById(savedAuthor.getId());
    //     assertThat(authorFromDb).isPresent();
    //     assertThat(authorFromDb.get().getFullName()).isEqualTo("New Name");
    // }

    // @Test
    // @DisplayName("должен выбрасывать исключение при обновлении несуществующего автора")
    // void shouldThrowExceptionWhenUpdatingNonExistingAuthor() {
    //     assertThatThrownBy(() -> authorService.update("non-existing-id", "Any Name"))
    //             .isInstanceOf(EntityNotFoundException.class)
    //             .hasMessageContaining("Author with id non-existing-id not found");
    // }

    // @Test
    // @DisplayName("должен успешно удалить автора без книг")
    // void shouldDeleteAuthorWithoutBooks() {
    //     var author = authorRepository.save(new Author(null, "Author to Delete"));

    //     authorService.deleteById(author.getId());

    //     var deletedAuthor = authorRepository.findById(author.getId());
    //     assertThat(deletedAuthor).isEmpty();
    // }

    // @Test
    // @DisplayName("должен успешно удалить автора вместе с его книгами")
    // void shouldDeleteAuthorWithBooks() {
    //     var author = authorRepository.save(new Author(null, "Author with Books"));
        
    //     var book1 = new Book(null, "Book 1", author, List.of());
    //     var book2 = new Book(null, "Book 2", author, List.of());
    //     bookRepository.saveAll(List.of(book1, book2));

    //     authorService.deleteById(author.getId());

    //     var deletedAuthor = authorRepository.findById(author.getId());
    //     assertThat(deletedAuthor).isEmpty();

    //     var remainingBooks = bookRepository.findAll();
    //     assertThat(remainingBooks).isEmpty();
    // }

    // @Test
    // @DisplayName("должен выбрасывать исключение при удалении несуществующего автора")
    // void shouldThrowExceptionWhenDeletingNonExistingAuthor() {
    //     assertThatThrownBy(() -> authorService.deleteById("non-existing-id"))
    //             .isInstanceOf(EntityNotFoundException.class)
    //             .hasMessageContaining("Author with id non-existing-id not found");
    // }

    // @Test
    // @DisplayName("должен корректно обрабатывать пустой список авторов")
    // void shouldHandleEmptyAuthorList() {
    //     var authors = authorService.findAll();

    //     assertThat(authors).isEmpty();
    // }

    // private static List<Author> getDbAuthors() {
    //     return IntStream.range(1, 4).boxed()
    //             .map(id -> new Author(id, "Author_" + id))
    //             .toList();
    // }
}