package com.example.backend.controller;

import com.example.backend.dto.CustomerDTO;
import com.example.backend.pagination.CursorPage;
import com.example.backend.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;         
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    //  POST - Create Customer
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    //  GET - Get All Customers
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    //  GET - Get Customer By ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {

        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    //  PUT - Update Customer
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {

        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    //  DELETE - Delete Customer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {

        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @GetMapping("/cursor")
    public ResponseEntity<CursorPage<CustomerDTO>> getCustomersCursor(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                customerService.getPage(cursor, size)
        );
    }
}
