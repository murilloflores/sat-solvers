package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;


public class Main {
	
	public static void main(String[] args) {
		
		int[] quantums = new int[]{-12,-11,-10,10,12,14};
		
		for(int i=0; i<quantums.length; i++){
			System.out.print(quantums[i]+",");
		}
		
		System.out.println("");
		
		for(int i=0; i<quantums.length; i++){
			for(int j=i+1;j<quantums.length; j++){
				
				int quantumI = quantums[i];
				int quantumJ = quantums[j];
				
				if(isGreaterThan(quantumI, quantumJ)){
					quantums[i] = quantumJ;
					quantums[j] = quantumI;
				}
				
			}
		}
		
		for(int i=0; i<quantums.length; i++){
			System.out.print(quantums[i]+",");
		}
		
	}

	private static boolean isGreaterThan(int quantumI, int quantumJ) {
		
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		map.put(-12, Arrays.asList(3,6));
		map.put(12, Arrays.asList(7));
		map.put(-11, Arrays.asList(6,8));
		map.put(11, new ArrayList<Integer>());
		map.put(-10, Arrays.asList(3));
		map.put(10, Arrays.asList(6,7));
		map.put(-14, new ArrayList<Integer>());
		map.put(14, Arrays.asList(7,8));
		
		List<Integer> listI = map.get(quantumI);
		List<Integer> listJ = map.get(quantumJ);
		List intersection = ListUtils.intersection(listI, listJ);
		
		if(ListUtils.subtract(listI, intersection).size() == ListUtils.subtract(listJ, intersection).size()){
			
			List<Integer> mirrorListI = map.get(quantumI * -1);
			List<Integer> mirrorListJ = map.get(quantumJ * -1);
			List mirrorIntersection = ListUtils.intersection(listI, listJ);
			
			if(ListUtils.subtract(mirrorListI, mirrorIntersection).size() > ListUtils.subtract(mirrorListJ, mirrorIntersection).size()){
				return true;
			}
			
		}
		
		if(ListUtils.subtract(listI, intersection).size() > ListUtils.subtract(listJ, intersection).size()){
			return true;
		}
		
		return false;
		
	}
	
}
	