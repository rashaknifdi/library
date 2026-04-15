package com.rasha.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rasha.library.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
