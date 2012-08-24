package parser;

import java.io.IOException;
import java.util.List;

import core.Clause;

public interface Parser {

	List<Clause> parse(String filePath) throws IOException;
	
}
