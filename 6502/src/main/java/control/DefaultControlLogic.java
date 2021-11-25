package control;

import alu.ALU;
import alu.ALU_OP;
import instructions.Instructions;
import memory.Memory;
import registers.Register;
import registers.RegisterFile;
import statemachine.State;
import statemachine.StateMachine;
import util.BitUtil;
import util.FormatUtil;

public class DefaultControlLogic implements ControlLogic {

	private StateMachine stateMachine;

	private Register destinationRegister;

	private RegisterFile registerFile;

	private Memory memory;

	private ALU alu;

	private int pcIncrement = 0;

	// LDA/LDX/LDY instruction
	private int load_only = 0;

	// loading a register (A, X, Y, S) in this instruction
	private int load_reg = 0;

	private int write_register = 0;

	// 0 = A, 1 = X, 2 = Y, 3 = S
	// private int regsel;
	private Register regsel;

	// private int src_reg;
	private Register src_reg;
	
	// 1 iff PLP instruction is decoded currently
	private int plp = 0;
	
	// zero flag is initially set to 1
	private int z = 1;
	
	// negative flag is initially set to 0
	private int n = 0;
	
	// doing CMP/CPY/CPX
	private int compare = 0;
	
	// doing BIT zp/abs 
	private int bit_ins;

	public void update() {
		determineDestinationRegister();
		fetch();
		updateDimux();
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 857
	 * 
	 * wire [7:0] DIMUX; assign DIMUX = ~RDY ? DIHOLD : DI;
	 */
	private void updateDimux() {
		// copy DI (Data In) into DIMUX register
		//
		// DIMUX sits in front of the ALU B Input parameter
		registerFile.setRegisterValue(Register.DIMUX, registerFile.getRegisterValue(Register.DI));
	}

	private void fetch() {
		if (stateMachine.getState() != State.FETCH) {
			return;
		}

		// load the value from memory that PC points to into DI (DI = DataIn, Interface
		// to the memory)
		// input [7:0] DI; // data in, read bus

		// fetch data from memory
		int value = memory.getByte(registerFile.getRegisterValue(Register.PC));

		// write data into the DI (Data In) register
		registerFile.setRegisterValue(Register.DI, value);
	}

	/**
	 * <pre>
	 * always @(posedge clk)
	 * if( state == DECODE && RDY )
	 *    casex( IR )
	 *            8'b1110_1000,   // INX
	 *            8'b1100_1010,   // DEX
	 *            8'b101x_xx10:   // LDX, TAX, TSX
	 *                            dst_reg <= SEL_X;
	 * 
	 *            8'b0x00_1000,   // PHP, PHA
	 *            8'b1001_1010:   // TXS
	 *                            dst_reg <= SEL_S;
	 * 
	 *            8'b1x00_1000,   // DEY, DEX
	 *            8'b101x_x100,   // LDY
	 *            8'b1010_x000:   // LDY #imm, TAY
	 *                            dst_reg <= SEL_Y;
	 * 
	 *            default:        dst_reg <= SEL_A;
	 *    endcase
	 * </pre>
	 */
	private void determineDestinationRegister() {

		if (stateMachine.getState() != State.DECODE) {
			return;
		}

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		if (BitUtil.matchesBitPattern("11101000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("11001010", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("101xxx10", instructionRegisterValue)) {
			// dst_reg <= SEL_X
			destinationRegister = Register.X;
		} else if (BitUtil.matchesBitPattern("0x001000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("10011010", instructionRegisterValue)) {
			// dst_reg <= SEL_S;
			destinationRegister = Register.S;
		} else if (BitUtil.matchesBitPattern("1x001000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("101xx100", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("1010x000", instructionRegisterValue)) {
			// dst_reg <= SEL_Y;
			destinationRegister = Register.Y;
		} else {
			// dst_reg <= SEL_A;
			destinationRegister = Register.A;
		}
	}

	/**
	 * cref: https://github.com/Arlet/verilog-6502/blob/master/cpu.v - l.320
	 * Determine wether we need PC_temp, or PC_temp + 1
	 */
	public void updatePCIncrement() {
		switch (stateMachine.getState()) {

		case DECODE:
//			if( (~I & IRQ) | NMI_edge )
//                PC_inc = 0;
//            else
//                PC_inc = 1;
			pcIncrement = 1;
			break;

		case FETCH:
			pcIncrement = 1;
			break;

		default:
			pcIncrement = 0;
			break;
		}
	}

	public void updatePC() {
		int newPC = registerFile.getRegisterValue(Register.PC) + pcIncrement;
		registerFile.setRegisterValue(Register.PC, newPC);

	}

	/**
	 * ??? where is this from? maybe 628
	 */
	public void computeALUAInput() {

		switch (stateMachine.getState()) {

		case FETCH:
			if (load_only == 1) {
				alu.setInputA(0);
			} else {
				alu.setInputA(registerFile.getRegisterValue(Register.DIMUX));
			}
			break;

		case DECODE:
			// TODO: this seems odd, why does it work?
			if (load_only == 0) {
				alu.setInputA(registerFile.getRegisterValue(destinationRegister));
			}
			break;

		case REG:
			alu.setInputA(registerFile.getRegisterValue(Register.DIMUX));
			break;

		default:
			alu.setInputA(0);
			break;
		}

	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 670
	 * 
	 * <pre>
	 * case( state )
	 *     BRA1,
	 *     RTS1,
	 *     RTI0,
	 *     RTI1,
	 *     RTI2,
	 *     INDX1,
	 *     READ,
	 *     REG,
	 *     JSR0,
	 *     JSR1,
	 *     JSR2,
	 *     BRK0,
	 *     BRK1,
	 *     BRK2,
	 *     PUSH0, 
	 *     PUSH1,
	 *     PULL0,
	 *     RTS0:  BI = 8'h00;
	 *    
	 *     BRA0:  BI = PCL;
	 * 
	 *     DECODE,
	 *     ABS1:  BI = 8'hxx;
	 * 
	 *     default:       BI = DIMUX;
	 *  endcase
	 * </pre>
	 */
	public void computeALUBInput() {
		switch (stateMachine.getState()) {

		case REG:
			alu.setInputB(0);
			break;

		case DECODE:
			break;

		default:
			alu.setInputB(registerFile.getRegisterValue(Register.DIMUX));
			break;
		}
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 1140
	 */
	public void computeALUOp() {

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		ALU_OP op = ALU_OP.UNKNOWN;

		if (BitUtil.matchesBitPattern("00xxxx10", instructionRegisterValue)) {
			// ROL
			op = ALU_OP.UNKNOWN;
		} else if (BitUtil.matchesBitPattern("0010x100", instructionRegisterValue)) {
			// AND
			op = ALU_OP.AND;
		} else if (BitUtil.matchesBitPattern("01xxxx10", instructionRegisterValue)) {
			// A
			op = ALU_OP.UNKNOWN;
		} else if (BitUtil.matchesBitPattern("10001000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("11001010", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("110xx110", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("11xxxx01", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("11x00x00", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("11x01100", instructionRegisterValue)) {
			// SUB
			op = ALU_OP.SUB;
		} else if (BitUtil.matchesBitPattern("010xxx01", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("00xxxx01", instructionRegisterValue)) {
			// OR
			op = ALU_OP.OR;
		} else {
			// ADD
			op = ALU_OP.ADD;
		}

		registerFile.setRegisterValue(Register.OP, ALU_OP.toInt(op));
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 584
	 */
	public void computeALUOperation() {
		switch (stateMachine.getState()) {

		case FETCH:
		case REG:
			alu.setOperation(ALU_OP.fromInt(registerFile.getRegisterValue(Register.OP)));
			break;

		case DECODE:
//			alu.setOperation(ALU_OP.UNKNOWN);
			break;

		default:
//			alu.setOperation(ALU_OP.UNKNOWN);
			break;
		}
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v -- line 465
	 * 
	 */
	public void computeWriteRegister() {
		switch (stateMachine.getState()) {
		case DECODE:
//			registerFile.setRegisterValue(Register.WriteRegister, 1);
//			registerFile.setRegisterValue(Register.WriteRegister, load_reg);
			write_register = load_reg;
			break;

		default:
//			registerFile.setRegisterValue(Register.WriteRegister, 0);
			write_register = 0;
			break;
		}

//		System.out.println("write_register: " + write_register);
	}

	public void storeAdd(int add) {
		// line 531 - register is only every written, when write_register is active
		// write the output
//		if (registerFile.getRegisterValue(Register.WriteRegister) == 1) {
		if (write_register == 1) {

//			System.out.println("Storing add " + FormatUtil.intToHex(add) + " into register " + destinationRegister);
			registerFile.setRegisterValue(destinationRegister, add);
		}
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - 1064
	 */
	public void computeLoadOnly() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}

		load_only = 0;

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		if (BitUtil.matchesBitPattern("101xxxxx", instructionRegisterValue)) {
			load_only = 1;
		}

//		System.out.println("load_only: " + load_only);
	}

	/**
	 * line 972
	 */
	public void computeLoadReg() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}

		load_reg = 0;

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		if (BitUtil.matchesBitPattern("0xx01010", instructionRegisterValue) // ASLA, ROLA, LSRA, RORA
				|| BitUtil.matchesBitPattern("0xxxxx01", instructionRegisterValue) // ORA, AND, EOR, ADC
				|| BitUtil.matchesBitPattern("100x10x0", instructionRegisterValue) // DEY, TYA, TXA, TXS
				|| BitUtil.matchesBitPattern("1010xxx0", instructionRegisterValue) // LDA/LDX/LDY
				|| BitUtil.matchesBitPattern("10111010", instructionRegisterValue) // TSX
				|| BitUtil.matchesBitPattern("1011x1x0", instructionRegisterValue) // LDX/LDY
				|| BitUtil.matchesBitPattern("11001010", instructionRegisterValue) // DEX
				|| BitUtil.matchesBitPattern("1x1xxx01", instructionRegisterValue) // LDA, SBC
				|| BitUtil.matchesBitPattern("xxx01000", instructionRegisterValue)) { // DEY, TAY, INY, INX
			load_reg = 1;
		}
	}

	/**
	 * line 525
	 */
	public void writeRegister(int value) {
		if (write_register != 1) {
			return;
		}

		switch (regsel) {

		case A:
//			System.out.println("writeRegister A value: " + value);
			registerFile.setRegisterValue(Register.A, value);
			break;

		case X:
//			System.out.println("writeRegister X value: " + value);
			registerFile.setRegisterValue(Register.X, value);
			break;

		case Y:
//			System.out.println("writeRegister Y value: " + value);
			registerFile.setRegisterValue(Register.Y, value);
			break;

		case S:
//			System.out.println("writeRegister S value: " + value);
			registerFile.setRegisterValue(Register.S, value);
			break;

		default:
			throw new RuntimeException("Not implemented exception");
		}

	}

	/**
	 * line 535
	 */
	public void computeRegisterSelectLogic() {

		switch (stateMachine.getState()) {

		case DECODE:
			regsel = destinationRegister;
			break;

		default:
			regsel = src_reg;
			break;

		}

	}

	/**
	 * line 1009
	 */
	public void computeSourceReg() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}

//		src_reg = 0; // A
		src_reg = Register.A;

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		if (BitUtil.matchesBitPattern("10111010", instructionRegisterValue)) { // TSX
//			src_reg = 3; // S
			src_reg = Register.S;
		} else if (BitUtil.matchesBitPattern("100xx110", instructionRegisterValue) // STX
				|| BitUtil.matchesBitPattern("100x1x10", instructionRegisterValue) // TXA, TXS
				|| BitUtil.matchesBitPattern("1110xx00", instructionRegisterValue) // INX, CPX
				|| BitUtil.matchesBitPattern("11001010", instructionRegisterValue)) { // DEX
//			src_reg = 1; // X
			src_reg = Register.X;
		} else if (BitUtil.matchesBitPattern("100x_x100", instructionRegisterValue) // STY
				|| BitUtil.matchesBitPattern("10011000", instructionRegisterValue) // TYA
				|| BitUtil.matchesBitPattern("1100xx00", instructionRegisterValue) // CPY
				|| BitUtil.matchesBitPattern("1x001000", instructionRegisterValue)) { // DEY, INY
//			src_reg = 2; // Y
			src_reg = Register.Y;
		}
	}
	
	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 760
	 * <br /><br />
	 * The Z flag is the zero flag
	 */
	public void computeZ() {
		if (stateMachine.getState() == State.DECODE) {
		
			if (plp == 1) {
				throw new RuntimeException("Not implemented yet");
			} else if ( ((load_reg == 1) && (regsel != Register.S)) || (compare == 1) || (bit_ins == 1) ) {
				z = alu.getZeroFlag();
			}
			
		}
	}
	
	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 760
	 * <br /><br />
	 * The N flag is the negative flag
	 */
	public void computeN() {
		if (stateMachine.getState() == State.DECODE) {
			if (plp == 1) {
				throw new RuntimeException("Not implemented yet");
			} else if ( ((load_reg == 1) && (regsel != Register.S)) || (compare == 1) ) {
				n = alu.getNegativeFlag();
			}
		}
	}
	
	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 1175
	 * <br /><br />
	 * Sets flags for special instructions
	 */
	public void computeSpecialInstruction() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}
		
		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);
		
		plp = (instructionRegisterValue == Instructions.PLP) ? 1 : 0;
	}
	
	/**
	 * line 1110
	 */
	public void computeCompare() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}
		
		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);
		
		compare = 0;
		if (BitUtil.matchesBitPattern("11x00x00", instructionRegisterValue) // CPX, CPY (imm/zp)
				|| BitUtil.matchesBitPattern("11x01100", instructionRegisterValue) // CPX, CPY (abs)
				|| BitUtil.matchesBitPattern("110xxx01", instructionRegisterValue)) { // CMP 
			compare = 1;
		}
	}
	
	/**
	 * line 1166
	 */
	public void computeBitIns() {
		if (stateMachine.getState() != State.DECODE) {
			return;
		}
		
		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);
		
		bit_ins = 0;
		if (BitUtil.matchesBitPattern("0010x100", instructionRegisterValue)) { // BIT zp/abs 
			bit_ins = 1;
		}
	}

	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public Register getDestinationRegister() {
		return destinationRegister;
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public void setAlu(ALU alu) {
		this.alu = alu;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

}
