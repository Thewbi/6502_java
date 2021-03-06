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

public class ORATest {

	/**
	 * This test will load 0xAA = 10101010 into the Accumulator (A) and it will
	 * execute the ORA command with the immediate operand 1.
	 * 
	 * This will OR the value 1 to the Accumulator and store the result into the
	 * accumulator, which yields 10101011 = 0xAB = 171
	 */
	@Test
	public void oraTest() {

		System.out.println("[Main.java] start");

		// Arrange

		DefaultMemory memory = new DefaultMemory();
		memory.setByte(0, Instructions.ORA_IMMEDIATE);
		memory.setByte(1, 0x01);
		memory.setByte(2, Instructions.ORA_IMMEDIATE);
		memory.setByte(3, 0x04);
		memory.setByte(4, Instructions.NOP);
		memory.setByte(5, Instructions.NOP);
		memory.setByte(6, Instructions.NOP);
		memory.setByte(7, Instructions.NOP);
		memory.setByte(8, Instructions.NOP);
		memory.setByte(9, Instructions.NOP);
		memory.setByte(10, Instructions.NOP);

		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, 0);
		defaultRegisterFile.setRegisterValue(Register.DI, memory.getByte(0));
		defaultRegisterFile.setRegisterValue(Register.PC, 0);
		defaultRegisterFile.setRegisterValue(Register.A, 0xAA); // 10101010 = 0xAA = 170d
		defaultRegisterFile.setRegisterValue(Register.X, 0);
		defaultRegisterFile.setRegisterValue(Register.Y, 0);
		defaultRegisterFile.setRegisterValue(Register.S, 0xFF); // stack pointer

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
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
		while (cycle < 6) {

			System.out.println(" ");
			System.out.println("[Main.java] cycle: " + cycle + " state: " + defaultStateMachine.getState());

			System.out.println("BEFORE:       " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ()
					+ " N: " + defaultControlLogic.getN());

			defaultControlLogic.determineDestinationRegister();
			defaultControlLogic.computeDataIn();
			defaultControlLogic.updateDimux();

			defaultControlLogic.updateIRHoldValid();
			defaultControlLogic.updateIR();

			defaultControlLogic.computeSpecialInstruction();
			defaultControlLogic.computeCompare();
			defaultControlLogic.computeBitIns();

			defaultControlLogic.computeALUOp();
			defaultControlLogic.computeALUOperation();

			defaultControlLogic.computeLoadReg();
			defaultControlLogic.computeWriteRegister();
			defaultControlLogic.computeLoadOnly();

			defaultControlLogic.computeALUAInput();
			defaultControlLogic.computeALUBInput();

			defaultControlLogic.computeSourceReg();
			defaultControlLogic.computeRegisterSelectLogic();

			System.out.println("AFTER DECODE: " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ()
					+ " N: " + defaultControlLogic.getN());

			alu.update();

			defaultControlLogic.computeZ();
			defaultControlLogic.computeN();

			// the output of the ALU is called add for some reason
			add = alu.getAdd();

			// write a value into a register
			defaultControlLogic.writeRegister(add);

			// determine how to update the PC (Program Counter) points to the
			// address of the next operation
			defaultControlLogic.updatePCIncrement();
			defaultControlLogic.updatePC();

			// next state
			defaultStateMachine.update();

			cycle++;

			System.out.println("AFTER:        " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ()
					+ " N: " + defaultControlLogic.getN());
		}

		// the CPU will store the result of the ALU operation into the A register
		assertEquals(Register.A, defaultControlLogic.getDestinationRegister());

		// the ALU has computed the correct value. 171 0xAB
		assertEquals(175, defaultRegisterFile.getRegisterValue(Register.A));
	}

}
