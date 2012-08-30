package representation;

import java.util.HashMap;
import java.util.Map;

public class QuantumTable {

	private Map<Integer, Quantum> quantumMap;

	public QuantumTable(){
		this.quantumMap = new HashMap<Integer, Quantum>();
	}
	
	public void addCoordinate(Integer literal, int coordinate) {
	
		if(!quantumMap.containsKey(literal)){
			quantumMap.put(literal, new Quantum(literal));
		}
		
		Quantum quantum = quantumMap.get(literal);
		quantum.addCoordinate(coordinate);
		
	}

	public Quantum getQuantum(Integer literal) {
		return this.quantumMap.get(literal);
	}
	
}
