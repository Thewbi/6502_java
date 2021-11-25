package alu;

import control.ControlLogic;
import registers.RegisterFile;

public class DefaultALU implements ALU {

	private RegisterFile registerFile;

	private ControlLogic controlLogic;

	private int inputA;

	private int inputB;

	// which operation should the ALU perform?
	private ALU_OP operation = ALU_OP.UNKNOWN;

	private int add;
	
	private int zeroFlag;
	
	private int negativeFlag;

	@Override
	public void update() {
//		Register destinationRegister = controlLogic.getDestinationRegister();

//		System.out.println("[ALU] inputA: " + inputA + " inputB: " + inputB);
//		System.out.println("[ALU] operation: " + operation);

		// compute the operation and store the value in add
		// let someone else grab the value from add
		switch (operation) {
		case OR:
			add = inputA | inputB;
			break;

		case ADD:
			add = inputA + inputB;
			break;

		case UNKNOWN:
			add = inputA;
			// do nothing
			break;

		default:
			throw new RuntimeException("Not implemented yet!");
		}
		
		zeroFlag = (add == 0) ? 1 : 0;
		negativeFlag = ( add < 0) ? 1 : 0;
	}

	@Override
	public int getZeroFlag() {
		return zeroFlag;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

	public void setControlLogic(ControlLogic controlLogic) {
		this.controlLogic = controlLogic;
	}

	public int getInputA() {
		return inputA;
	}

	public void setInputA(int inputA) {
		this.inputA = inputA;
	}

	public int getInputB() {
		return inputB;
	}

	public void setInputB(int inputB) {
		this.inputB = inputB;
	}

	public ALU_OP getOperation() {
		return operation;
	}

	public void setOperation(ALU_OP operation) {
		this.operation = operation;
	}

	public int getAdd() {
		return add;
	}

	@Override
	public int getNegativeFlag() {
		return negativeFlag;
	}

}
