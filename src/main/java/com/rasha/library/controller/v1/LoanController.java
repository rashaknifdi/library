package com.rasha.library.controller.v1;

import com.rasha.library.dto.v1.LoanRequest;
import com.rasha.library.dto.v1.LoanResponse;
import com.rasha.library.model.Loan;
import com.rasha.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    public LoanResponse create(@Valid @RequestBody LoanRequest req) {
        Loan loan = service.createLoan(req.getBookId(), req.getBorrower());
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBorrower()
        );
    }

    @GetMapping("/{id}")
    public LoanResponse get(@PathVariable Long id) {
        Loan loan = service.getLoan(id);
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBorrower()
        );
    }
    @GetMapping
    public Page<LoanResponse> getAll(Pageable pageable) {
        return service.getAllLoans(pageable).map(loan -> new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBorrower()
        ));
    }

}
