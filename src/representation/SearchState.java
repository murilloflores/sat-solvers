package representation;

import java.util.HashSet;
import java.util.Set;

public class SearchState {

	private Set<Quantum> quantums;
	private Set<Quantum> forbiddenQuantums;
	
	public SearchState(SearchState searchState){
		
		Set<Quantum> clonedQuantums = new HashSet<Quantum>();
		for(Quantum quantum:searchState.getQuantums()){
			clonedQuantums.add(new Quantum(quantum));
		}
		
		this.quantums = clonedQuantums;
		
		Set<Quantum> clonedForbiddenQuantums = new HashSet<Quantum>();
		for(Quantum quantum: searchState.getForbiddenQuantums()){
			clonedForbiddenQuantums.add(new Quantum(quantum));
		}
		
		this.forbiddenQuantums = clonedForbiddenQuantums;

	}
	
	public SearchState(){
		this.quantums = new HashSet<Quantum>();
		this.forbiddenQuantums = new HashSet<Quantum>();
	}
	
	public void addQuantum(Quantum quantum) {
		this.quantums.add(quantum);		
	}

	public Set<Quantum> getQuantums() {
		return this.quantums;
	}

	@Override
	public String toString() {
		return "SearchState [quantums=" + quantums  + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((forbiddenQuantums == null) ? 0 : forbiddenQuantums.hashCode());
		result = prime * result
				+ ((quantums == null) ? 0 : quantums.hashCode());
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
		SearchState other = (SearchState) obj;
		if (forbiddenQuantums == null) {
			if (other.forbiddenQuantums != null)
				return false;
		} else if (!forbiddenQuantums.equals(other.forbiddenQuantums))
			return false;
		if (quantums == null) {
			if (other.quantums != null)
				return false;
		} else if (!quantums.equals(other.quantums))
			return false;
		return true;
	}

	public void addForbiddenQuantum(Quantum quantum) {
		this.forbiddenQuantums.add(quantum);
	}

	public Set<Quantum> getForbiddenQuantums() {
		return forbiddenQuantums;
	}

	
	
}
