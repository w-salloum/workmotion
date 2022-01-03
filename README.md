# Workmotion Employee

Building a platform including an employee management system.

The employees on this system are assigned to different states.

The states (State machine) for A given Employee are:

    ADDED
    IN-CHECK
    APPROVED
    ACTIVE
Initially when an employee is added it will be assigned "ADDED" state automatically.
The allowed state transitions are:
	
	ADDED -> IN-CHECK *-> APPROVED -> ACTIVE

Furthermore, IN-CHECK state is special and has the following orthogonal child substates:

    SECURITY_CHECK_STARTED
    SECURITY_CHECK_FINISHED
    WORK_PERMIT_CHECK_STARTED
    WORK_PERMIT_CHECK_FINISHED


# Technologies
Java 11, Spring-boot and Spring State Machine

# How to run it
- Clone the main project by this command:
 `git clone https://github.com/w-salloum/workmotion`
- From the terminal go to ./workmotion and use maven to build the project `mvn clean package`

- Run the application by `java -jar workmotion/target/workmotion-employee.jar` , it will run it on the port 8082 ( you can change the port from application.properties)

- Or you can use docker, first run  `docker build -t workmotion-employee.jar . ` , then run the docker by `docker run -p 8082:8082 workmotion-employee.jar` 

- Go to http://localhost:8082/api-docs.html to check APIs documentation (build using OpenAPI Swagger ) 
- To create an employee, use POST API http://localhost:8082/api/employee/create
- To check employee info, use GET API http://localhost:8082/api/employee/info?id=EmployeeID
- To change state for an employee, use POST API http://localhost:8082/api/employee/change

# How it works
- When we create an employee it will be in ADDED state automatically.
- If you want to change the employee state, you need to call `api/employee/change` API as apost request with tow parameters 
1- `id` : employee ID 2- `action` which could be one of the following 
`[APPROVE_ADDED, APPROVE_WORK_PERMIT ,APPROVE_SEC_CHECK, FINAL_APPROVE,REJECT_ADDED, REJECT_WORK_PERMIT ,REJECT_SEC_CHECK, FINAL_REJECT]` which are used to move between the states `[ADDED ,INCHECK , APPROVED , ACTIVE ,	SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED,SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_FINISHED,	REJECTED]`
- Employee object has a state field witch shows the current state 
- Employee object has a history field witch shows the EVENT and the STATE in order, incase you use the wrong EVENT with the employee state, it will not move and it will stay in the current state, and the history will show that , in the following example , I created an employee and used the action APPROVE_ADDED, then APPROVE_WORK_PERMIT
```JSON
{
  "id": 1,
  "name": "Test Employee",
  "contractInfo": "Contract Info",
  "dateOfBirth": "1990-02-03",
  "state": [
    "INCHECK",
    "SECURITY_CHECK_STARTED",
    "WORK_PERMIT_CHECK_FINISHED"
  ],
  "history": [
    "Initial State:[ADDED]",
    "Event:APPROVE_ADDED State:[INCHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED]",
    "Event:APPROVE_WORK_PERMIT State:[INCHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED]"
  ]
}
```
now let us use FINAL_APPROVE ( which is supposed to move the state from APPROVED to ACTIVE), we will see the state does not change and history will show that action
```JSON
{
  "id": 1,
  "name": "Test Employee",
  "contractInfo": "Contract Info",
  "dateOfBirth": "1990-02-03",
  "state": [
    "INCHECK",
    "SECURITY_CHECK_STARTED",
    "WORK_PERMIT_CHECK_FINISHED"
  ],
  "history": [
    "Initial State:[ADDED]",
    "Event:APPROVE_ADDED State:[INCHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED]",
    "Event:APPROVE_WORK_PERMIT State:[INCHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED]",
    "Event:FINAL_APPROVE State:[INCHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED]"
  ]
}
```
# Testing

- There is a unit test to test the StateMachine configuration and another test to test calling API and the expected states after that.
