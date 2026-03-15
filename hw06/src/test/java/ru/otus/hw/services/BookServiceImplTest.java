package ru.otus.hw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import ru.otus.hw.models.Book;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@Import(BookServiceImpl.class)
public class BookServiceImplTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private BookServiceImpl bookService;

    @Test
    void shouldFindAllBooksAndAllowAccessToLazyFieldsWithoutTransaction() {
        List<Book> books = bookService.findAll();
        assertThat(TransactionSynchronizationManager.isActualTransactionActive())
            .isFalse();
        assertThat(books).isNotEmpty();

        for (Book book : books) {
            assertDoesNotThrow(() -> {
                book.getAuthor();
                book.getGenres();
            });
        }
    }

    @Test
    void shouldFindBookByIdAndAllowAccessToLazyFieldsWithoutTransaction() {
        var optionalBook = bookService.findById(FIRST_BOOK_ID);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive())
            .isFalse();
        assertThat(optionalBook).isPresent();
        var book = optionalBook.get();
        assertDoesNotThrow(() -> {
            book.getAuthor();
            book.getGenres();
        });
    }    
}
