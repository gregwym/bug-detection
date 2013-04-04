package ca.uwaterloo.pi;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RelationAnalyst {
	private RegisterOffice registerOffice;
	private Float confidenceThreshold;
	private Integer supportThreshold;
	private Integer depth;

	public RelationAnalyst(RegisterOffice registerOffice, Float confidenceThreshold, Integer supportThreshold, Integer depth) {
		this.registerOffice = registerOffice;
		this.confidenceThreshold = confidenceThreshold;
		this.supportThreshold = supportThreshold;
		this.depth = depth;
	}

	/**
	 * Analysis the given calling relationship mappings. The result will be
	 * output to stdout.
	 * 
	 * @param callerToCallee
	 * @param calleeToCaller
	 * @throws Exception
	 */
	public void analysis(Map<Integer, Set<Integer>> callerToCallee, Map<Integer, Set<Integer>> calleeToCaller) throws Exception {
		int i = 0, depth = 0;
		Map<FunctionPair, FunctionStat> stats = new HashMap<FunctionPair, FunctionStat>();
		
		if (this.depth > 0) {
			Map<Integer, Set<Integer>> newMapping = new HashMap<Integer, Set<Integer>>();
			for (Integer caller: callerToCallee.keySet()) {
				Set<Integer> callees = new HashSet<Integer>(callerToCallee.get(caller));
				newMapping.put(caller, callees);
				
				for(depth = this.depth; depth > 0; depth--) {
					for(Integer callee: new HashSet<Integer>(callees)) {
						callees.addAll(callerToCallee.get(callee));
					}
				}
			}
			callerToCallee = newMapping;
		}

		for (Integer calleeId : calleeToCaller.keySet()) {
			Set<Integer> callers = calleeToCaller.get(calleeId);
			Integer supportA = callers.size();
			Integer totalFunction = this.registerOffice.totalRegistration();

			// Count times AB appears together
			int[] supportABs = new int[totalFunction];

			for (Integer callerId : callers) {
				Set<Integer> callees = callerToCallee.get(callerId);
				for (Integer callingId : callees) {
					supportABs[callingId]++;
				}
			}

			for (i = 0; i < totalFunction; i++) {
				if (i == calleeId) {
					continue;
				}

				Integer supportAB = supportABs[i];
				Float confidence = (float) supportAB / (float) supportA;
				if (confidence >= this.confidenceThreshold && supportAB >= this.supportThreshold) {
					FunctionPair pair = new FunctionPair(calleeId, i);
					FunctionStat stat = new FunctionStat(supportAB, confidence);
					stats.put(pair, stat);
				}
			}
		}

		for (Integer callerId : callerToCallee.keySet()) {
			String caller = this.registerOffice.getName(callerId);
			Set<Integer> callees = callerToCallee.get(callerId);
			for (FunctionPair pair : stats.keySet()) {
				if (callees == null) {
					throw new Exception("Null Callees");
				} else if (callees.contains(pair.id1) && (!callees.contains(pair.id2))) {
					System.out.println("bug: " + this.registerOffice.getName(pair.id1) + " in " + caller + " pair: " + pair + " " + stats.get(pair));
				}
			}
		}
	}

	/**
	 * The representation of a function pair. The functions are identified by
	 * their ID. The order of IDs does matter.
	 * 
	 * @author Greg Wang
	 */
	private class FunctionPair {
		public Integer id1;
		public Integer id2;

		public FunctionPair(Integer id1, Integer id2) {
			this.id1 = id1;
			this.id2 = id2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return (id1 << 16) + id2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FunctionPair) {
				FunctionPair pair = (FunctionPair) obj;
				return (pair.id1 == this.id1) && (pair.id2 == this.id2);
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String name1 = null;
			String name2 = null;
			try {
				name1 = registerOffice.getName(id1);
				name2 = registerOffice.getName(id2);
			} catch (Exception e) {
				// Should never fail
				e.printStackTrace();
				System.exit(-1);
			}

			// Display the pair name in alphabetical order
			if (name1.compareTo(name2) < 0) {
				return "(" + name1 + " " + name2 + ")";
			}
			return "(" + name2 + " " + name1 + ")";
		}
	}

	/**
	 * The representation of a function pairs' statistic.
	 * 
	 * @author Greg Wang
	 */
	private static class FunctionStat {
		public Integer supportAB;
		public Float confidence;
		private static final NumberFormat numFormat;

		static {
			numFormat = NumberFormat.getNumberInstance();
			numFormat.setMaximumFractionDigits(2);
			numFormat.setMinimumFractionDigits(2);
			numFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		}

		public FunctionStat(Integer supportAB, Float confidence) {
			this.supportAB = supportAB;
			this.confidence = confidence;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "support: " + supportAB + ", confidence: " + numFormat.format(confidence * 100.0) + "%";
		}
	}
}
