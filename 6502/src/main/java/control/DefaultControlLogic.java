package control;

import alu.ALU;
import alu.ALU_OP;
import memory.Memory;
import registers.Register;
import registers.RegisterFile;
import statemachine.State;
import statemachine.StateMachine;
import util.BitUtil;

public class DefaultControlLogic implements ControlLogic {

	private StateMachine stateMachine;

	private Register destinationRegister;

	private RegisterFile registerFile;
	
	private Memory memory;
	
	private ALU alu;
	
	private int pcIncrement = 0;

	public void update() {
		determineDestinationRegister();
		fetch();
		updateDimux();
	}

	/**
	 * https://github.com/Arlet/verilog-6502/blob/master/cpu.v - line 857
	 * 
	 * wire [7:0] DIMUX;
	 * assign DIMUX = ~RDY ? DIHOLD : DI;
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
		
		// load the value from memory that PC points to into DI (DI = DataIn, Interface to the memory)
		// input [7:0] DI;         // data in, read bus
		
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
			//dst_reg <= SEL_X
			destinationRegister = Register.X;
		} else if (BitUtil.matchesBitPattern("0x001000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("10011010", instructionRegisterValue)
				) {
			//dst_reg <= SEL_S;
			destinationRegister = Register.S;
		} else if (BitUtil.matchesBitPattern("1x001000", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("101xx100", instructionRegisterValue)
				|| BitUtil.matchesBitPattern("1010x000", instructionRegisterValue)) {
			//dst_reg <= SEL_Y;
			destinationRegister = Register.Y;
		} else {
			//dst_reg <= SEL_A;
			destinationRegister = Register.A;
		}
	}
	
	/**
	 * cref: https://github.com/Arlet/verilog-6502/blob/master/cpu.v - l.321
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
				throw new RuntimeException("Not implemented exception");
		}
		
	}
	
	public void updatePC() {
		int newPC = registerFile.getRegisterValue(Register.PC) + pcIncrement;
		registerFile.setRegisterValue(Register.PC, newPC);
		
	}
	
	public void computeALUAInput() {
		
		switch (stateMachine.getState()) {
		
		case FETCH:
			alu.setInputA(registerFile.getRegisterValue(Register.DIMUX));
			break;
		
		case DECODE:
			alu.setInputA(registerFile.getRegisterValue(destinationRegister));
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
			registerFile.setRegisterValue(Register.WriteRegister, 1);
			break;
			
		default:
			registerFile.setRegisterValue(Register.WriteRegister, 0);
			break;
		}
	}
	
	public void storeAdd(int add) {
//		// line 531 - register is only every written, when write_register is active
//		
//		// write the output
		if (registerFile.getRegisterValue(Register.WriteRegister) == 1) {
			
			System.out.println("Storing add " + add + " into register " + destinationRegister);
			registerFile.setRegisterValue(destinationRegister, add);
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

}
