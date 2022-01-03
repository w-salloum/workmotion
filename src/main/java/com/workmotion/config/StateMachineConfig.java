package com.workmotion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.workmotion.domain.EmployeeEvents;
import com.workmotion.domain.EmployeeStates;


@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<EmployeeStates, EmployeeEvents>{
	
	@Override
    public void configure(StateMachineStateConfigurer<EmployeeStates, EmployeeEvents> states) throws Exception {		
		
		/* ADDED -> INCHECK {
		*						SECURITY_CHECK_STARTED    -> SECURITY_CHECK_FINISHED
		*					  	WORK_PERMIT_CHECK_STARTED -> WORK_PERMIT_CHECK_FINISHED
		*					} 
		*					-> APPROVED -> ACTIVE
		* I will add a reject state, for the rejection event  
		*/
		
		 states
         .withStates()
         .initial(EmployeeStates.ADDED)
         .fork(EmployeeStates.FORK)
         .state(EmployeeStates.INCHECK)
         .join(EmployeeStates.JOIN)
         .state(EmployeeStates.APPROVED)
         .end(EmployeeStates.ACTIVE)
         .end(EmployeeStates.REJECTED)
         
         .and()
         .withStates()
             .parent(EmployeeStates.INCHECK)
             .initial(EmployeeStates.SECURITY_CHECK_STARTED)
             .end(EmployeeStates.SECURITY_CHECK_FINISHED)
         .and()
         .withStates()
             .parent(EmployeeStates.INCHECK)
             .initial(EmployeeStates.WORK_PERMIT_CHECK_STARTED)
             .end(EmployeeStates.WORK_PERMIT_CHECK_FINISHED);
		 
		   }
	@Override
    public void configure(StateMachineTransitionConfigurer<EmployeeStates, EmployeeEvents> transitions) throws Exception {
		
		transitions.withExternal()
        .source(EmployeeStates.ADDED).target(EmployeeStates.INCHECK).event(EmployeeEvents.APPROVE_ADDED)
        .and().withExternal()
            .source(EmployeeStates.SECURITY_CHECK_STARTED).target(EmployeeStates.SECURITY_CHECK_FINISHED).event(EmployeeEvents.APPROVE_SEC_CHECK)
        .and().withExternal()
            .source(EmployeeStates.WORK_PERMIT_CHECK_STARTED).target(EmployeeStates.WORK_PERMIT_CHECK_FINISHED).event(EmployeeEvents.APPROVE_WORK_PERMIT)        
        .and()
        .withFork()
            .source(EmployeeStates.FORK)
            .target(EmployeeStates.INCHECK)
        .and()
        .withJoin()
            .source(EmployeeStates.INCHECK)
            .target(EmployeeStates.JOIN)
         .and().withExternal()
         .source(EmployeeStates.JOIN).target(EmployeeStates.APPROVED)
         .and().withExternal()
         	.source(EmployeeStates.APPROVED).target(EmployeeStates.ACTIVE).event(EmployeeEvents.FINAL_APPROVE)
         
         .and().withExternal()
         .source(EmployeeStates.ADDED).target(EmployeeStates.REJECTED).event(EmployeeEvents.REJECT_ADDED)
         
         .and().withExternal()
         .source(EmployeeStates.SECURITY_CHECK_STARTED).target(EmployeeStates.REJECTED).event(EmployeeEvents.REJECT_SEC_CHECK)
         
         .and().withExternal()
         .source(EmployeeStates.WORK_PERMIT_CHECK_STARTED).target(EmployeeStates.REJECTED).event(EmployeeEvents.REJECT_WORK_PERMIT)
         .and().withExternal()
         .source(EmployeeStates.APPROVED).target(EmployeeStates.REJECTED).event(EmployeeEvents.FINAL_REJECT)      
		 ;
		
				
    }
	
	// to log the state changes 
	/*
	@Override
	public void configure(StateMachineConfigurationConfigurer<EmployeeStates, EmployeeEvents> config) throws Exception {
		// TODO Auto-generated method stub
		StateMachineListenerAdapter<EmployeeStates, EmployeeEvents> adapter = new StateMachineListenerAdapter<>() {
						
			@Override
			public void stateChanged(State<EmployeeStates, EmployeeEvents> from,
					State<EmployeeStates, EmployeeEvents> to) {
				
					System.out.println("State changed from " + from );
					System.out.println("              to " + to );
				
			}

		};
		
		config.withConfiguration().listener(adapter);
	}
	*/
	}
