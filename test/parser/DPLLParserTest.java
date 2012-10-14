package parser;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import representation.Theory;
import solvers.DPLLSolver;


public class DPLLParserTest{

	@Test
	public void positiveSimpleTests_shouldReturnSatisfiable() throws IOException{
		runtTest("examples/satisfiable_by_up.cnf", true);
	}
	
	@Test
	public void negativeSimpleTests_shouldReturnSatisfiable() throws IOException{
		runtTest("examples/unsatisfiable_by_up.cnf", false);
	}
	
	@Test
	public void satLibNegativeTest_shouldReturnSatisfiable() throws IOException{
		runtTest("examples/uuf75-024.cnf", false);
	}
	
	@Test
	public void satLibPositiveTest_shouldReturnSatisfiable() throws IOException{
		runtTest("examples/uf75-040.cnf", true);
	}
	
	@Test
	public void minimunSatisfiableTest_shouldReturnSatisfiable() throws IOException{
		runtTest("examples/minimun_satisfiable.cnf", true);
	}
	
	private void runtTest(String filePath, boolean expectedResult) throws IOException{
	
		DimacsParser parser = new DimacsParser();
		Theory theory = parser.parse(filePath);
		
		DPLLSolver solver =  new DPLLSolver();
		
		Assert.assertEquals(expectedResult, solver.isSatisfiable(theory.getClauses(), theory.getNumberOfVariables()));
		
	}
	
}
