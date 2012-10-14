package util;

public class BitWiseUtils {

	public static String bitRepresentation(byte b){
		
		byte displayMask = 1;
		
		StringBuffer buf = new StringBuffer(8);

		for (int c = 1; c <= 8; c++) {
			buf.append((b & displayMask) == 0 ? '0' : '1');
			b >>= 1;
		}

		return buf.reverse().toString();
		
	}
	
	public static String bitRepresentation(byte[] bytes){
		String rep = "";
		for(byte b: bytes){
			rep = rep+ " " +bitRepresentation(b);
		}
		return rep;
	}
	
	public static int countOnes(byte b){
		
		byte displayMask = 1;
		int counter = 0;

		for (int c = 1; c <= 8; c++) {
			if((b & displayMask) != 0){
				counter++;
			}
			b >>= 1;
		}
		
		return counter;
		
	}
	
	public static int countOnes(byte[] bytes){
		
		int counter = 0;
		for(byte b: bytes){
			counter += countOnes(b);
		}
		return counter;
		
	}
	
	public static void main(String[] args) {
		System.out.println(bitRepresentation(new byte[]{1, -2}));
	}
	
}
