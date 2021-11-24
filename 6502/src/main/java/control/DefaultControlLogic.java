package control;

import registers.DefaultRegisterFile;
import registers.Register;
import statemachine.State;
import statemachine.StateMachine;
import util.BitUtil;

public class DefaultControlLogic implements ControlLogic {

	private StateMachine stateMachine;

	private Register destinationRegister;

	private DefaultRegisterFile defaultRegisterFile;

	public void update() {
		determineDestinationRegister();
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

		int instructionRegisterValue = defaultRegisterFile.getRegisterValue(Register.IR);

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

	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public Register getDestinationRegister() {
		return destinationRegister;
	}

	public void setRegisterFile(DefaultRegisterFile defaultRegisterFile) {
		this.defaultRegisterFile = defaultRegisterFile;
	}

}
