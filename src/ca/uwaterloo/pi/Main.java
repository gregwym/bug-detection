package ca.uwaterloo.pi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public Main() {
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		// Execute opt to generate call graph
		Process opt = Runtime.getRuntime().exec( "opt -S -print-callgraph " + args[0] );
		BufferedReader optReader = new BufferedReader( new InputStreamReader( opt.getErrorStream() ) );
		int exitcode = opt.waitFor();
		
		if(exitcode != 0) {
			throw new Exception("Cannot generate call graph from opt");
		}
		
		// Read in all lines
		List<String> rawLines = new ArrayList<String>();
		while(optReader.ready()) {
			String line = optReader.readLine();
			rawLines.add(line);
		}
		
		RegisterOffice registerOffice = new RegisterOffice();
		
		// Parse call graph
		CallGraphParser parser = new CallGraphParser(registerOffice);
		parser.parseRawCallGraph(rawLines);
		
		
	}

}
