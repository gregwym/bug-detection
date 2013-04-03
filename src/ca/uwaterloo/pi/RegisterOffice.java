package ca.uwaterloo.pi;

import java.util.HashMap;
import java.util.Map;

public class RegisterOffice {
	private Map<String, Integer> registration;

	public RegisterOffice() {
		this.registration = new HashMap<String, Integer>();
	}
	
	public Integer register(String name) throws Exception {
		int id = this.registration.size();
		if (this.registration.containsKey(name)) {
			return this.getId(name);
		}
		this.registration.put(name, id);
		return id;
	}

	public Integer getId(String name) throws Exception {
		Integer id = this.registration.get(name);
		if (id == null) {
			throw new Exception("Unknown name: " + name);
		}
		return id;
	}
}
