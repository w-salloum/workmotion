package com.workmotion.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workmotion.domain.Employee;
import com.workmotion.domain.EmployeeEvents;
import com.workmotion.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;

	@Operation(summary = "Create Employee")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Employee has been created, and the API return the employee info as JSON", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad Request- Missing data or date format exception", content = @Content) })
	@PostMapping("/create")
	public ResponseEntity<Object> create(@Parameter(description="Employee Name") 
										@RequestParam(name = "name") String name,
										@Parameter(description="Contract Info") 
										@RequestParam(name = "contractInfo") String contractInfo, 
										@Parameter(description="Date of birth, should be in this format yyyy-mm-dd 1990-02-03") 
										@RequestParam(name = "dateOfBirth") String dateOfBirth){
		LocalDate date =null;
		try {
		 date = LocalDate.parse(dateOfBirth);
		}catch (DateTimeParseException e) {
			return new ResponseEntity<>(ResponseEntity.badRequest().build(),HttpStatus.BAD_REQUEST);
		}
		Employee emp = new Employee(name,contractInfo,date);
		this.employeeService.save(emp);
		return new ResponseEntity<>(emp,HttpStatus.CREATED);
	}
	
	@Operation(summary = "Get employee info by employee ID")

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retun the employee info as JSON, the history field will show you all the actions you did with the response states for that actions , the state field  will have the current state", content = {					@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class)) }),
			@ApiResponse(responseCode = "404", description = "Employee not found", content = @Content) })

	@GetMapping("/info")
	public ResponseEntity<Object> info(@Parameter(description="Employee ID") 
									   @RequestParam(name = "id") Integer id) {
		
		
		Employee emp =  this.employeeService.getById(id);
		if (emp ==null) {			
			return new ResponseEntity<Object>(ResponseEntity.notFound().build(),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(emp,HttpStatus.OK);
	}
	
	@Operation(summary = "Change the employee state")

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retun the employee info as JSON, the history field will show you all the actions you did with the response states for that actions , the state field  will have the current state", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class)) }),
			@ApiResponse(responseCode = "404", description = "Employee not found", content = @Content) ,
			@ApiResponse(responseCode = "400", description = "Bad request, action not defined", content = @Content) })

	@PostMapping("/change")
	public ResponseEntity<Object> change(@Parameter(description="Employee ID") 
										@RequestParam(name = "id") Integer id,
										@Parameter(description="Could be one of these actions [APPROVE_ADDED, APPROVE_WORK_PERMIT ,APPROVE_SEC_CHECK, FINAL_APPROVE,REJECT_ADDED, REJECT_WORK_PERMIT ,REJECT_SEC_CHECK, FINAL_REJECT]") 
										@RequestParam(name = "action")String action) {
		
		Employee emp =  this.employeeService.getById(id);
		if (emp ==null) {			
			return new ResponseEntity<Object>(ResponseEntity.notFound().build(),HttpStatus.NOT_FOUND);
		}
		
		if (!EnumUtils.isValidEnum(EmployeeEvents.class, action) ) {			
			return new ResponseEntity<Object>(ResponseEntity.notFound().build(),HttpStatus.BAD_REQUEST);
		}
		
		emp = this.employeeService.changeState(id, EmployeeEvents.valueOf(action));
		return new ResponseEntity<>(emp,HttpStatus.OK);
				
		
	}
	
	
}
