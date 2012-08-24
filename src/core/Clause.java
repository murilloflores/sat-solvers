package core;
import java.util.List;


public class Clause {

	private List<Integer> literals;
	
	public Clause(List<Integer> literals) {
		this.literals = literals;
	}

	public List<Integer> getLiterals() {
		return literals;
	}

	@Override
	public String toString() {
		return "Clause [literals=" + literals + "]";
	}
	
}
