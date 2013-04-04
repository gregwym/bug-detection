package ca.uwaterloo.pi;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RelationAnalyst {
	private RegisterOffice registerOffice;
	private float confidenceThreshold;
	private int supportThreshold;
	private int depth;

	public RelationAnalyst(RegisterOffice registerOffice, float confidenceThreshold, int supportThreshold, int depth) {
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

		for (Entry<Integer, Set<Integer>> entry : calleeToCaller.entrySet()) {
			int calleeId = entry.getKey();
			Set<Integer> callers = entry.getValue();
			int supportA = callers.size();
			int totalFunction = this.registerOffice.totalRegistration();

			// Count times AB appears together
			int[] supportABs = new int[totalFunction];

			for (int callerId : callers) {
				Set<Integer> callees = callerToCallee.get(callerId);
				for (int callingId : callees) {
					supportABs[callingId]++;
				}
			}

			for (i = 0; i < totalFunction; i++) {
				if (i == calleeId) {
					continue;
				}

				int supportAB = supportABs[i];
				float confidence = (float) supportAB / (float) supportA;
				if (confidence >= this.confidenceThreshold && supportAB >= this.supportThreshold) {
					FunctionPair pair = new FunctionPair(calleeId, i);
					FunctionStat stat = new FunctionStat(supportAB, confidence);
					stats.put(pair, stat);
				}
			}
		}

		for (Entry<Integer, Set<Integer>> entry : callerToCallee.entrySet()) {
			int callerId = entry.getKey();
			String caller = this.registerOffice.getName(callerId);
			Set<Integer> originalCallees = entry.getValue();
			Set<Integer> callees = new HashSet<Integer>(originalCallees);

			if (this.depth > 0) {
				for (depth = this.depth; depth > 0; depth--) {
					for (int callee : new HashSet<Integer>(callees)) {
						callees.addAll(callerToCallee.get(callee));
					}
				}
			}

			for (Entry<FunctionPair, FunctionStat> statEntry : stats.entrySet()) {
				FunctionPair pair = statEntry.getKey();
				if (originalCallees.contains(pair.id1) && (!callees.contains(pair.id2))) {
					System.out.println("bug: " + this.registerOffice.getName(pair.id1) + " in " + caller + " pair: " + pair + " " + statEntry.getValue());
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
		public int id1;
		public int id2;

		public FunctionPair(int id1, int id2) {
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
				return "";
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
		public int supportAB;
		public float confidence;
		private static final NumberFormat numFormat;

		static {
			numFormat = NumberFormat.getNumberInstance();
			numFormat.setMaximumFractionDigits(2);
			numFormat.setMinimumFractionDigits(2);
			numFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		}

		public FunctionStat(int supportAB, float confidence) {
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
