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

public class LDXTest {
	
	/**
	 * IMMEDIATE LDX loads a immediate value into the X register and updates the Z(ero) and N(egative) flags
	 */
	@Test
	public void oraTest() {
		
		DefaultMemory memory = new DefaultMemory();
		memory.setByte(0, Instructions.LDX_IMMEDIATE);
		memory.setByte(1, 0x01);
		memory.setByte(2, Instructions.NOP);
		memory.setByte(3, Instructions.NOP);
		memory.setByte(4, Instructions.NOP);
		memory.setByte(5, Instructions.NOP);
		
		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, memory.getByte(0));
		defaultRegisterFile.setRegisterValue(Register.PC, 0);
		defaultRegisterFile.setRegisterValue(Register.A, 0xAA); // 10101010
		defaultRegisterFile.setRegisterValue(Register.X, 0);
		defaultRegisterFile.setRegisterValue(Register.Y, 0);
		defaultRegisterFile.setRegisterValue(Register.S, 0xFF); // stack pointer

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
//		defaultStateMachine.setState(State.DECODE);
		defaultStateMachine.setState(State.FETCH);
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
		
		int add = 0;

		int cycle = 0;
		while (cycle < 3) {
			
			System.out.println(" ");
			System.out.println("[Main.java] cycle: " + cycle + " state: " + defaultStateMachine.getState());
			
			System.out.println("BEFORE: " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ() + " N: " + defaultControlLogic.getN());
		
//			defaultControlLogic.update();
			defaultControlLogic.determineDestinationRegister();
			defaultControlLogic.computeDataIn();
			defaultControlLogic.updateDimux();
			
			defaultControlLogic.computeSpecialInstruction();
			defaultControlLogic.computeCompare();
			defaultControlLogic.computeBitIns();
			
			defaultControlLogic.computeALUOp();
			defaultControlLogic.computeALUOperation();
			
//			defaultControlLogic.computeALUAInput();
//			defaultControlLogic.computeALUBInput();
			
			defaultControlLogic.computeLoadReg();
			defaultControlLogic.computeWriteRegister();
//			defaultControlLogic.writeRegister(add);
			defaultControlLogic.computeLoadOnly();
			
			defaultControlLogic.computeALUAInput();
			defaultControlLogic.computeALUBInput();
			
			defaultControlLogic.computeSourceReg();
			defaultControlLogic.computeRegisterSelectLogic();
			
			alu.update();
			
			defaultControlLogic.computeZ();
			defaultControlLogic.computeN();
			
			// the output of the ALU is called add for some reason
			add = alu.getAdd();
//			System.out.println("ALU OUTPUT: " + FormatUtil.intToHex(add));
			
			// write a value into a register
			defaultControlLogic.writeRegister(add);
			
			// determine how to update the PC (Program Counter) points to the 
			// address of the next operation
			defaultControlLogic.updatePCIncrement();
			defaultControlLogic.updatePC();
			
			// next state
			defaultStateMachine.update();
			
			cycle++;
			
			System.out.println("AFTER:  " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ() + " N: " + defaultControlLogic.getN());
		}
		
		// Assert
		
		// loading the value 1 causes zero to be 0 and negative to be 0
		assertEquals(0, defaultControlLogic.getZ());
		assertEquals(0, defaultControlLogic.getN());
		
		// the X register contains the value 1
		assertEquals(1, defaultRegisterFile.getRegisterValue(Register.X));
	}

}
