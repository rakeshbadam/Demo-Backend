package com.example.backend.service;

import com.example.backend.dto.CustomerDTO;
import com.example.backend.entity.Customer;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // 🔹 CREATE
    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        Customer customer = mapToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        return mapToDTO(savedCustomer);
    }

    // 🔹 GET ALL
    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🔹 GET BY ID
    @Override
    public CustomerDTO getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id)
                );

        return mapToDTO(customer);
    }

    // 🔹 UPDATE
    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id)
                );

        existingCustomer.setCustomerName(customerDTO.getCustomerName());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        existingCustomer.setIncome(customerDTO.getIncome());

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return mapToDTO(updatedCustomer);
    }

    // 🔹 DELETE
    @Override
    public void deleteCustomer(Long id) {

        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
    }

    // ==========================
    // 🔹 ENTITY → DTO
    // ==========================
    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setIncome(customer.getIncome());
        dto.setCreatedTime(customer.getCreatedTime());
        dto.setModifiedTime(customer.getModifiedTime());
        return dto;
    }

    // ==========================
    // 🔹 DTO → ENTITY
    // ==========================
    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setIncome(dto.getIncome());
        return customer;
    }
}
