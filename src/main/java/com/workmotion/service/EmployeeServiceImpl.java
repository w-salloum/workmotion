package com.workmotion.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.workmotion.domain.Employee;
import com.workmotion.domain.EmployeeEvents;
import com.workmotion.domain.EmployeeStates;
import com.workmotion.repository.EmployeeRepository;

import reactor.core.publisher.Mono;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private StateMachineFactory<EmployeeStates, EmployeeEvents> factory;

	public static final String EMP_ID_HEADER = "EMP_ID";

	public Employee save(Employee emp) {

		return this.employeeRepository.save(emp);
	}

	public Employee getById(Integer id) {

		return this.employeeRepository.findById(id).orElse(null);
	}

	private Employee sendEventAndSaveNewState(Employee emp, StateMachine<EmployeeStates, EmployeeEvents> sm, EmployeeEvents event) {
		
		Mono<Message<EmployeeEvents>> message = Mono
				.just(MessageBuilder.withPayload(event).setHeader(EMP_ID_HEADER, emp.getId()).build());
		
		sm.sendEvent(message).subscribe();

		emp.getState().clear();
		emp.setState(event,new ArrayList<EmployeeStates>(sm.getState().getIds()));
		return this.employeeRepository.save(emp);
	}

	
	private StateMachine<EmployeeStates, EmployeeEvents> buildStateNachine(Employee emp) {
		
		//Let us build the StateMachine
		
		StateMachine<EmployeeStates, EmployeeEvents> sm = this.factory.getStateMachine(emp.getId().toString());
		
		sm.stopReactively().subscribe();

		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			DefaultStateMachineContext<EmployeeStates,EmployeeEvents> ct = null;
			List<EmployeeStates> states = new ArrayList<EmployeeStates>(emp.getState());

			if (states.size() > 1) { // incase it is INCHECK state we need to check WORK and SECURITY state
				List<StateMachineContext<EmployeeStates, EmployeeEvents>> list = new ArrayList<>();
				states.subList(1, emp.getState().size()).forEach(s -> list.add(
						new DefaultStateMachineContext<EmployeeStates, EmployeeEvents>(s, null, null, null, null)));
				ct = new DefaultStateMachineContext<EmployeeStates, EmployeeEvents>(list, states.get(0), null, null,
						null, null);
				
			} else {
				
				ct = new DefaultStateMachineContext<EmployeeStates, EmployeeEvents>(states.get(0), null, null, null,
						null);
			}
			sma.resetStateMachineReactively(ct).subscribe();
		}

		);

		sm.startReactively().subscribe();

		
		return sm;
	}



	@Override
	@Transactional
	public Employee changeState(Integer employeeID, EmployeeEvents event) {		
		Employee emp = this.getById(employeeID);
		if(emp == null ) {
			return null;
		}
		StateMachine<EmployeeStates,EmployeeEvents> sm = this.buildStateNachine(emp);
		
		return this.sendEventAndSaveNewState(emp, sm, event);
	}
}
