package registers;

public class DefaultRegisterFile implements RegisterFile {

	// accumulator
	private int a = -1;

	// instruction register, contains the instruction
	private int ir = -1;

	// X register
	private int x = -1;

	// Y register
	private int y = -1;

	// S register (what is that???)
	private int s = -1;

	// program counter
	private int pc = -1;

	// DataIn from memory
	private int di = -1;

	// ???
	private int dimux = -1;
	
	// ALU operation
	private int op = -1;
	
	private int writeRegister = -1;

	public int getRegisterValue(Register register) {

		switch (register) {

		case A:
			return a;

		case IR:
			return ir;

		case X:
			return x;

		case Y:
			return y;

		case S:
			return s;

		case PC:
			return pc;

		case DI:
			return di;

		case DIMUX:
			return dimux;
			
		case OP:
			return op;
			
//		case WriteRegister:
//			return writeRegister;

		default:
			throw new RuntimeException("Not implemented yet!");
		}
	}

	public void setRegisterValue(Register register, int value) {

		switch (register) {

		case A:
			a = value;
			break;

		case IR:
			ir = value;
			break;

		case X:
			x = value;
			break;

		case Y:
			y = value;
			break;

		case S:
			s = value;
			break;

		case PC:
			pc = value;
			break;

		case DI:
			di = value;
			break;

		case DIMUX:
			dimux = value;
			break;
			
		case OP:
			op = value;
			break;
			
//		case WriteRegister:
//			writeRegister = value;
//			break;

		default:
			throw new RuntimeException("Not implemented yet! register: " + register);
		}
	}

	@Override
	public String toString() {
		return "DefaultRegisterFile [a=" + a + ", x=" + x + ", y=" + y + ", s=" + s + ", pc=" + pc + ", ir=" + ir
				+ ", di=" + di + "]";
	}

}
