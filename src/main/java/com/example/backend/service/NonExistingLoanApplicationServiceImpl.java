package com.example.backend.service;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.pagination.AbstractCursorService;
import com.example.backend.pagination.CursorPage;
import com.example.backend.repository.NonExistingLoanApplicationRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NonExistingLoanApplicationServiceImpl
        extends AbstractCursorService<NonExistingLoanApplication, NonExistingLoanApplication>
        implements NonExistingLoanApplicationService {

    private final NonExistingLoanApplicationRepository repository;

    public NonExistingLoanApplicationServiceImpl(
            NonExistingLoanApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public NonExistingLoanApplication createApplication(
            NonExistingLoanApplication application) {

        // Status will default to PENDING via @PrePersist
        return repository.save(application);
    }

    @Override
    public List<NonExistingLoanApplication> getPendingApplications() {
        return repository.findByStatus(LoanRequestStatus.PENDING);
    }

    @Override
    public NonExistingLoanApplication updateStatus(
            Long id,
            LoanRequestStatus status) {

        NonExistingLoanApplication application =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Non-existing loan application not found with id: " + id
                                )
                        );

        application.setStatus(status);

        return repository.save(application);
    }

    @Override
        public List<NonExistingLoanApplication> getByStatus(LoanRequestStatus status) {
                return repository.findByStatus(status);
        }

        @Override
        public void deleteApplication(Long id) {
                NonExistingLoanApplication application =
                                repository.findById(id)
                                                .orElseThrow(() ->
                                                                new ResourceNotFoundException(
                                                                                "Non-existing loan application not found with id: " + id
                                                                )
                                                );

                repository.delete(application);
        }
        @Override
protected List<NonExistingLoanApplication> fetchAfterCursor(Long cursor, Pageable pageable) {
    return repository.findAfterCursor(cursor, pageable);
}

@Override
protected Long extractId(NonExistingLoanApplication entity) {
    return entity.getId();
}

@Override
protected NonExistingLoanApplication mapToDTO(NonExistingLoanApplication entity) {
    return entity;
}

@Override
public CursorPage<NonExistingLoanApplication> getPage(Long cursor, int size) {
    return super.getPage(cursor, size);
}
}