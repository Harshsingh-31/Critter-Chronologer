package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Pet pet = convertToEntity(petDTO);

        if (petDTO.getOwnerId() != 0) {
            Customer owner = customerService.getCustomerById(petDTO.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id " + petDTO.getOwnerId()));
            pet.setCustomer(owner);
        }

        Pet saved = petService.savePet(pet);

        if (saved.getCustomer() != null) {
            Customer owner = saved.getCustomer();
            if (owner.getPets() == null) {
                owner.setPets(new ArrayList<>());
            }
            if (!owner.getPets().contains(saved)) {
                owner.getPets().add(saved);
            }
            customerService.saveCustomer(owner);
        }

        return convertToDTO(saved);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        Pet pet = petService.getPet(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id " + petId));
        return convertToDTO(pet);
    }

    @GetMapping
    public List<PetDTO> getPets() {
        return petService.getAllPets()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<Pet> pets = customerService.getPetsByCustomer(ownerId);
        return pets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private Pet convertToEntity(PetDTO dto) {
        Pet pet = new Pet();
        if (dto.getId() != 0) {
            pet.setId(dto.getId());
        }
        pet.setName(dto.getName());
        pet.setType(dto.getType());
        pet.setBirthDate(dto.getBirthDate());
        pet.setNotes(dto.getNotes());
        return pet;
    }

    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setBirthDate(pet.getBirthDate());
        dto.setNotes(pet.getNotes());
        if (pet.getCustomer() != null) {
            dto.setOwnerId(pet.getCustomer().getId());
        }
        return dto;
    }
}
