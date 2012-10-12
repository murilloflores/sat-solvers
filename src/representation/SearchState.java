package representation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchState {

	private Set<Quantum> quantums;
	private Set<Quantum> forbiddenQuantums;
	private List<Clause> gap;
	
	public SearchState(SearchState searchState){
		
		Set<Quantum> clonedQuantums = new HashSet<Quantum>();
		for(Quantum quantum:searchState.getQuantums()){
			clonedQuantums.add(quantum);
		}
		
		this.quantums = clonedQuantums;
		
		Set<Quantum> clonedForbiddenQuantums = new HashSet<Quantum>();
		for(Quantum quantum: searchState.getForbiddenQuantums()){
			clonedForbiddenQuantums.add(quantum);
		}
		
		this.forbiddenQuantums = clonedForbiddenQuantums;
		
		List<Clause> clonedGap = new ArrayList<Clause>();
		for(Clause clause: searchState.getGap()){
			clonedGap.add(clause);
		}
		
		this.gap = clonedGap;

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

	public List<Clause> getGap() {
		return gap;
	}

	public void setGap(List<Clause> gap) {
		this.gap = gap;
	}

	@Override
	public String toString() {
		return "SearchState [quantums=" + quantums  + "]";
	}

	public void addForbiddenQuantum(Quantum quantum) {
		this.forbiddenQuantums.add(quantum);
	}

	public Set<Quantum> getForbiddenQuantums() {
		return forbiddenQuantums;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (quantums == null) {
			if (other.quantums != null)
				return false;
		} else if (!quantums.equals(other.quantums))
			return false;
		return true;
	}

}
