package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PetService petService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        List<Long> petIds = customerDTO.getPetIds();

        if (petIds != null && !petIds.isEmpty()) {
            List<Pet> pets = petIds.stream()
                    .map(id -> petService.getPet(id)
                            .orElseThrow(() -> new RuntimeException("Pet not found with id " + id)))
                    .collect(Collectors.toList());

            for (Pet pet : pets) {
                customer.addPet(pet);
            }
        }

        Customer savedCustomer = customerService.saveCustomer(customer);

        Customer updatedCustomer = customerService.getCustomerById(savedCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found after saving"));

        return convertToDTO(updatedCustomer);
    }



    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = customerService.getAllCustomers();
        return customers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Pet pet = petService.getPet(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id " + petId));
        Customer customer = pet.getCustomer();
        if (customer == null) throw new RuntimeException("Owner not found for pet " + petId);
        return convertToDTO(customer);
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        Employee saved = employeeService.saveEmployee(employee);
        return convertToDTO(saved);
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
        return convertToDTO(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));

        employee.setDaysAvailable(daysAvailable);
        employeeService.saveEmployee(employee);
    }


    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<Employee> employees = employeeService.findAvailableEmployees(
                employeeDTO.getSkills(), employeeDTO.getDate().getDayOfWeek()
        );
        return employees.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setNotes(dto.getNotes());
        return customer;
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setNotes(customer.getNotes());

        List<Long> petIds = new ArrayList<>();
        if (customer.getPets() != null) {
            for (Pet p : customer.getPets()) {
                petIds.add(p.getId());
            }
        }
        dto.setPetIds(petIds);
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setSkills(dto.getSkills());
        employee.setDaysAvailable(dto.getDaysAvailable());
        return employee;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSkills(employee.getSkills());
        dto.setDaysAvailable(employee.getDaysAvailable());
        return dto;
    }
}
