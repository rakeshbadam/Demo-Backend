package com.example.backend.service;

import com.example.backend.dto.CustomerDTO;
import com.example.backend.entity.Customer;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.pagination.AbstractCursorService;
import com.example.backend.pagination.CursorPage;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl
        extends AbstractCursorService<Customer, CustomerDTO>
        implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = mapToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id)
                );
        return convertToDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id)
                );

        existingCustomer.setCustomerName(customerDTO.getCustomerName());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        existingCustomer.setEmail(customerDTO.getEmail()); // ✅ NEW
        existingCustomer.setIncome(customerDTO.getIncome());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    protected List<Customer> fetchAfterCursor(Long cursor, Pageable pageable) {
        return customerRepository.findAfterCursor(cursor, pageable);
    }

    @Override
    protected Long extractId(Customer entity) {
        return entity.getCustomerId();
    }

    @Override
    protected CustomerDTO mapToDTO(Customer entity) {
        return convertToDTO(entity);
    }

    @Override
    public CursorPage<CustomerDTO> getPage(Long cursor, int size) {
        return super.getPage(cursor, size);
    }

    // ENTITY → DTO
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmail(customer.getEmail()); // ✅ NEW
        dto.setIncome(customer.getIncome());
        dto.setCreatedTime(customer.getCreatedTime());
        dto.setModifiedTime(customer.getModifiedTime());
        return dto;
    }

    // DTO → ENTITY
    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(dto.getEmail()); // ✅ NEW
        customer.setIncome(dto.getIncome());
        return customer;
    }
}