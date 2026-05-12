package com.rasha.library.service;

import com.rasha.library.dto.v1.BookRequest;
import com.rasha.library.dto.v1.BookResponse;
import com.rasha.library.dto.v2.BookListResponse;
import com.rasha.library.exception.AuthorNotFoundException;
import com.rasha.library.exception.BookNotFoundException;
import com.rasha.library.model.Author;
import com.rasha.library.model.Book;
import com.rasha.library.repository.AuthorRepository;
import com.rasha.library.repository.BookRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;

    public BookService(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    public BookResponse create(BookRequest request) {

        Author author = authorRepo.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthorId()));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setAuthor(author);

        Book saved = bookRepo.save(book);

        return new BookResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getIsbn(),
                saved.getPublishedYear(),
                saved.getAuthor().getId(),
                saved.getAuthor().getName()
        );
    }

    @Cacheable(value = "books", key = "#id")
    public BookResponse getById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }

    public Page<BookResponse> getAll(Pageable pageable) {
        return bookRepo.findAll(pageable).map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        ));
    }

    public Page<BookResponse> getByAuthor(Long authorId, Pageable pageable) {
        if (!authorRepo.existsById(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }
        return bookRepo.findByAuthorId(authorId, pageable).map(book -> new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        ));
    }

    public BookResponse update(Long id, BookRequest request) {

        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        Author author = authorRepo.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.getAuthorId()));

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setAuthor(author);

        Book saved = bookRepo.save(book);

        return new BookResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getIsbn(),
                saved.getPublishedYear(),
                saved.getAuthor().getId(),
                saved.getAuthor().getName()
        );
    }
    @CacheEvict(value = "books", key = "#id")
    public void delete(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepo.deleteById(id);
    }

    public BookListResponse getAllV2(Pageable pageable) {
        List<com.rasha.library.dto.v2.BookResponse> data = bookRepo.findAll(pageable).stream()
                .map(b -> new com.rasha.library.dto.v2.BookResponse(
                        b.getTitle(),
                        b.getAuthor().getName(),
                        b.getLoan() == null
                ))
                .toList();

        return new BookListResponse(data);
    }
}
