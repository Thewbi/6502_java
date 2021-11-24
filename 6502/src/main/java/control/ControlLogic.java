package control;

import registers.Register;
import statemachine.StateMachine;

public interface ControlLogic {
	
	void setStateMachine(StateMachine stateMachine);
	
	Register getDestinationRegister();

}
