package cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import control.DefaultControlLogic;
import instructions.Instructions;
import registers.DefaultRegisterFile;
import registers.Register;
import statemachine.DefaultStateMachine;
import statemachine.State;

public class CPUTest {
	
	/**
	 * <ol>
	 * <li>Given StateMachine State is Decode</li>
	 * <li>When InstructionRegister contains a ORA instruction</li>
	 * <li>Then the destination register is A</li>
	 * </ol>
	 */
	@Test
	public void test_StateDecode_INSNOra_DestRegA() {
		
		// Arrange
		
		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, Instructions.ORA);

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
		defaultStateMachine.setState(State.DECODE);

		DefaultControlLogic defaultControlLogic = new DefaultControlLogic();
		defaultControlLogic.setStateMachine(defaultStateMachine);
		defaultControlLogic.setRegisterFile(defaultRegisterFile);
		
		// Act

		defaultControlLogic.update();
		
		// Assert

		assertEquals(Register.A, defaultControlLogic.getDestinationRegister());
	}

}
