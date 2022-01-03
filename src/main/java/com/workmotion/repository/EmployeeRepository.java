package com.workmotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.workmotion.domain.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

}
