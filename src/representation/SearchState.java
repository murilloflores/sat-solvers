package representation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchState {

	private Set<Integer> quantums;
	private Set<Integer> forbiddenQuantums;
	private byte[] gap;
	
	public SearchState(SearchState searchState){
		
		Set<Integer> clonedQuantums = new HashSet<Integer>();
		for(Integer quantum:searchState.getQuantums()){
			clonedQuantums.add(quantum);
		}
		
		this.quantums = clonedQuantums;
		
		Set<Integer> clonedForbiddenQuantums = new HashSet<Integer>();
		for(Integer quantum: searchState.getForbiddenQuantums()){
			clonedForbiddenQuantums.add(quantum);
		}
		
		this.forbiddenQuantums = clonedForbiddenQuantums;
		
		byte[] clonedGap = new byte[searchState.getGap().length];
		for(int i=0; i<searchState.getGap().length; i++){
			clonedGap[i] = searchState.getGap()[i];
		}
		
		this.gap = clonedGap;

	}
	
	public SearchState(){
		this.quantums = new HashSet<Integer>();
		this.forbiddenQuantums = new HashSet<Integer>();
	}

	public void addQuantum(Integer quantum){
		this.quantums.add(quantum);
	}
	
	public void addForbiddenQuantum(Integer quantum){
		this.forbiddenQuantums.add(quantum);
	}
	
	public Set<Integer> getQuantums() {
		return quantums;
	}

	public void setQuantums(Set<Integer> quantums) {
		this.quantums = quantums;
	}

	public Set<Integer> getForbiddenQuantums() {
		return forbiddenQuantums;
	}

	public void setForbiddenQuantums(Set<Integer> forbiddenQuantums) {
		this.forbiddenQuantums = forbiddenQuantums;
	}

	public byte[] getGap() {
		return gap;
	}

	public void setGap(byte[] gap) {
		this.gap = gap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((forbiddenQuantums == null) ? 0 : forbiddenQuantums
						.hashCode());
		result = prime * result + Arrays.hashCode(gap);
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
		if (!Arrays.equals(gap, other.gap))
			return false;
		if (quantums == null) {
			if (other.quantums != null)
				return false;
		} else if (!quantums.equals(other.quantums))
			return false;
		return true;
	}
	
	

}
