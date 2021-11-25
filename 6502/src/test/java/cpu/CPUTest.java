package cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import control.DefaultControlLogic;
import instructions.Instructions;
import memory.DefaultMemory;
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
		
		DefaultMemory memory = new DefaultMemory();
		memory.setByte(0, Instructions.LDX_IMMEDIATE);
		memory.setByte(1, 0x01);
		memory.setByte(2, Instructions.NOP);
		memory.setByte(3, Instructions.NOP);
		memory.setByte(4, Instructions.NOP);
		memory.setByte(5, Instructions.NOP);
		
		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, Instructions.ORA_IMMEDIATE);
		defaultRegisterFile.setRegisterValue(Register.PC, 0);

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
		defaultStateMachine.setState(State.DECODE);

		DefaultControlLogic defaultControlLogic = new DefaultControlLogic();
		defaultControlLogic.setStateMachine(defaultStateMachine);
		defaultControlLogic.setRegisterFile(defaultRegisterFile);
		defaultControlLogic.setMemory(memory);
		
		// Act

//		defaultControlLogic.update();
		defaultControlLogic.determineDestinationRegister();
		defaultControlLogic.computeDataIn();
		defaultControlLogic.updateDimux();
		
		// Assert

		assertEquals(Register.A, defaultControlLogic.getDestinationRegister());
	}

}
