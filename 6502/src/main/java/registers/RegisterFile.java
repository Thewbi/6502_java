package registers;

public interface RegisterFile {

	int getRegisterValue(Register register);

	void setRegisterValue(Register register, int value);

}
