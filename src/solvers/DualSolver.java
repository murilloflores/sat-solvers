package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.DimacsParser;
import representation.Clause;
import representation.Quantum;

public class DualSolver implements Solver {

	@Override
	public boolean solve(List<Clause> clauses) {
		calculateQuantum(clauses);
		return false;
	}
	
	private List<Quantum> calculateQuantum(List<Clause> clauses){
		
		Map<Integer, List<Integer>> quantumMap = new HashMap<Integer, List<Integer>>();
		
		for(int i=0;i<clauses.size();i++){
			
			Clause clause = clauses.get(i);
			
			List<Integer> literals = clause.getLiterals();
			for(Integer literal:literals){
				
				if(!quantumMap.containsKey(literal)){
					quantumMap.put(literal, new ArrayList<Integer>());
				}
				
				List<Integer> coordinates = quantumMap.get(literal);
				coordinates.add(i);
				
			}
			
		}
		
		System.out.println(quantumMap);
		

		//Trying heuristic to determie the first clause to be used, i.e. the initial state
		
		Map<Integer, Set<Integer>> clausesCoverage = new HashMap<Integer, Set<Integer>>();
		for(int i=0; i< clauses.size(); i++){
			Set<Integer> coverage = new HashSet<Integer>();

			Clause clause = clauses.get(i);
			List<Integer> literals = clause.getLiterals();
			for(Integer literal: literals){
				List<Integer> coordinates = quantumMap.get(literal);
				for(Integer coordinate: coordinates){
					coverage.add(coordinate);
				}
			}
			clausesCoverage.put(i, coverage);
		}
		
		int maxCoverage = clausesCoverage.get(0).size();
		int bestClauseIndex = 0;
		for(int i=0; i< clauses.size(); i++){
			
			if(clausesCoverage.get(i).size() > maxCoverage){
				maxCoverage = clausesCoverage.get(i).size();
				bestClauseIndex = i;
			}
			
		}
		
		System.out.println("the best clause to initiate the search is -> "+clauses.get(bestClauseIndex));
		
		return null;
	}
	
	public static void main(String[] args) throws IOException {
	
		DimacsParser parser = new DimacsParser();
		List<Clause> clauses = parser.parse("examples/dual_example.cnf");
		
		Solver solver =  new DualSolver();
		
		solver.solve(clauses);
		
	}
	
	
}
