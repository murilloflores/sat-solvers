package parser;

import java.io.IOException;
import java.util.List;

import representation.Clause;


public interface Parser {

	List<Clause> parse(String filePath) throws IOException;
	
}
