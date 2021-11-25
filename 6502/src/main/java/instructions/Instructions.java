package instructions;

public interface Instructions {

	/** ORA 0x09 9d 00001001 */
	/* Perform OR-operation between an immediate value and the value stored in the Accumulator and write result back to accumulator */
	static final int ORA_IMMEDIATE = 0x09;
	
	/** LDA 0xa9 169d 10101001 */
	static final int LDA_IMMEDIATE = 0xA9;
	
	/** LDX 0xa2 162d 10100010 */
	static final int LDX_IMMEDIATE = 0xA2;
	
	/** NOP 0xEA 234d 11101010 */
	static final int NOP = 0xEA;
	
	/* PLP (PuLl Processor status)     $28  4 */
	static final int PLP = 0x28;

}
