package parser;

import java.io.IOException;

import representation.Theory;


public interface Parser {

	Theory parse(String filePath) throws IOException;
	
}
