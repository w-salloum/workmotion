package com.workmotion.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public enum EmployeeStates {	
	ADDED , INCHECK , APPROVED , ACTIVE ,
	
	FORK, JOIN, // will use them for fork and join
	SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED,SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_FINISHED,
	
	REJECTED
	
}
