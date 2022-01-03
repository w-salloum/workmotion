package com.workmotion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.workmotion.domain.Employee;
import com.workmotion.domain.EmployeeEvents;
import com.workmotion.domain.EmployeeStates;
import com.workmotion.service.EmployeeService;

import reactor.core.publisher.Mono;


@SpringBootTest
@AutoConfigureMockMvc
public class StateMachineConfigTest{
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private StateMachineFactory<EmployeeStates, EmployeeEvents> factory;
	
	@Autowired
	private EmployeeService employeeService;
	
	/*
	 * Test the configration, starting from ADDED to be ACTIVE
	 */
	@Test
    public void testConfig() {
       
		StateMachine< EmployeeStates, EmployeeEvents> sm = factory.getStateMachine();
		sm.startReactively().subscribe();		
		System.out.println("state1 = "+sm.getState().getIds());
		
		Mono<Message<EmployeeEvents>> message = Mono.just(MessageBuilder
				.withPayload(EmployeeEvents.APPROVE_ADDED)
				.build());
		sm.sendEvent(message).subscribe();
		System.out.println("state2 = "+sm.getState().getIds());	
		

		message = Mono.just(MessageBuilder
				.withPayload(EmployeeEvents.APPROVE_SEC_CHECK)
				.build());
		sm.sendEvent(message).subscribe();
		System.out.println("state3 = "+sm.getState().getIds());
		
		message = Mono.just(MessageBuilder
				.withPayload(EmployeeEvents.APPROVE_WORK_PERMIT)
				.build());
		sm.sendEvent(message).subscribe();
		System.out.println("state4 = "+sm.getState().getIds());	
		
		
		message = Mono.just(MessageBuilder
				.withPayload(EmployeeEvents.FINAL_APPROVE)
				.build());
		sm.sendEvent(message).subscribe();
		System.out.println("state5 = "+sm.getState().getIds());
		
		assertEquals(sm.getState().getId(), EmployeeStates.ACTIVE);
              

    }
	
    /*
     * create an employee, change state to [INCHECK,SECURITY_CHECK_STARTED,WORK_PERMIT_CHECK_STARTED], 
     * then change the sate to [INCHECK,SECURITY_CHECK_STARTED,WORK_PERMIT_CHECK_FINISHED] 
     */
	@Test
	@Transactional
	void testAPICall() throws Exception{
		
		//1- create employee
		Employee emp = new Employee("Test Name","Contract Info",LocalDate.of(1990, 10, 22));
		emp = employeeService.save(emp);
		
		//2- test info API
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/employee/info?id="+emp.getId())).andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk());
		
		// 3- call API change the state from ADDED to INCHECK
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		params.add("id", emp.getId().toString());
		params.add("action",EmployeeEvents.APPROVE_ADDED.toString());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/employee/change").params(params)).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
		
		//4- call API change state to  [INCHECK,SECURITY_CHECK_STARTED,WORK_PERMIT_CHECK_FINISHED]
		params = new LinkedMultiValueMap<>();
		params.add("id", emp.getId().toString());
		params.add("action",EmployeeEvents.APPROVE_WORK_PERMIT.toString());
		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/employee/change").params(params)).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk());
		
		// 5- fetch the employee info from DB		
		Employee emp2 = this.employeeService.getById(emp.getId());
		
		//6- make sure the state is [INCHECK,SECURITY_CHECK_STARTED,WORK_PERMIT_CHECK_FINISHED]
		assertEquals(emp2.getState().get(0), EmployeeStates.INCHECK);
		assert(emp2.getState().contains(EmployeeStates.SECURITY_CHECK_STARTED));
		assert(emp2.getState().contains(EmployeeStates.WORK_PERMIT_CHECK_FINISHED));
	    
	}
	
	
    

}
