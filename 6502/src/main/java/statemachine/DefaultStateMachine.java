package statemachine;

import registers.Register;
import registers.RegisterFile;
import util.BitUtil;

public class DefaultStateMachine implements StateMachine {

	private State state = State.UNKNOWN;

	private RegisterFile registerFile;

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void update() {
		switch (state) {

		case DECODE:
			state = nextStateAfterDecode();
			break;
			
		case FETCH:
			state = nextStateAfterFetch();
			break;

		default:
			throw new RuntimeException("Not implemented yet!");
		}
	}

	/**
	 * <pre>
	 * FETCH   : state <= DECODE;
	 * </pre>
	 *  
	 * @return
	 */
	private State nextStateAfterFetch() {
		return State.DECODE;
	}

	/**
	 * cref https://github.com/Arlet/verilog-6502/blob/master/cpu.v Microcode state
	 * machine
	 * 
	 * @return
	 */
	private State nextStateAfterDecode() {

		int instructionRegisterValue = registerFile.getRegisterValue(Register.IR);

		if (BitUtil.matchesBitPattern("xxx01001", instructionRegisterValue)) {
			
			// fetch the immediate operand
			return State.FETCH;
		}

		throw new RuntimeException("Not implemented yet!");
	}

	public void setRegisterFile(RegisterFile registerFile) {
		this.registerFile = registerFile;
	}

}
