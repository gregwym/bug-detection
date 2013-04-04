package ca.uwaterloo.pi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		Float confidenceThreshold = 0.65f;
		Integer supportThreshold = 3;
		if(args.length > 1) {
			supportThreshold = Integer.valueOf(args[1]);
			confidenceThreshold = Integer.valueOf(args[2]) / 100.0f;
		}
		
		// Read call graph from input file
		InputStream inStream = new FileInputStream(args[0]);
		BufferedReader optReader = new BufferedReader( new InputStreamReader( inStream ));
		
		// Read in all lines
		List<String> rawLines = new ArrayList<String>();
		if(!optReader.ready()) {
			optReader.close();
			throw new Exception("optReader not ready");
		}
		while(optReader.ready()) {
			String line = optReader.readLine();
			rawLines.add(line);
		}
		optReader.close();
		
		RegisterOffice registerOffice = new RegisterOffice();
		
		// Parse call graph
		CallGraphParser parser = new CallGraphParser(registerOffice);
		parser.parseRawCallGraph(rawLines);
		
		RelationAnalyst analyst = new RelationAnalyst(registerOffice, confidenceThreshold, supportThreshold);
		analyst.analysis(parser.getCallerToCallee(), parser.getCalleeToCaller());
	}

}
