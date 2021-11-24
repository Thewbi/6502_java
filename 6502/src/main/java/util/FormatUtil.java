package util;

public class FormatUtil {
	
	private FormatUtil() {
		// no instances of this class
	}
	
	public static String intToHex(final int value) {
		return String.format("0x%08X", value) + " (" + value + ")";
	}

}
