package alu;

public enum ALU_OP {

	UNKNOWN, ADD, SUB, AND, OR;

	public static int toInt(ALU_OP aluOp) {

		switch (aluOp) {
		
		case ADD:
			return 1;
			
		case SUB:
			return 2;
			
		case AND:
			return 3;
			
		case OR:
			return 4;

		case UNKNOWN:
		default:
			return -1;
		}

	}

	public static ALU_OP fromInt(int value) {
		switch (value) {
		
		case 1:
			return ADD;
			
		case 2:
			return SUB;
			
		case 3:
			return AND;
			
		case 4:
			return OR;

		case -1:
		default:
			return UNKNOWN;
		}
	}

}
