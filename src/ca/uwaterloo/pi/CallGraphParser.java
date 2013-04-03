package ca.uwaterloo.pi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallGraphParser {
	private final Pattern functionHeader;
	private final Pattern functionCall;
	private RegisterOffice registerOffice;
	private Map<Integer, Set<Integer>> callerToCallee;
	private Map<Integer, Set<Integer>> calleeToCaller;

	public CallGraphParser(RegisterOffice registerOffice) {
		this.functionHeader = Pattern.compile("Call graph node for function: '(.+)'<<(.+)>>  #uses=(\\d+)");
		this.functionCall = Pattern.compile("\\s*CS<(.+)> calls function '(.+)'");
		this.registerOffice = registerOffice;
	}

	public Map<Integer, Set<Integer>> getCallerToCallee() {
		return callerToCallee;
	}

	public Map<Integer, Set<Integer>> getCalleeToCaller() {
		return calleeToCaller;
	}

	public void parseRawCallGraph(List<String> lines) throws Exception {
		Integer callerId = null;
		// A called B
		this.callerToCallee = new HashMap<Integer, Set<Integer>>();
		// B was called by A
		this.calleeToCaller = new HashMap<Integer, Set<Integer>>();
		
		for(String line: lines) {
			Matcher header = this.functionHeader.matcher(line);
			Matcher call = this.functionCall.matcher(line);
			
			if (header.find()) {
				String caller = header.group(1);
				callerId = this.registerOffice.register(caller);
				this.callerToCallee.put(callerId, new HashSet<Integer>());
			} else if (callerId != null && call.find()) {
				String callee = call.group(2);
				Integer calleeId = this.registerOffice.register(callee);
				this.callerToCallee.get(callerId).add(calleeId);
				
				Set<Integer> callers = this.calleeToCaller.get(calleeId);
				if (callers == null) {
					callers = new HashSet<Integer>();
					this.calleeToCaller.put(calleeId, callers);
				}
				callers.add(callerId);
			}
		}
	}

}
