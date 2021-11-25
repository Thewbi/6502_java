package main;

import alu.DefaultALU;
import control.DefaultControlLogic;
import instructions.Instructions;
import memory.DefaultMemory;
import registers.DefaultRegisterFile;
import registers.Register;
import statemachine.DefaultStateMachine;
import statemachine.State;
import util.FormatUtil;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("[Main.java] start");
		
		// Arrange
		
		DefaultMemory memory = new DefaultMemory();
//		memory.setByte(0, Instructions.ORA);
//		memory.setByte(1, 0x01);
//		memory.setByte(2, 0x00);
//		memory.setByte(3, 0x00);
//		memory.setByte(4, 0x00);
//		memory.setByte(5, 0x00);
		
//		memory.setByte(0, Instructions.LDA_IMMEDIATE);
//		memory.setByte(1, 0x01);
//		memory.setByte(2, 0x00);
//		memory.setByte(3, 0x00);
//		memory.setByte(4, 0x00);
//		memory.setByte(5, 0x00);
//		memory.setByte(6, 0x00);
//		memory.setByte(7, 0x00);
//		memory.setByte(8, 0x00);
//		memory.setByte(9, 0x00);
//		memory.setByte(10, 0x00);
		
//		memory.setByte(0, Instructions.LDX_IMMEDIATE);
//		memory.setByte(1, 0x01);
//		memory.setByte(2, Instructions.NOP);
//		memory.setByte(3, Instructions.NOP);
//		memory.setByte(4, Instructions.NOP);
//		memory.setByte(5, Instructions.NOP);
		
		memory.setByte(0, Instructions.NOP);
		memory.setByte(1, Instructions.NOP);
		memory.setByte(2, Instructions.NOP);
		memory.setByte(3, Instructions.NOP);
		memory.setByte(4, Instructions.NOP);
		memory.setByte(5, Instructions.NOP);
		memory.setByte(6, Instructions.NOP);
		memory.setByte(7, Instructions.NOP);
		memory.setByte(8, Instructions.NOP);
		memory.setByte(9, Instructions.NOP);
		
//		memory.setByte(0, 0x00);
//		memory.setByte(1, 0x00);
//		memory.setByte(2, 0x00);
//		memory.setByte(3, 0x00);
//		memory.setByte(4, 0x00);
//		memory.setByte(5, 0x00);
//		memory.setByte(6, 0x00);
//		memory.setByte(7, 0x00);
//		memory.setByte(8, 0x00);
//		memory.setByte(9, 0x00);
//		memory.setByte(10, 0x00);

		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, memory.getByte(0));
		defaultRegisterFile.setRegisterValue(Register.PC, 0);
		defaultRegisterFile.setRegisterValue(Register.A, 0xAA); // 10101010
		defaultRegisterFile.setRegisterValue(Register.X, 0);
		defaultRegisterFile.setRegisterValue(Register.Y, 0);
		defaultRegisterFile.setRegisterValue(Register.S, 0xFF); // stack pointer

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
		
		int add = 0;

		int cycle = 0;
		while (cycle < 6) {
			
			System.out.println(" ");
			System.out.println("[Main.java] cycle: " + cycle + " state: " + defaultStateMachine.getState());
			
			System.out.println("BEFORE: " + defaultRegisterFile.toString() + " Z: " + defaultControlLogic.getZ() + " N: " + defaultControlLogic.getN());
		
			defaultControlLogic.update();
			
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

	}

}
