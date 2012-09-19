package representation;

import java.util.HashSet;
import java.util.Set;


public class Quantum {

	private Integer literal;
	private Set<Integer> coordinates;
	
	public Quantum(Quantum quantum){
		this.literal = new Integer(quantum.getLiteral());
		
		Set<Integer> clonedCoordinates = new HashSet<Integer>();
		for(Integer coordinate: quantum.getCoordinates()){
			clonedCoordinates.add(new Integer(coordinate));
		}
		
		this.coordinates = clonedCoordinates;
		
	}
	
	public Quantum(Integer literal){
		this.literal = literal;
		this.coordinates = new HashSet<Integer>();
	}
	
	public void addCoordinate(Integer coordinate) {
		this.coordinates.add(coordinate);
	}

	public Set<Integer> getCoordinates() {
		return this.coordinates;
	}

	public Integer getLiteral() {
		return literal;
	}

	@Override
	public String toString() {
		return "Quantum [literal=" + literal + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quantum other = (Quantum) obj;
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!literal.equals(other.literal))
			return false;
		return true;
	}

}
