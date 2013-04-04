package ca.uwaterloo.pi;

import java.util.HashMap;
import java.util.Map;

public class RegisterOffice {
	private Map<String, Integer> registration;
	private Map<Integer, String> reverseMatch;

	public RegisterOffice() {
		this.registration = new HashMap<String, Integer>();
		this.reverseMatch = new HashMap<Integer, String>();
	}
	
	public Integer register(String name) throws Exception {
		int id = this.registration.size();
		if (this.registration.containsKey(name)) {
			return this.getId(name);
		}
		this.registration.put(name, id);
		this.reverseMatch.put(id, name);
		return id;
	}

	public Integer getId(String name) throws Exception {
		Integer id = this.registration.get(name);
		if (id == null) {
			throw new Exception("Unknown name: " + name);
		}
		return id;
	}
	
	public Integer totalRegistration() {
		return this.registration.size();
	}
	
	public String getName(Integer id) {
		return this.reverseMatch.get(id);
	}
}
