package util;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Integer> integerRepresentation(byte[] coordinates){
		
		ArrayList<Integer> coordinatesIntegers = new ArrayList<Integer>();
		
		for(int i=coordinates.length-1; i>=0; i--){
			
			for(int j=0;j<8;j++){
				
				byte b = (byte) Math.pow(2, j);
				byte comparison = (byte) (coordinates[i] & b);
				
				if(countOnes(comparison) > 0){
					int pos = (((coordinates.length-1 - i)*8) -1) + (j+1);
					coordinatesIntegers.add(pos);
				}
				
			}
			
		}
		
		return coordinatesIntegers;
		
	}
	
	public static void main(String[] args) {
//		System.out.println(bitRepresentation((byte)255));
//		System.out.println(bitRepresentation(new byte[]{0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}));
	}
	
}
