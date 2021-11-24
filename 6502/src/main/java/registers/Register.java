package registers;

public enum Register {
	
	A, // accumulator
	IR, // instruction register, contains the instruction
	X, // X register
	Y, // Y register
	S, // S register (what is that???)
	PC, // program counter
	DI, // DataIn from memory
	DIMUX, // ???
	OP, // ALU OP
	WriteRegister // Determines if value can be put from ALU into registers

}
