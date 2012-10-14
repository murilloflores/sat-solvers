package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import parser.DimacsParser;
import representation.Clause;
import representation.SearchState;
import representation.Theory;
import util.BitWiseUtils;

public class DualSolver implements Solver {

	private Theory theory;
	private byte[][] quantumTable;
	private int coordinatesArraySize;
	
	public boolean isSatisfiable(Theory theory){
		List<SearchState> finalStates = calculateFinalStates(theory, true);
		return !finalStates.isEmpty();
	}
	
	@Override
	public List<Clause> toMinimalDualClauses(Theory theory) {
		
		calculateFinalStates(theory, false);
		return new ArrayList<Clause>();
		
	}
	
	public List<SearchState> calculateFinalStates(Theory theory, boolean returnFirst) {
		
		this.coordinatesArraySize = (theory.getNumberOfClauses() + 7) / 8;
		this.theory = theory;
		buildQuantumTable();
		
//		printQuantumTable();
		
		List<SearchState> openedStates = calculateInitialOpenedStates();
		List<SearchState> closedStates = new ArrayList<SearchState>();
		List<SearchState> finalStates = new ArrayList<SearchState>();
		
//		while(!openedStates.isEmpty()){
//			loops++;
//			SearchState currentState = getStateWithSmallestGap(openedStates);
//
//			if(isFinalState(currentState)){
//				if(loopsFirst == 0){
//					loopsFirst = loops;
//					timeFirst = System.currentTimeMillis();
//				}
//				
//				openedStates.remove(currentState);
//				finalStates.add(currentState);
//				if(returnFirst){
//					return finalStates;
//				}
//				continue;
//			}
//			
//			openedStates.remove(currentState);
//			closedStates.add(currentState);
//			
//			List<SearchState> neighbors = calculateNeighbors(currentState, quantumTable);
//			
//			for(SearchState state: neighbors){
//				openedStates.add(state);
//			}
//			
//		}
//		
//		long end = System.currentTimeMillis();
//		System.out.print((timeFirst-begin));
//		System.out.print(" | "+(loopsFirst));
//		System.out.print(" | "+(end-begin));
//		System.out.println(" | "+(loops));
		
		return finalStates;
	}
	
	// Quantum table operations
	
	private byte[] getCoordinates(Integer literal){
		int tableIndex = getQuantumTableIndex(literal);
		return quantumTable[tableIndex];
	}

	private int getQuantumTableIndex(Integer literal) {
		int tableIndex;
		
		if(literal < 0){
			tableIndex = literal + theory.getNumberOfVariables();
		}else{
			tableIndex = literal + (theory.getNumberOfVariables() - 1);
		}
		
		return tableIndex;
	}
	
	private void buildQuantumTable(){
		
		int numberOfVariables = theory.getNumberOfVariables();
		this.quantumTable = new byte[numberOfVariables * 2][coordinatesArraySize];
		
		for(int literal=numberOfVariables; literal>0; literal--){
			fillTableForLiteral(literal);
			fillTableForLiteral(literal * -1);
		}
		
		
	}

	private void fillTableForLiteral(int literal) {
		
		List<Integer> clausesWithQuantum = getClausesWithQuantum(literal);
		for(Integer clause: clausesWithQuantum){

			int pos = clause / 8;
			int piece = coordinatesArraySize - (pos +1);

			byte b = (byte) ((int)(Math.pow(2, clause)) >>> (pos*8));
			
			int quantumTableIndex = getQuantumTableIndex(literal);
			quantumTable[quantumTableIndex][piece] = (byte) (quantumTable[quantumTableIndex][piece] | b);
			
		}
		
	}
	
	private List<Integer> getClausesWithQuantum(int literal) {
		
		List<Clause> clauses = theory.getClauses();
		
		List<Integer> positions = new ArrayList<Integer>();
		
		for(int i=0; i<clauses.size(); i++){
			if(clauses.get(i).getLiterals().contains(literal)){
				positions.add(i);
			}
		}
		
		return positions;
		
	}

	// Initial state calculus
	
	private List<SearchState> calculateInitialOpenedStates() {
		
		Clause clause = getBestCnfClauseToStart();
		List<Integer> clauseLiterals = sortInitialQuantums(clause.getLiterals());
		
		List<SearchState> initialOpenedStates = new ArrayList<SearchState>();
		
		for(int i=0; i<clauseLiterals.size(); i++){
			
			SearchState state = new SearchState();
			state.addQuantum(clauseLiterals.get(i));
			state.addForbiddenQuantum(clauseLiterals.get(i) * -1);
			
			for(int j=i-1; j>=0; j--){
				state.addForbiddenQuantum(clauseLiterals.get(j));
			}
			
			byte[] calculatedGap = calculateGap(state);
			state.setGap(calculatedGap);
			
			initialOpenedStates.add(state);
		}
		
		return initialOpenedStates;
		
	}

	private byte[] calculateGap(SearchState searchState) {

		byte[] gap = completeGap();

		for(Integer quantum: searchState.getQuantums()){
			byte[] quantumCoordinates = getCoordinates(quantum);
			gap = byteArrayXor(gap, quantumCoordinates);
		}
		
		return gap;
	
	}
	
	private List<Integer> sortInitialQuantums(List<Integer> clauseLiterals) {
		SearchState dummyState = new SearchState();
		dummyState.setGap(completeGap());
		
		clauseLiterals = sortQuantumsAccordingToHeuristic(clauseLiterals, dummyState);
		return clauseLiterals;
	}
	
	private List<Integer> sortQuantumsAccordingToHeuristic(List<Integer> quantumsOfClause, SearchState currentState) {
//	
//	List<Clause> gap = currentState.getGap();
//	List<Integer> gapCoordinates = getCoordinates(gap);
//	
//	for(int i=0; i<quantumsOfClause.size(); i++){
//		for(int j=i+1; j<quantumsOfClause.size(); j++){
//			
//			Quantum quantumI = quantumsOfClause.get(i);
//			Quantum quantumJ = quantumsOfClause.get(j);
//			
//			Set<Integer> coordinatesInterGapI = new HashSet<Integer>(quantumI.getCoordinates());
//			coordinatesInterGapI.retainAll(gapCoordinates);
//			
//			Set<Integer> coordinatesInterGapJ = new HashSet<Integer>(quantumJ.getCoordinates());
//			coordinatesInterGapJ.retainAll(gapCoordinates);
//			
//			Set<Integer> coordinatesInterGapIJ = new HashSet<Integer>(coordinatesInterGapI);
//			coordinatesInterGapIJ.retainAll(coordinatesInterGapJ);
//			
//			Collection coordinatesIMinusIJ = CollectionUtils.subtract(coordinatesInterGapI, coordinatesInterGapIJ);
//			Collection coordinatesJMinusIJ = CollectionUtils.subtract(coordinatesInterGapJ, coordinatesInterGapIJ);
//			
//			if(coordinatesIMinusIJ.size() == coordinatesJMinusIJ.size()){
//				
//				Quantum mirrorQuantumI = quantumTable.getQuantum(quantumI.getLiteral() * -1);
//				Quantum mirrorQuantumJ = quantumTable.getQuantum(quantumJ.getLiteral() * -1);
//				
//				Set<Integer> coordinatesInterGapMirrorI  = new HashSet<Integer>();
//				if(mirrorQuantumI != null){
//					coordinatesInterGapMirrorI  = new HashSet<Integer>(mirrorQuantumI.getCoordinates());
//				}
//				coordinatesInterGapMirrorI.retainAll(gapCoordinates);
//				
//				Set<Integer> coordinatesInterGapMirrorJ = new HashSet<Integer>();
//				if(mirrorQuantumJ != null){
//					coordinatesInterGapMirrorJ = new HashSet<Integer>(mirrorQuantumJ.getCoordinates());
//				}
//				coordinatesInterGapMirrorJ.removeAll(gapCoordinates);
//				
//				Set<Integer> coordinatesInterGapMirrorIJ = new HashSet<Integer>(coordinatesInterGapMirrorI);
//				coordinatesInterGapMirrorIJ.removeAll(coordinatesInterGapMirrorJ);
//				
//				Collection coordinatesMirrorIMinusIJ = CollectionUtils.subtract(coordinatesInterGapMirrorI, coordinatesInterGapMirrorIJ);
//				Collection coordinatesMirrorJMinusIJ = CollectionUtils.subtract(coordinatesInterGapMirrorJ, coordinatesInterGapMirrorIJ);
//				
//				if(!(coordinatesMirrorIMinusIJ.size() > coordinatesMirrorJMinusIJ.size())){
//					quantumsOfClause.set(i, quantumJ);
//					quantumsOfClause.set(j, quantumI);
//				}
//				
//				
//			}else{
//				
//				if(!(coordinatesIMinusIJ.size() > coordinatesJMinusIJ.size())){
//					quantumsOfClause.set(i, quantumJ);
//					quantumsOfClause.set(j, quantumI);
//				}
//				
//			}
//			
//			
//		}
//	}
//	
		return quantumsOfClause;
	}
	

	private Clause getBestCnfClauseToStart() {

		//Trying heuristic to determine the first clause to be used, i.e. the initial state (pg 25)
		
		int bestClauseIndex = 0;
		int maxCoverage = 0;
		
		for(int i=0; i<theory.getClauses().size(); i++){
			
			Clause clause = theory.getClauses().get(i);
			
			byte[] allCoordinates = new byte[coordinatesArraySize]; 
			for(Integer literal: clause.getLiterals()){
				byte[] literalCoordinates = getCoordinates(literal);
				allCoordinates = byteArrayOr(allCoordinates, literalCoordinates);
			}
			
			int coverage = BitWiseUtils.countOnes(allCoordinates);
			if(coverage > maxCoverage){
				bestClauseIndex = i; 
				maxCoverage = coverage;
			}
			
		}
		
		return theory.getClauses().get(bestClauseIndex);
		
	}
	
	// Neighbor calculus operatinos
	
	private List<SearchState> calculateNeighbors(SearchState currentState) {
		
		// Sucessors function implemented from pg 25
		
//		List<SearchState> sucessors = new ArrayList<SearchState>();
		
		//step 1
//		List<Quantum> possibleExtensions = determinePossibleExtensions(currentState, quantumTable);
		
		//step 2
//		sortQuantumsAccordingToHeuristic(possibleExtensions, currentState, quantumTable);
		
		//step 3
//		List<Clause> gapConditions = gapConditions(currentState, quantumTable);
//		for(Clause clause: gapConditions){
//			
//			if(!intersects(clause, possibleExtensions)){
//				return new ArrayList<SearchState>();
//			}
//		}
		
//		List<Quantum> usedQuantums = new ArrayList<Quantum>();
//		List<Quantum> refused = new ArrayList<Quantum>();
		//step 4
////		for(Quantum quantum: possibleExtensions){
//			
//			SearchState possibleNextState = new SearchState(currentState);
//			possibleNextState.addQuantum(quantum);
//			Quantum mirrorQuantum = quantumTable.getQuantum(quantum.getLiteral() * -1);
//			if(mirrorQuantum != null){
//				possibleNextState.addForbiddenQuantum(mirrorQuantum);
//			}
//			removeFromGapClausesOfQuantum(possibleNextState, quantum);
//			
//			List<Clause> possibleNextStateGapConditions = gapConditions(possibleNextState, quantumTable);
//			
//			if(gapConditionsAreSatisfied(possibleNextStateGapConditions, currentState)
//					&& isExclusiveCoordinateCompatible(currentState, quantum)){
//				
//				for(Quantum forbiddenQuantum:usedQuantums){
//					possibleNextState.addForbiddenQuantum(forbiddenQuantum);
//				}			
//				
//				usedQuantums.add(quantum);
//				
//				sucessors.add(possibleNextState);
//				
//			}else{
//				refused.add(quantum);
//			}
//			
//		}
//		
//		for(SearchState sucessor: sucessors){
//			for(Quantum quantum: refused){
//				sucessor.addForbiddenQuantum(quantum);
//			}
//		}
		
		
//		List<SearchState> sucessorsWithFuture = new ArrayList<SearchState>();
//		for(SearchState sucessor: sucessors){
//			List<Clause> gapConditionsSucessor = gapConditions(sucessor, quantumTable);
//			if(haveFuture(gapConditionsSucessor, sucessor)){
//				sucessorsWithFuture.add(sucessor);
//			} 
//		}
		
		
		return new ArrayList<SearchState>();
	}

	private boolean haveFuture(List<Clause> nextStateGapConditions, SearchState state){
		
//		for(Clause clause: nextStateGapConditions){
//			if(areAllLiteralsInQuantums(clause.getLiterals(), state.getForbiddenQuantums())) return false;
//		}
		
		return true;
	}
	
	
	private boolean gapConditionsAreSatisfied(List<Clause> nextStateGapConditions , SearchState currentState){
		
//		Set<Quantum> forbiddenQuantums = currentState.getForbiddenQuantums();
//		
//		for(Clause clause: nextStateGapConditions){
//			
//			if(clause.isEmpty()) return false; // Old isNewRestrictionsContradictory function
//			
//			if(areAllLiteralsInQuantums(clause.getLiterals(), forbiddenQuantums)){ // Old isNewRestrictionsCompatibleWithFobiddenList function
//				return false;
//			}
//
//			if(clause.isUnit()){
//				for(Clause otherClause: nextStateGapConditions){
//					if(otherClause.isUnit() && otherClause.getLiterals().get(0).equals(clause.getLiterals().get(0) * -1)){
//						return false;
//					}
//				}
//			}
//			
//		}
		
		return true;
		
	}
	
//	private void removeFromGapClausesOfQuantum(SearchState possibleNextState, Quantum quantum) {
		
//		List<Clause> gap = possibleNextState.getGap();
//		
//		for(Integer coordinate: quantum.getCoordinates()){
//			gap.remove(this.cnfClauses.get(coordinate));
//		}
//		
//		possibleNextState.setGap(gap);
		
		
//	}


//	private boolean areAllLiteralsInQuantums(List<Integer> literals, Set<Quantum> forbiddenQuantums) {
		
//		for(Integer literal:literals){
//			if(!containsLiteral(forbiddenQuantums, literal)){
//				return false;
//			}
//		}
		
//		return true;
//	}

//	private boolean containsLiteral(Set<Quantum> forbiddenQuantums,
//			Integer literal) {
//		for (Quantum quantum : forbiddenQuantums) {
//			if (quantum.getLiteral().equals(literal))
//				return true;
//		}
//		return false;

//	}

//	private boolean isExclusiveCoordinateCompatible(SearchState currentState, Quantum quantumBeingAdded) {
		
//		Set<Integer> quantumBeingAddedCoordinates = quantumBeingAdded.getCoordinates();
//		
//		for(Quantum quantum: currentState.getQuantums()){
//			
//			Set<Integer> coordinates = getExclusiveCoordinatesFor(currentState, quantum);
//			if(quantumBeingAddedCoordinates.containsAll(coordinates)){
//				return false;
//			}
//			
//		}
		
//		return true;
//	}


//	private Set<Integer> getExclusiveCoordinatesFor(SearchState currentState, Quantum quantum) {
		
//		Quantum copy = new Quantum(quantum);
//		Set<Integer> coordinates =  copy.getCoordinates();
//		
//		for(Quantum currentStateQuantum: currentState.getQuantums()){
//			if(currentStateQuantum.equals(quantum)){
//				continue;
//			}
//			coordinates.removeAll(currentStateQuantum.getCoordinates());
//		}
//		
//		return coordinates;
//		retur new HashSet<Integer>();
//	}

//	private List<Clause> gapConditions(SearchState state,QuantumTable quantumTable){
//		
//		List<Clause> gapConditions = new ArrayList<Clause>();
//		
//		Set<Quantum> mirrorQuantums = calculateMirror(state.getQuantums(), quantumTable); 
//		List<Clause> gap = state.getGap();
//		
//		for(Clause clause: gap){
//			if(!intersects(clause, mirrorQuantums)) continue;
//			
//			Clause clone = new Clause(clause);
//			removeLiteralsOfQuantumsFromClause(mirrorQuantums, clone);
//			gapConditions.add(clone);
//			
//		}
//		
//		return gapConditions;
//	}
	
//	private void removeLiteralsOfQuantumsFromClause(Set<Quantum> mirrorQuantums, Clause clause) {
//		for(Quantum quantum: mirrorQuantums){
//			clause.removeLiteral(quantum.getLiteral());
//		}
//	}
//
//	private boolean intersects(Clause clause, Collection<Quantum> quantums) {
//		
//		for(Integer literal: clause.getLiterals()){
//			for(Quantum quantum: quantums){
//				if (quantum.getLiteral().equals(literal)) return true;
//			}
//		}
//		
//		return false;
//	}

//	private Set<Quantum> calculateMirror(Set<Quantum> quantums, QuantumTable quantumTable) {
//		
//		Set<Quantum> mirror = new HashSet<Quantum>();
//		for(Quantum quantum: quantums){
//			Integer mirrorLiteral = quantum.getLiteral() * -1;
//			Quantum mirrorQuantum = quantumTable.getQuantum(mirrorLiteral);
//			if(mirrorQuantum != null){
//				mirror.add(mirrorQuantum);
//			}
//		}
//		
//		return mirror;
//		
//	}

//	private List<Quantum> determinePossibleExtensions(SearchState currentState, QuantumTable quantumTable) {
//		
//		Set<Integer> literalsInTheClausesOfGap = getLiteralsFromGapOf(currentState);
//
//		List<Quantum> possibleExtensions = new ArrayList<Quantum>();
//		for(Integer literal: literalsInTheClausesOfGap){
//			possibleExtensions.add(quantumTable.getQuantum(literal));
//		}
//		
//		removeForbiddenQuantums(possibleExtensions, currentState);
//		
//		return possibleExtensions;
//	}
//
//	private void removeForbiddenQuantums(List<Quantum> possibleExtensions, SearchState currentState) {
//		Set<Quantum> forbiddenQuantums = currentState.getForbiddenQuantums();
//		for(Quantum forbiddenQuantum: forbiddenQuantums){
//			possibleExtensions.remove(forbiddenQuantum);
//		}
//	}
//
//	private Set<Integer> getLiteralsFromGapOf(SearchState currentState) {
//		List<Clause> gap = currentState.getGap();
//
//		Set<Integer> literalsInTheClausesOfGap = new HashSet<Integer>();
//		for(Clause clause: gap){
//			List<Integer> literals = clause.getLiterals();
//			literalsInTheClausesOfGap.addAll(literals);
//		}
//		
//		return literalsInTheClausesOfGap;
//	}
//
//	private boolean isFinalState(SearchState state) {
//		return state.getGap().isEmpty();
//	}
//
//	private SearchState getStateWithSmallestGap(List<SearchState> openedStates) {
//		
//		int betterStateIndex = 0;
//		List<Clause> minorGap = openedStates.get(0).getGap();
//		
//		for(int i=1; i < openedStates.size(); i++){
//			
//			SearchState state = openedStates.get(i);
//			List<Clause> gap = state.getGap();
//			
//			if(gap.size() < minorGap.size()){
//				minorGap = gap;
//				betterStateIndex = i;
//			}
//			
//		}
//		
//		return openedStates.get(betterStateIndex);
//		
//	}

	// ByteArray operations
	
	private byte[] byteArrayOr(byte[] allCoordinates, byte[] literalCoordinates) {
		
		byte[] result  = new byte[coordinatesArraySize];
		for(int i=0; i<coordinatesArraySize; i++){
			result[i] = (byte) (allCoordinates[i] | literalCoordinates[i]);
		}
		return result;
	}
	
	private byte[] byteArrayXor(byte[] b1, byte[] b2) {

		byte[] result  = new byte[coordinatesArraySize];
		for(int i=0; i<coordinatesArraySize; i++){
			result[i] = (byte) (b1[i] ^ b2[i]);
		}
		return result;
	}
	
	private byte[] completeGap() {
		
		int sum = 0;
		for(int i=0; i<this.theory.getClauses().size(); i++){
			sum += (int) Math.pow(2, i);
		}
		
		byte[] gap = new byte[coordinatesArraySize];
		int desloc = 0;
		for(int i=coordinatesArraySize-1 ; i>=0; i--){
			gap[i] = (byte) (sum >> 8*desloc);
			desloc++;
		}
		
		return gap;
	}

	// Debug operations
	
	private void printQuantumTable() {
		
		for(int i=theory.getNumberOfVariables()*-1; i<= theory.getNumberOfVariables(); i++){
			
			if(i==0) continue;
			
			System.out.print(i+" -> ");
			byte[] coordinates = getCoordinates(i);
			
			String representation = "";
			for(int j=coordinatesArraySize-1; j>-1; j--){
				String bits = BitWiseUtils.bitRepresentation(coordinates[j]); 
				representation = bits + " " + representation; 
			}
			
			System.out.print(representation);
			System.out.println("");
			
		}
		
	}
	
	// Main
	
	public static void main(String[] args) throws IOException {
	
		DimacsParser parser = new DimacsParser();
		
		Theory theory = parser.parse("examples/dual_example.cnf");
		
		DualSolver solver =  new DualSolver();
		List<Clause> minimalDualClauses = solver.toMinimalDualClauses(theory);
		System.out.println("Size: "+minimalDualClauses.size());
		System.out.println(minimalDualClauses);

	}

}
