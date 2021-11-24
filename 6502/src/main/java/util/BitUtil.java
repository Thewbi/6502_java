package util;

public class BitUtil {
	
	private BitUtil() {
		// no instances of this class
	}
	
	public static boolean matchesBitPattern(final String bitPattern, final int value) {
		
		String valueAsBinaryString = Integer.toBinaryString(value);
		
		int maxLength = Math.max(bitPattern.length(), valueAsBinaryString.length());
		
		for (int i = 0; i < maxLength; i++) {
			
			char a = '0';
			if (i < valueAsBinaryString.length()) {
				a = valueAsBinaryString.charAt(valueAsBinaryString.length() - i -1);
			}
			
			
			char b = 'x';
			if (i < bitPattern.length()) {
				b = bitPattern.charAt(bitPattern.length() - i -1);
			}
			if (b == 'x') {
				continue;
			}
			
			if (a != b) {
				return false;
			}
			
		}
		
		return true;
		
	}

}
