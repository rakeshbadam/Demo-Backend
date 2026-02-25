package com.example.backend.service;

import com.example.backend.dto.CustomerDTO;

import java.util.List;
import com.example.backend.pagination.CursorPage;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Long id);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);
    CursorPage<CustomerDTO> getPage(Long cursor, int size);
    
    
}
