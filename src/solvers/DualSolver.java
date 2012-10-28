package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		

		List<Clause> minimalDualClauses = new ArrayList<Clause>();
		List<SearchState> finalStates = calculateFinalStates(theory, false);
		for(SearchState state : finalStates){
			List<Integer> literals = new ArrayList<Integer>();
			Set<Integer> quantums = state.getQuantums();
			for(Integer quantum: quantums){
				literals.add(quantum);
			}
			
			minimalDualClauses.add(new Clause(literals));
		}
		
		return minimalDualClauses;
		
	}
	
	public List<SearchState> calculateFinalStates(Theory theory, boolean returnFirst) {
		
		long loops = 0;
		long begin = System.currentTimeMillis();
		long loopsFirst = 0;
		long timeFirst = 0;
		
		this.coordinatesArraySize = (theory.getNumberOfClauses() + 7) / 8;
		this.theory = theory;
		buildQuantumTable();
		
//		printQuantumTable();
		
		List<SearchState> openedStates = calculateInitialOpenedStates();
		List<SearchState> closedStates = new ArrayList<SearchState>();
		List<SearchState> finalStates = new ArrayList<SearchState>();
		
		while(!openedStates.isEmpty()){
			loops++;
			SearchState currentState = getStateWithSmallestGap(openedStates);

			if(isFinalState(currentState)){
				if(loopsFirst == 0){
					loopsFirst = loops;
					timeFirst = System.currentTimeMillis();
				}
				
				openedStates.remove(currentState);
				finalStates.add(currentState);
				
				if(returnFirst){
					return finalStates;
				}
				
				continue;
			}
			
			openedStates.remove(currentState);
			closedStates.add(currentState);
			
			List<SearchState> neighbors = calculateNeighbors(currentState);
			
			for(SearchState state: neighbors){
				openedStates.add(state);
			}
			
		}
		
		long end = System.currentTimeMillis();
		System.out.print(((timeFirst-begin)/10));
		System.out.print(" | "+(loopsFirst));
		System.out.print(" | "+((end-begin) / 10));
		System.out.print(" | "+(loops));
		
		return finalStates;
	}
	
	private boolean isFinalState(SearchState state) {
		return BitWiseUtils.countOnes(state.getGap()) == 0;
	}
	
	private SearchState getStateWithSmallestGap(List<SearchState> openedStates) {
	
		int minorGapSize = BitWiseUtils.countOnes(openedStates.get(0).getGap());
		int bestStateIndex = 0;
		
		for(int i=0; i<openedStates.size(); i++){
			
			byte[] gap = openedStates.get(i).getGap();
			int gapSize = BitWiseUtils.countOnes(gap);
			
			if(gapSize < minorGapSize){
				minorGapSize = gapSize;
				bestStateIndex = i;
			}
			
		}
		
		return openedStates.get(bestStateIndex);
		
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
		
		int quantumTableIndex = getQuantumTableIndex(literal);
		
		List<Integer> clausesWithQuantum = getClausesWithQuantum(literal);
		for(Integer clause: clausesWithQuantum){

			int pos = (coordinatesArraySize -1) - (clause / 8);
			int exp = clause % 8;
			
			byte b = (byte) Math.pow(2, exp);
			
			quantumTable[quantumTableIndex][pos] = (byte) (quantumTable[quantumTableIndex][pos] | b);
			
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
		
		sortQuantumsAccordingToHeuristic(clauseLiterals, dummyState);
		return clauseLiterals;
	}
	
	private void sortQuantumsAccordingToHeuristic(List<Integer> possibleExtensions, SearchState currentState) {

		byte[] gap = currentState.getGap();
	
		for(int i=0; i<possibleExtensions.size(); i++){
			for(int j=i+1; j<possibleExtensions.size(); j++){
				
				Integer quantumI = possibleExtensions.get(i);
				byte[] coordinatesQuantumI = getCoordinates(quantumI);
				
				Integer quantumJ = possibleExtensions.get(j);
				byte[] coordinatesQuantumJ = getCoordinates(quantumJ);
				
				byte[] interGapQuantumI = byteArrayAnd(coordinatesQuantumI, gap);
				byte[] interGapQuantumJ = byteArrayAnd(coordinatesQuantumJ, gap);
				
				byte[] interGapIJ = byteArrayAnd(interGapQuantumI, interGapQuantumJ);
				
				byte[] interGapIMinusIJ = subtract(interGapQuantumI, interGapIJ);
				int counterInterGapIMinusIJ = BitWiseUtils.countOnes(interGapIMinusIJ);
				
				byte[] inteGapJMinusIJ = subtract(interGapQuantumJ, interGapIJ);
				int counterInterGapJMinusIJ = BitWiseUtils.countOnes(inteGapJMinusIJ);
				
				if(counterInterGapIMinusIJ == counterInterGapJMinusIJ){
					
					Integer mirrorQuantumI = quantumI * -1;
					byte[] mirrorICoordinates = getCoordinates(mirrorQuantumI);
					
					Integer mirrorQuantumJ = quantumJ * -1;
					byte[] mirrorJCoordinates = getCoordinates(mirrorQuantumJ);
					
					byte[] interGapMirrorI = byteArrayAnd(mirrorICoordinates, gap);
					byte[] interGapMirrorJ = byteArrayAnd(mirrorJCoordinates, gap);
					
					byte[] interGapMirrorIJ = byteArrayAnd(interGapMirrorI, interGapMirrorJ);
					
					int counterInterGapMirrorIMinusIJ = BitWiseUtils.countOnes(subtract(interGapMirrorI, interGapMirrorIJ));
					int counterInterGapMirrorJMinusIJ = BitWiseUtils.countOnes(subtract(interGapMirrorJ, interGapMirrorIJ));
					
					if(!(counterInterGapMirrorIMinusIJ > counterInterGapMirrorJMinusIJ)){
						possibleExtensions.set(i, quantumJ);
						possibleExtensions.set(j, quantumI);
					}
					
				} else {
					if(!(counterInterGapIMinusIJ > counterInterGapJMinusIJ)){
						possibleExtensions.set(i, quantumJ);
						possibleExtensions.set(j, quantumI);
					}
				}
				
			}
		}
		
	}

	private byte[] subtract(byte[] b1, byte[] b2) {
		
		byte[] sub = byteArrayAnd(b1, b2);
		sub = byteArrayXor(sub, b1);
		
		return sub;
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
		
		List<SearchState> sucessors = new ArrayList<SearchState>();
		
		//step 1
		List<Integer> possibleExtensions = determinePossibleExtensions(currentState);
		
//		System.out.println("First possible extensions: "+possibleExtensions);
		
		//step 2
		sortQuantumsAccordingToHeuristic(possibleExtensions, currentState);
		
//		String tabs = "";
//		for(int i=1; i<currentState.getQuantums().size(); i++){
//			tabs += "\t";
//		}
//		
//		System.out.print(tabs+"Selected: ");
//		for(Integer quantum: currentState.getQuantums()){
//			System.out.print(quantum + ", ");
//		}
//		System.out.println(tabs+"");
//		
//		System.out.print(tabs+"Gap: ");
//		System.out.println(BitWiseUtils.bitRepresentation(currentState.getGap()));
//		
//		System.out.print(tabs+"Possible extensions: ");
//		for(int i=0; i< possibleExtensions.size(); i++){
//			System.out.print(possibleExtensions.get(i) + ", ");
//		}
//		System.out.println(tabs+"");
//		
//		System.out.print(tabs+"Forbidden quanta: ");
//		for(Integer quantum: currentState.getForbiddenQuantums()){
//			System.out.print(quantum + ", ");
//		}
//		System.out.println(tabs+"");
		
		//step 3
		List<Clause> gapConditions = gapConditions(currentState);
		for(Clause clause: gapConditions){
			if(!intersects(clause, possibleExtensions)){
//				System.out.println(tabs+"------");
				return new ArrayList<SearchState>();
			}
		}
		
		List<Integer> usedQuantums = new ArrayList<Integer>();
		List<Integer> refused = new ArrayList<Integer>();
		
		//step 4
		for(Integer quantum: possibleExtensions){
			
			SearchState possibleNextState = new SearchState(currentState);
			possibleNextState.addQuantum(quantum);
			Integer mirrorQuantum = quantum * -1;
			possibleNextState.addForbiddenQuantum(mirrorQuantum);
			removeFromGapClausesOfQuantum(possibleNextState, quantum);
			
			if(isExclusiveCoordinateCompatible(currentState, quantum) 
					&& gapConditionsAreSatisfied(gapConditions(possibleNextState), currentState)
					){
				
				for(Integer forbiddenQuantum:usedQuantums){
					possibleNextState.addForbiddenQuantum(forbiddenQuantum);
				}			
				
				usedQuantums.add(quantum);
				
				sucessors.add(possibleNextState);
				
			}else{
				refused.add(quantum);
			}
			
		}
		
		for(SearchState sucessor: sucessors){
			for(Integer quantum: refused){
				sucessor.addForbiddenQuantum(quantum);
			}
		}
		
//		System.out.print(tabs+"used: ");
//		for(Integer quantum: usedQuantums){
//			System.out.print(quantum + ", ");
//		}
//		System.out.println(tabs+"");
//		
//		System.out.print(tabs+"refused: ");
//		for(Integer quantum: refused){
//			System.out.print(quantum + ", ");
//		}
//		System.out.println(tabs+"");
		
		List<SearchState> sucessorsWithFuture = new ArrayList<SearchState>();
		for(SearchState sucessor: sucessors){
			List<Clause> gapConditionsSucessor = gapConditions(sucessor);
			if(haveFuture(gapConditionsSucessor, sucessor)){
				sucessorsWithFuture.add(sucessor);
			} else {
//				System.out.print(tabs+"future less: ");
//				
//				for (Integer quantumFerrado : sucessor.getQuantums()) {
//					System.out.print(quantumFerrado + ", ");
//				}
//				System.out.println(tabs+"");
			} 
		}
		
//		System.out.println(tabs+"-------");
		return sucessorsWithFuture;
	}

	
	private void removeFromGapClausesOfQuantum(SearchState possibleNextState, Integer quantum) {
		
		byte[] gap = possibleNextState.getGap();
		byte[] coordinates = getCoordinates(quantum);
		
		byte[] newGap = subtract(gap, coordinates);
		possibleNextState.setGap(newGap);
		
	}
	
	private List<Clause> gapConditions(SearchState state) {

		List<Clause> gapConditions = new ArrayList<Clause>();

//		Set<Integer> mirrorQuantums = calculateMirror(state.getQuantums());
		Set<Integer> mirrorQuantums = state.getForbiddenQuantums();
		List<Integer> clausesInGap = getClausesFromGap(state.getGap());
		
		for (Integer clause : clausesInGap) {
//			if (!intersects(clause, mirrorQuantums))
//				continue;

			Clause clone = new Clause(theory.getClauses().get(clause));
			removeLiteralsOfQuantumsFromClause(mirrorQuantums, clone);
			gapConditions.add(clone);

		}

		return gapConditions;
	}
	
	private void removeLiteralsOfQuantumsFromClause(Set<Integer> mirrorQuantumLiterals, Clause clause) {
		for (Integer literal : mirrorQuantumLiterals) {
			clause.removeLiteral(literal);
		}
	}
	
	
	//FIXME this function and the function above indicates that we can represent clauses as byte[] also
	
	private boolean intersects(Integer clause, Collection<Integer> quantums) {

		int pos = (coordinatesArraySize -1) - (clause / 8);
		int exp = clause % 8;
		
		byte b = (byte) Math.pow(2, exp);
		
		for(Integer quantum: quantums){
			
			byte[] quantumCoordinates = getCoordinates(quantum);
			byte intersec = (byte) (quantumCoordinates[pos] & b);
			if(BitWiseUtils.countOnes(intersec) > 0) return true;
			
		}

		return false;
	}
	
	private boolean intersects(Clause clause, List<Integer> possibleExtensions) {
		
		for(Integer literal: clause.getLiterals()){
			if(possibleExtensions.contains(literal)) return true;
		}
		return false;
	}

	
	private List<Integer> getClausesFromGap(byte[] gap) {
		
		List<Clause> clauses = theory.getClauses();
		List<Integer> clausesInGap = new ArrayList<Integer>();
		
		for(int i=0; i<clauses.size(); i++){
			
//			int pos = i / 8;
//			int piece = coordinatesArraySize - (pos +1);
//
//			byte b = (byte) ((int)(Math.pow(2, i)) >>> (pos*8));
			
			int pos = (coordinatesArraySize -1) - (i / 8);
			int exp = i % 8;
			
			byte b = (byte) Math.pow(2, exp);
			
			byte b2 = (byte) (gap[pos] & b);
			
			if(b2 == b){
				clausesInGap.add(i);
			}
			
		}
			
		return clausesInGap;
		
	}

	private Set<Integer> calculateMirror(Set<Integer> quantums) {

		Set<Integer> mirror = new HashSet<Integer>();
		for (Integer quantum : quantums) {
			mirror.add(quantum*-1);
		}

		return mirror;

	}
	
	private List<Integer> determinePossibleExtensions(SearchState currentState) {
		
		List<Integer> possibleExtensions = getLiteralsFromGapOf(currentState);
		possibleExtensions.removeAll(currentState.getForbiddenQuantums());
		return possibleExtensions;
		
	}
	
	private List<Integer> getLiteralsFromGapOf(SearchState currentState) {
		
		byte[] gap = currentState.getGap();
		
		List<Integer> literalsInTheClausesOfGap = new ArrayList<Integer>();
		for(int i=theory.getNumberOfVariables()*-1; i<= theory.getNumberOfVariables(); i++){
			if(i==0) continue;
			
			byte[] coordinates = getCoordinates(i);
			byte[] intersection = byteArrayAnd(gap, coordinates);
			if(BitWiseUtils.countOnes(intersection) > 0){
				literalsInTheClausesOfGap.add(i);
			}
		
		}
		
		return literalsInTheClausesOfGap;
	}
	
	private boolean haveFuture(List<Clause> nextStateGapConditions, SearchState state){
		
		for(Clause clause: nextStateGapConditions){
			if(state.getForbiddenQuantums().containsAll(clause.getLiterals())) return false;
		}
		
		return true;
	}
	
	
	private boolean gapConditionsAreSatisfied(List<Clause> nextStateGapConditions , SearchState currentState){
		
		Set<Integer> forbiddenQuantums = currentState.getForbiddenQuantums();
		
		for(Clause clause: nextStateGapConditions){
			
			if(clause.isEmpty()) return false; // Old isNewRestrictionsContradictory function
			
			if(forbiddenQuantums.containsAll(clause.getLiterals())){ // Old isNewRestrictionsCompatibleWithFobiddenList function
				return false;
			}

			if(clause.isUnit()){
				for(Clause otherClause: nextStateGapConditions){
					if(otherClause.isUnit() && otherClause.getLiterals().get(0).equals(clause.getLiterals().get(0) * -1)){
						return false;
					}
				}
			}
			
		}
		
		return true;
		
	}
		
	private boolean isExclusiveCoordinateCompatible(SearchState currentState, Integer quantumBeingAdded) {
		
		byte[] quantumBeingAddedCoordinates = getCoordinates(quantumBeingAdded);
		
		for(Integer quantum: currentState.getQuantums()){
			
			byte[] exclusiveCoordinatesOfQuantum = getExclusiveCoordinatesFor(currentState, quantum);

			boolean equal = true;
			for(int i=0; i<coordinatesArraySize; i++){
				byte comparison = (byte) (exclusiveCoordinatesOfQuantum[i] & quantumBeingAddedCoordinates[i]);
				if (!(comparison == exclusiveCoordinatesOfQuantum[i])){
					equal = false;
					break;
				}
			}
			
			if(equal) return false;
			
		}
		
		return true;
	}


	private byte[] getExclusiveCoordinatesFor(SearchState currentState, Integer quantum) {
		
		byte[] allCoordinates = new byte[coordinatesArraySize];
		for(Integer currentStateQuantum: currentState.getQuantums()){
			if(!currentStateQuantum.equals(quantum)){
				allCoordinates = byteArrayOr(allCoordinates, getCoordinates(currentStateQuantum));
			}
				
		}
		
		byte[] exclusiveCoordinates = byteArrayXor(getCoordinates(quantum), allCoordinates);
		exclusiveCoordinates = byteArrayAnd(exclusiveCoordinates, getCoordinates(quantum));
		
		return exclusiveCoordinates;
	}

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
	
	private byte[] byteArrayAnd(byte[] b1, byte[] b2) {
		
		byte[] result  = new byte[coordinatesArraySize];
		for(int i=0; i<coordinatesArraySize; i++){
			result[i] = (byte) (b1[i] & b2[i]);
		}
		return result;
	}
	
	private byte[] completeGap() {
		
		byte[] gap = new byte[coordinatesArraySize];
		int pos = theory.getClauses().size() / 8;
		int exp = theory.getClauses().size() % 8;
		
		// fill begin with zeros
		for(int i=0; i<pos; i++){
			gap[coordinatesArraySize-1-i] = (byte) 255;
		}
		
		//fill last one
		for(int i=0; i<exp; i++){
			
			byte b = (byte) Math.pow(2, i);
			gap[coordinatesArraySize-1-pos] = (byte) (gap[coordinatesArraySize-1-pos] | b); 
			
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

		String[] theories = new String[] {"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0110.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0111.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0112.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0113.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0114.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0115.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0116.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0117.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0118.cnf",
											"/home/murillo/Dropbox/tcc/satlib/uf20-91/uf20-0119.cnf"};

//		String[] theories = new String[] {"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0110.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0111.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0112.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0113.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0114.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0115.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0116.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0117.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0118.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf50-218/uf50-0119.cnf"};
		
//		String[] theories = new String[] {	"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-010.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-011.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-012.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-013.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-014.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-015.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-016.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-017.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-018.cnf",
//				 							"/home/murillo/Dropbox/tcc/satlib/uf75-325/uf75-019.cnf"};
		
		
//		String[] theories = new String[] {	"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0110.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0111.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0112.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0113.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0114.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0115.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0116.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0117.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0118.cnf",
//											"/home/murillo/Dropbox/tcc/satlib/uf100-430/uf100-0119.cnf"};

		DualSolver solver =  new DualSolver();

		for(String t: theories){
			
			System.out.print(t.substring(41)+"| ");
			
			Theory theory = parser.parse(t);
			List<Clause> minimalDualClauses = solver.toMinimalDualClauses(theory);
			System.out.println(" | "+minimalDualClauses.size());
		}
		
	}

}
