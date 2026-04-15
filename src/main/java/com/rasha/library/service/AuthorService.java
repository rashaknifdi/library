package com.rasha.library.service;

import com.rasha.library.exception.AuthorNotFoundException;
import com.rasha.library.model.Author;
import com.rasha.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository repo;

    public Author create(String name) {
        return repo.save(new Author(name));
    }

    public Author find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }
}
