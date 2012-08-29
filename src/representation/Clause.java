package representation;
import java.util.ArrayList;
import java.util.List;


public class Clause {

	private List<Integer> literals;
	
	public Clause(List<Integer> literals) {
		this.literals = literals;
	}

	public Clause(Clause clause) {
		
		List<Integer> clonedLiterals = new ArrayList<Integer>(clause.getLiterals().size());
		for(Integer literal: clause.getLiterals()){
			clonedLiterals.add(new Integer(literal));
		}
		
		this.literals = clonedLiterals;
		
	}

	public List<Integer> getLiterals() {
		return literals;
	}

	public boolean containsLiteral(Integer literal) {
		return this.literals.contains(literal);
	}

	public void removeLiteral(Integer complementaryLiteral) {
		this.literals.remove(complementaryLiteral);
	}
	
	public boolean isUnit() {
		return this.literals.size() == 1;
	}
	
	public boolean isEmpty() {
		return this.literals.isEmpty();
	}
	
	@Override
	public String toString() {
		return "Clause [literals=" + literals + "] "+this.hashCode()+"}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((literals == null) ? 0 : literals.hashCode());
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
		Clause other = (Clause) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!literals.equals(other.literals))
			return false;
		return true;
	}

}
