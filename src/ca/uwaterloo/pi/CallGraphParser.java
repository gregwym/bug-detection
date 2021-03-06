package ca.uwaterloo.pi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallGraphParser {
	private final Pattern functionHeader; // Pattern to match function header
	private final Pattern functionCall; // Pattern to match function call
	private RegisterOffice registerOffice;

	private Map<Integer, Set<Integer>> callerToCallee; // A called B
	private Map<Integer, Set<Integer>> calleeToCaller; // B was called by A

	public CallGraphParser(RegisterOffice registerOffice) {
		this.functionHeader = Pattern.compile("Call graph node for function: '(.+)'<<(.+)>>  #uses=(\\d+)");
		this.functionCall = Pattern.compile("\\s*CS<(.+)> calls function '(.+)'");
		this.registerOffice = registerOffice;
	}

	/**
	 * Must be called after parseRawCallGraph()
	 * 
	 * @return the mapping for all `A called B` relationship
	 */
	public Map<Integer, Set<Integer>> getCallerToCallee() {
		return callerToCallee;
	}

	/**
	 * Must be called after parseRawCallGraph()
	 * 
	 * @return the mapping for all `B was called by A` relationship
	 */
	public Map<Integer, Set<Integer>> getCalleeToCaller() {
		return calleeToCaller;
	}

	/**
	 * Parse the raw call graph file into two call relationship mapping. To get
	 * the relationship mapping, call the getters.
	 * 
	 * @param rawCallGraph
	 * @throws Exception
	 */
	public void parseRawCallGraph(File rawCallGraph) throws Exception {
		// Open a BufferedReader for the given call graph file
		BufferedReader optReader = new BufferedReader(new InputStreamReader(new FileInputStream(rawCallGraph), "utf-8"));
		
		try {
			// Initialize mappings
			int callerId = -1; // Current scope's caller id
			this.callerToCallee = new HashMap<Integer, Set<Integer>>();
			this.calleeToCaller = new HashMap<Integer, Set<Integer>>();
	
			// Read through each line and construct the mapping
			String line = null;
			while ((line = optReader.readLine()) != null) {
				// Match the line with header and call
				Matcher header = this.functionHeader.matcher(line);
				Matcher call = this.functionCall.matcher(line);
	
				if (header.find()) {
					// If it matches with header pattern, register the caller name
					// and set current scope caller id
					String caller = header.group(1);
					callerId = this.registerOffice.register(caller);
					this.callerToCallee.put(callerId, new HashSet<Integer>());
				} else if (callerId != -1 && call.find()) {
					// If it matches with call pattern, register the callee name and
					// save to both mapping
					String callee = call.group(2);
					int calleeId = this.registerOffice.register(callee);
					this.callerToCallee.get(callerId).add(calleeId);
	
					Set<Integer> callers = this.calleeToCaller.get(calleeId);
					if (callers == null) {
						callers = new HashSet<Integer>();
						this.calleeToCaller.put(calleeId, callers);
					}
					callers.add(callerId);
				}
			}
		} finally {
			// Close the reader
			optReader.close();
		}
	}

}
