package ca.uwaterloo.pi;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RelationAnalyst {
	private RegisterOffice registerOffice;
	private Float confidenceThreshold;
	private Integer supportThreshold;

	public RelationAnalyst(RegisterOffice registerOffice, Float confidenceThreshold, Integer supportThreshold) {
		this.registerOffice = registerOffice;
		this.confidenceThreshold = confidenceThreshold;
		this.supportThreshold = supportThreshold;
	}
	
	private class FunctionPair {
		public Integer id1;
		public Integer id2;
		
		public FunctionPair(Integer id1, Integer id2) {
			this.id1 = id1;
			this.id2 = id2;
		}
		
		@Override
		public int hashCode() {
			return (id1 << 16) + id2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FunctionPair) {
				FunctionPair pair = (FunctionPair) obj;
				return (pair.id1 == this.id1) && (pair.id2 == this.id2);
			}
			return false;
		}
		
		@Override
		public String toString() {
			String name1 = registerOffice.getName(id1);
			String name2 = registerOffice.getName(id2);
			if(name1.compareTo(name2) < 0) {
				return "(" + name1 + " " + name2 + ")";
			}
			return "(" + name2 + " " + name1 + ")";
		}
	}
	
	private class FunctionStat {
		public Integer supportAB;
		public Float confidence;
		
		public FunctionStat(Integer supportAB, Float confidence) {
			this.supportAB = supportAB;
			this.confidence = confidence;
		}
		
		@Override
		public String toString() {
			NumberFormat numFormat = NumberFormat.getNumberInstance();
			numFormat.setMaximumFractionDigits(2);
			numFormat.setMinimumFractionDigits(2);
			numFormat.setRoundingMode(RoundingMode.HALF_EVEN);
			return "support: " + supportAB + ", confidence: " + numFormat.format(confidence * 100.0) + "%";
		}
	}
	
	public void analysis(Map<Integer, Set<Integer>> callerToCallee, Map<Integer, Set<Integer>> calleeToCaller) throws Exception {
		int i = 0;
		Map<FunctionPair, FunctionStat> stats = new HashMap<FunctionPair, FunctionStat>();
		
		for(Integer calleeId: calleeToCaller.keySet()) {
			Set<Integer> callers = calleeToCaller.get(calleeId);
			Integer supportA = callers.size();
			Integer totalFunction = this.registerOffice.totalRegistration();
			
			// Count times AB appears together
			Integer[] supportABs = new Integer[totalFunction];
			Arrays.fill(supportABs, 0);
			
			for(Integer callerId: callers) {
				Set<Integer> callees = callerToCallee.get(callerId);
				for(Integer callingId: callees) {
					supportABs[callingId]++;
				}
			}
			
			for(i = 0; i < totalFunction; i++) {
				if(i == calleeId) {
					continue;
				}
				
				Integer supportAB = supportABs[i];
				Float confidence = (float)supportAB / (float)supportA;
				if(confidence >= this.confidenceThreshold && supportAB >= this.supportThreshold) {
					FunctionPair pair = new FunctionPair(calleeId, i);
					FunctionStat stat = new FunctionStat(supportAB, confidence);
					stats.put(pair, stat);
				}
			}
		}
		
		for(Integer callerId: callerToCallee.keySet()) {
			String caller = this.registerOffice.getName(callerId);
			Set<Integer> callees = callerToCallee.get(callerId);
			for(FunctionPair pair: stats.keySet()) {
				if(callees == null) {
					throw new Exception("Null Callees");
				} else if(callees.contains(pair.id1) && (!callees.contains(pair.id2))) {
					System.out.println("bug: " + this.registerOffice.getName(pair.id1) + " in " + caller + " pair: " + pair + " " + stats.get(pair));
				}
			}
		}
	}

}
