package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PetService petService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        List<Employee> employees = employeeService.getEmployeesByIds(scheduleDTO.getEmployeeIds());
        List<Pet> pets = petService.getPetsByIds(scheduleDTO.getPetIds());

        Schedule schedule = convertDTOToEntity(scheduleDTO, employees, pets);
        Schedule saved = scheduleService.saveSchedule(schedule);
        return convertEntityToDTO(saved);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleService.getAllSchedules()
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return scheduleService.getAllSchedules()
                .stream()
                .filter(s -> s.getPets().stream().anyMatch(p -> p.getId().equals(petId)))
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return scheduleService.getAllSchedules()
                .stream()
                .filter(s -> s.getEmployees().stream().anyMatch(e -> e.getId().equals(employeeId)))
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        return scheduleService.getAllSchedules()
                .stream()
                .filter(s -> s.getPets().stream().anyMatch(p -> p.getCustomer() != null
                        && p.getCustomer().getId().equals(customerId)))
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    private ScheduleDTO convertEntityToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setActivities(schedule.getActivities());
        dto.setEmployeeIds(schedule.getEmployees().stream().map(Employee::getId).collect(Collectors.toList()));
        dto.setPetIds(schedule.getPets().stream().map(Pet::getId).collect(Collectors.toList()));
        return dto;
    }

    private Schedule convertDTOToEntity(ScheduleDTO dto, List<Employee> employees, List<Pet> pets) {
        Schedule schedule = new Schedule();
        schedule.setId(dto.getId());
        schedule.setDate(dto.getDate());
        schedule.setActivities(dto.getActivities());
        schedule.setEmployees(employees);
        schedule.setPets(pets);
        return schedule;
    }
}
