package memory;

public class DefaultMemory implements Memory {
	
	int memory[] = new int[100];

	@Override
	public int getByte(int address) {
		return memory[address];
	}
	
	@Override
	public void setByte(int address, int value) {
		memory[address] = value;
	}

}
