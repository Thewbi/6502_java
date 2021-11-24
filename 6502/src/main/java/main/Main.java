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
		
		memory.setByte(0, Instructions.ORA);
		memory.setByte(1, 0x01);
		memory.setByte(2, Instructions.ORA);
		memory.setByte(3, 0x04);
		memory.setByte(4, 0x00);
		memory.setByte(5, 0x00);
		memory.setByte(6, 0x00);
		memory.setByte(7, 0x00);
		memory.setByte(8, 0x00);
		memory.setByte(9, 0x00);
		memory.setByte(10, 0x00);

		DefaultRegisterFile defaultRegisterFile = new DefaultRegisterFile();
		defaultRegisterFile.setRegisterValue(Register.IR, Instructions.ORA);
		defaultRegisterFile.setRegisterValue(Register.PC, 0);
		defaultRegisterFile.setRegisterValue(Register.A, 0xAA); // 10101010

		DefaultStateMachine defaultStateMachine = new DefaultStateMachine();
		defaultStateMachine.setState(State.DECODE);
//		defaultStateMachine.setState(State.FETCH);
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
		while (cycle < 9) {
			
			System.out.println(" ");
			System.out.println("[Main.java] cycle: " + cycle + " state: " + defaultStateMachine.getState());
		
			defaultControlLogic.update();
			
			// next state
//			defaultStateMachine.update();
			
			defaultControlLogic.computeALUOp();
			defaultControlLogic.computeALUOperation();
			
//			defaultStateMachine.update();
			
			defaultControlLogic.computeALUBInput();
			defaultControlLogic.computeALUAInput();
			
			defaultControlLogic.computeWriteRegister();
			
//			defaultStateMachine.update();
			
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

	}

}
