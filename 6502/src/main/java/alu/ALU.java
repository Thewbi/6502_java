package alu;

import control.ControlLogic;
import registers.RegisterFile;

public interface ALU {

	void setRegisterFile(RegisterFile registerFile);

	void setControlLogic(ControlLogic controlLogic);

	void update();

	int getInputA();

	void setInputA(int inputA);

	int getInputB();

	void setInputB(int inputB);
	
	ALU_OP getOperation();

	void setOperation(ALU_OP operation);
	
	int getAdd();

	int getZeroFlag();

	int getNegativeFlag();

}
