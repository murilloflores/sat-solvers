package representation;

import java.util.List;

public class Theory {

	private List<Clause> clauses;
	private Integer numberOfVariables;
	private Integer numberOfClauses;
	
	public Theory(List<Clause> clauses, Integer numberOfLiterals, Integer numberOfClauses) {
		this.clauses = clauses;
		this.numberOfVariables = numberOfLiterals;
		this.numberOfClauses = numberOfClauses;
	}

	public List<Clause> getClauses() {
		return clauses;
	}

	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}

	public Integer getNumberOfVariables() {
		return numberOfVariables;
	}

	public void setNumberOfVariables(Integer numberOfVariables) {
		this.numberOfVariables = numberOfVariables;
	}

	public Integer getNumberOfClauses() {
		return numberOfClauses;
	}

	public void setNumberOfClauses(Integer numberOfClauses) {
		this.numberOfClauses = numberOfClauses;
	}
	
}
