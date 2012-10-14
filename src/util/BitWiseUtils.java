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

		for(int i=0;i<8;i++){
			byte b = (byte) (Math.pow(2, i));
			System.out.println(Math.pow(2, i)+ ": "+ bitRepresentation(b) + " - " + countOnes(b));
		}
		
		System.out.println(bitRepresentation((byte) 255)  + " - " + countOnes((byte) 255));
		System.out.println(bitRepresentation((byte) 27)  + " - " + countOnes((byte) 27));
		
		byte[] bytes = new byte[]{27, (byte) 128};
		System.out.println(countOnes(bytes));
		
		byte b = (byte) ((int)(Math.pow(2, 15)) >>> 8);
		System.out.println(bitRepresentation(b));
		
	}
	
}
