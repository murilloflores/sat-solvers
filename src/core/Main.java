package core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;


public class Main {
	
	public static void main(String[] args) {
		
//		List<Integer> gap = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 32, 34, 35, 36, 37, 38, 39, 40, 42, 43, 44, 45, 47, 49, 50, 52, 53, 54, 55, 56, 57, 58, 59, 61, 62, 63, 64, 65, 68, 69, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90);
//		List<Integer> fourCoordinates = Arrays.asList(10,12,18,22,43,45,60,64,66);
//		List<Integer> minusSixteenCoordinates = Arrays.asList(1,18,36,37,44,62);
//		
//		Collection fourInterGAp = CollectionUtils.intersection(gap, fourCoordinates);
//		Collection minusSixteenInterGap = CollectionUtils.intersection(gap, minusSixteenCoordinates);
//
//		Collection intersection = CollectionUtils.intersection(fourInterGAp, minusSixteenInterGap);
//				
//		System.out.println(CollectionUtils.subtract(fourInterGAp, intersection).size());
//		System.out.println(CollectionUtils.subtract(minusSixteenInterGap, intersection).size());
		
//		List<Integer> quantums = Arrays.asList(1, 4, 5, 6, 9, 11, 13, -20, 15, 17, 16, -12, -10, -8, -2);
//		Collections.sort(quantums);
//		System.out.println(quantums);

		
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		map.put(-20, Arrays.asList(14,19,23,24,28,39,41,48,55,85));
		map.put(-12, Arrays.asList(20,30,31,47,55,64,79,85,89));
		map.put(-10, Arrays.asList(2,8,9,23,43,49,77,78,82));
		map.put(-8, Arrays.asList(10,21,36,48,49,68,78,80,83));
		map.put(-2, Arrays.asList(3,14,18,38,63,65,70,76,83));
		map.put(1, Arrays.asList(1,6,16,17,20,32,38,46,47,51,57,72,75,79,90));
		map.put(4, Arrays.asList(6,9,21,35,40,42,50,72,88));
		map.put(5, Arrays.asList(4,13,37,44,54,56,67));
		map.put(6, Arrays.asList(11,29,46,57,84));
		map.put(9, Arrays.asList(2,5,35,37,53,54,71,86,87));
		map.put(11, Arrays.asList(4,7,8,31,39,45,58,68,88));
		map.put(13, Arrays.asList(7,10,15,25,26,27,61,69,73,87));
		map.put(15, Arrays.asList(7,12,16,31,33,41,46,48,51,60,66,67,70));
		map.put(16, Arrays.asList(1,18,36,37,44,62));
		map.put(17, Arrays.asList(0,22,69,90));
		
		for(Map.Entry<Integer, List<Integer>> entry: map.entrySet()){
			
			Integer quantum = entry.getKey();
			List<Integer> coordinates = entry.getValue();
			
			Set<Integer> allCoordinates = getAllCoordinates(map, quantum);
			
			Collection exclusive = CollectionUtils.subtract(coordinates, allCoordinates);
			System.out.println(quantum+"->"+exclusive);
			
		}
			
		
	}

	private static Set<Integer> getAllCoordinates(Map<Integer, List<Integer>> map, Integer quantum) {
		
		Set<Integer> coordinates = new HashSet<Integer>();
		
		for(Map.Entry<Integer, List<Integer>> entry: map.entrySet()){
			
			if(!quantum.equals(entry.getKey())){
				coordinates.addAll(entry.getValue());
			}
			
		}
		
		return coordinates;
		
	}
	
}
