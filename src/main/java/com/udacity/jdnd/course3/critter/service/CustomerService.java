package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    @Autowired
    public CustomerService(CustomerRepository customerRepository, PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }
    public Customer saveCustomer(Customer customer){
        return customerRepository.save(customer);
    }
    public List<Pet> getPetsByCustomer(Long customerId) {
        return petRepository.findPetByOwnerId(customerId);
    }
    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


}
