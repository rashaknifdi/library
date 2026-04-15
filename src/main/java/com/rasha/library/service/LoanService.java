package com.rasha.library.service;

import com.rasha.library.exception.BookAlreadyLoanedException;
import com.rasha.library.exception.BookNotFoundException;
import com.rasha.library.exception.LoanNotFoundException;
import com.rasha.library.model.Book;
import com.rasha.library.model.Loan;
import com.rasha.library.repository.BookRepository;
import com.rasha.library.repository.LoanRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private BookRepository bookRepo;

    @Transactional
    public synchronized Loan createLoan(Long bookId, String borrower) {

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.getLoan() != null) {
            throw new BookAlreadyLoanedException(bookId);
        }

        Loan loan = new Loan(book, borrower);
        book.setLoan(loan);

        return loanRepo.save(loan);
    }

    public Loan getLoan(Long id) {
        return loanRepo.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    public List<Loan> getAllLoans() {
        return loanRepo.findAll();
    }
}
