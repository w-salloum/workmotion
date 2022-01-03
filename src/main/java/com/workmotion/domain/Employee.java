package com.workmotion.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Employee {
	
	@Id
	@GeneratedValue
	private Integer id;
	private String name;
	private String contractInfo;
	private LocalDate dateOfBirth;
	
	
	
	@ElementCollection
	private List<EmployeeStates> state; 
	
	@ElementCollection
	private List<String> history;
	
	public Employee() {
		this("","",null);
	}
	
	public Employee(String name, String contractInfo, LocalDate dateOfBirth) {
		// TODO Auto-generated constructor stub
		;
		this.setName(name);
		this.setContractInfo(contractInfo);
		this.setDateOfBirth(dateOfBirth);
		this.setHistory(new ArrayList<>());
		this.setState(null,(new ArrayList<EmployeeStates>() {{add(EmployeeStates.ADDED);}}));
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContractInfo() {
		return contractInfo;
	}
	public void setContractInfo(String contractInfo) {
		this.contractInfo = contractInfo;
	}
	
	public List<EmployeeStates> getState() {
		return this.state;
	}
	public void setState(EmployeeEvents event, List<EmployeeStates> state) {
		this.state = state;
		if (event == null) {
			this.getHistory().add( "Initial State:" + state.toString());
		}else {
			this.getHistory().add("Event:"+ event+ " State:" + state.toString());
		}
		
	}
	public List<String> getHistory() {
		return history;
	}
	public void setHistory(List<String> history) {
		this.history = history;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	
	

	

}
