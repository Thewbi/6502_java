package main;

import control.DefaultControlLogic;
import instructions.Instructions;
import registers.DefaultRegisterFile;
import registers.Register;
import statemachine.DefaultStateMachine;
import statemachine.State;

public class Main {

	public static void main(String[] args) {
		System.out.println("start");

		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, Instructions.ORA);

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
//		defaultStateMachine.setState(State.FETCH);
		defaultStateMachine.setState(State.DECODE);

		DefaultControlLogic defaultControlLogic = new DefaultControlLogic();
		defaultControlLogic.setStateMachine(defaultStateMachine);
		defaultControlLogic.setRegisterFile(defaultRegisterFile);

		defaultControlLogic.update();

		defaultControlLogic.getDestinationRegister();

	}

}
