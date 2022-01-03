package com.workmotion.service;


import com.workmotion.domain.Employee;
import com.workmotion.domain.EmployeeEvents;

public interface EmployeeService {
	
	public Employee save(Employee emp) ;
	public Employee getById(Integer id);	
	
	public Employee changeState(Integer employeeID,EmployeeEvents event);
	
}
