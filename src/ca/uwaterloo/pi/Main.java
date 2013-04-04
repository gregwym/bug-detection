package ca.uwaterloo.pi;

import java.io.File;

public class Main {

	public Main() {

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Float confidenceThreshold = 0.65f;
		Integer supportThreshold = 3;
		Integer depth = 0;

		if (args.length > 2) {
			supportThreshold = Integer.valueOf(args[1]);
			confidenceThreshold = Integer.valueOf(args[2]) / 100.0f;
		}
		if (args.length > 3) {
			depth = Integer.valueOf(args[3]);
		}

		// Create raw call graph file representation
		File rawCallGraph = new File(args[0]);

		// Construct register office, parser and analyst
		RegisterOffice registerOffice = new RegisterOffice();
		CallGraphParser parser = new CallGraphParser(registerOffice);
		RelationAnalyst analyst = new RelationAnalyst(registerOffice, confidenceThreshold, supportThreshold, depth);

		// Parse call graph
		parser.parseRawCallGraph(rawCallGraph);

		// Analysis function call relations
		analyst.analysis(parser.getCallerToCallee(), parser.getCalleeToCaller());
	}

}
