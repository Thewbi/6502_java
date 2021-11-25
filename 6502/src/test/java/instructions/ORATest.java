package instructions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import alu.DefaultALU;
import control.DefaultControlLogic;
import memory.DefaultMemory;
import registers.DefaultRegisterFile;
import registers.Register;
import statemachine.DefaultStateMachine;
import statemachine.State;
import util.FormatUtil;

public class ORATest {
	
	/**
	 * This test will load 0xAA = 10101010 into the Accumulator (A) and
	 * it will execute the ORA command with the immediate operand 1.
	 * 
	 * This will OR the value 1 to the Accumulator and store the result into 
	 * the accumulator, which yields 10101011 = 0xAB = 171
	 */
	@Test
	public void oraTest() {
		
		DefaultMemory memory = new DefaultMemory();
		memory.setByte(0, Instructions.ORA_IMMEDIATE);
		memory.setByte(1, 0x01);
		memory.setByte(2, Instructions.ORA_IMMEDIATE);
		memory.setByte(3, 0x04);
		memory.setByte(4, 0x00);
		memory.setByte(5, 0x00);
		memory.setByte(6, 0x00);
		memory.setByte(7, 0x00);
		memory.setByte(8, 0x00);
		memory.setByte(9, 0x00);
		memory.setByte(10, 0x00);

		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, Instructions.ORA_IMMEDIATE);
		defaultRegisterFile.setRegisterValue(Register.PC, 0);
		defaultRegisterFile.setRegisterValue(Register.A, 0xAA); // 10101010

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
		defaultStateMachine.setState(State.DECODE);
		defaultStateMachine.setRegisterFile(defaultRegisterFile);

		DefaultControlLogic defaultControlLogic = new DefaultControlLogic();
		defaultControlLogic.setStateMachine(defaultStateMachine);
		defaultControlLogic.setRegisterFile(defaultRegisterFile);
		defaultControlLogic.setMemory(memory);
		
		DefaultALU alu = new DefaultALU();
		alu.setRegisterFile(defaultRegisterFile);
		alu.setControlLogic(defaultControlLogic);
		
		defaultControlLogic.setAlu(alu);
		
		// Act

		int cycle = 0;
		while (cycle < 3) {
			
			System.out.println(" ");
			System.out.println("[Main.java] cycle: " + cycle + " state: " + defaultStateMachine.getState());
		
			defaultControlLogic.update();
			
			defaultControlLogic.computeALUOp();
			defaultControlLogic.computeALUOperation();
			
			defaultControlLogic.computeALUAInput();
			defaultControlLogic.computeALUBInput();
			
			defaultControlLogic.computeWriteRegister();
			defaultControlLogic.computeLoadOnly();
			
			alu.update();
			
			// the output of the ALU is called add for some reason
			int add = alu.getAdd();
			System.out.println("ALU OUTPUT: " + FormatUtil.intToHex(add));
			defaultControlLogic.storeAdd(add);
			
			// determine how to update the PC (Program Counter) points to the 
			// address of the next operation
			defaultControlLogic.updatePCIncrement();
			defaultControlLogic.updatePC();
			
			// next state
			defaultStateMachine.update();
			
			cycle++;
		}
		
		// Assert
		
		// the CPU will store the result of the ALU operation into the A register
		assertEquals(Register.A, defaultControlLogic.getDestinationRegister());
		
		// the ALU has computed the correct value. 171 0xAB
		assertEquals(171, alu.getAdd());
	}

}
