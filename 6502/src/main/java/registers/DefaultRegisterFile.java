package registers;

public class DefaultRegisterFile {
	
	private int a = -1;
	
	private int ir = -1;
	
	public int getRegisterValue(Register register) {
		
		switch (register) {
		
		case A:
			return a;
			
		case IR:
			return ir;
			
		default:
			return -1;
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
			
		default:
			return;
		}
	}

}
