package core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DPLLSolver implements Solver {

	@Override
	public boolean solve(List<Clause> clauses) {
		
		//montar tabela de literais
		Map<Integer, LiteralTableEntry> literalTable = new HashMap<Integer, LiteralTableEntry>();
		for(Clause clause: clauses){
			
			//Soh monto esta tabela de literis para propagacao, e no comeco então não há, nunca, cláusulas vazias e nem cláusulas removidas
			List<Integer> literals = clause.getLiterals();
			for(Integer literal: literals){
				
				if(!literalTable.containsKey(literal)) literalTable.put(literal, new LiteralTableEntry());
				LiteralTableEntry literalTableEntry = literalTable.get(literal);
				//fazer for tradicional
				//literalTableEntry.addPositiveClauseIndex()
				
			}
			
		}
		
		return false;
		
	}

}
