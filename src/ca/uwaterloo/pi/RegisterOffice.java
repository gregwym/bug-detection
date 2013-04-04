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

	/**
	 * Register a function name. Simply return the id if the name already
	 * exists.
	 * 
	 * @param name
	 *            function name
	 * @return function id
	 */
	public Integer register(String name) {
		// Next id is the size of current registration
		int id = this.registration.size();
		// If contain the name, return its ID. Should never fail
		if (this.registration.containsKey(name)) {
			try {
				return this.getId(name);
			} catch (Exception e) {
				// Should not fail
				e.printStackTrace();
			}
		}
		// Otherwise, register it
		this.registration.put(name, id);
		this.reverseMatch.put(id, name);
		return id;
	}

	/**
	 * Get the id related to the name. Throw exception if the name has not been
	 * registered yet
	 * 
	 * @param name
	 *            function name
	 * @return function id
	 * @throws Exception
	 *             the name has not been registered yet
	 */
	public Integer getId(String name) throws Exception {
		Integer id = this.registration.get(name);
		if (id == null) {
			throw new Exception("Unknown name: " + name);
		}
		return id;
	}

	/**
	 * Get the name related to the id. Throw exception if the id does not exists
	 * 
	 * @param id
	 *            function id
	 * @return function name
	 * @throws Exception
	 */
	public String getName(Integer id) throws Exception {
		String name = this.reverseMatch.get(id);
		if (name == null) {
			throw new Exception("Unknown id: " + id);
		}
		return name;
	}

	/**
	 * @return total number of registered name
	 */
	public Integer totalRegistration() {
		return this.registration.size();
	}

}
