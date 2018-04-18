package client;

import java.util.HashMap;

public class Utils {

	/**
	 * Make a long from a byte[6], useful for storing the MAC addresses
	 * 
	 * @param bytes
	 * @return
	 */
	public static long macToLong(byte[] bytes) {
		long ret = 0;
		ret += (bytes[0] & 0xFF) << 40;
		ret += (bytes[1] & 0xFF) << 32;
		ret += (bytes[2] & 0xFF) << 24;
		ret += (bytes[3] & 0xFF) << 16;
		ret += (bytes[4] & 0xFF) << 8;
		ret += (bytes[5]) & 0xFF;
		return ret;
	}

	/**
	 * Returns a HashMap of the known AP locations as a <String, Postion> The string
	 * is used because this is easily searchable for the hashMap
	 * 
	 * @return
	 */
	public static HashMap<String, Position> getKnownLocations() {

		HashMap<String, Position> knownLocations = new HashMap<String, Position>();

		// APs SP 3
		knownLocations.put("F4:CF:E2:20:4C:10", new Position(15,6));	//ap2700-0102
		knownLocations.put("F4:CF:E2:20:4C:11", new Position(15,6));	//ap2700-0102
		knownLocations.put("F4:CF:E2:20:4C:12", new Position(15,6));	//ap2700-0102
		
		knownLocations.put("F4:CF:E2:36:48:00", new Position(15,42));	//ap2700-0086
		knownLocations.put("F4:CF:E2:36:48:01", new Position(15,42));	//ap2700-0086
		knownLocations.put("F4:CF:E2:36:48:02", new Position(15,42));	//ap2700-0086
		
		knownLocations.put("F4:CF:E2:33:2F:20", new Position(52,6));	//ap2700-0103
		knownLocations.put("F4:CF:E2:33:2F:21", new Position(52,6));	//ap2700-0103
		knownLocations.put("F4:CF:E2:33:2F:22", new Position(52,6));	//ap2700-0103
		
		knownLocations.put("F4:CF:E2:83:15:70", new Position(52,42));	//ap2700-0088
		knownLocations.put("F4:CF:E2:83:15:71", new Position(52,42));	//ap2700-0088
		knownLocations.put("F4:CF:E2:83:15:72", new Position(52,42));	//ap2700-0088
		
		// APs SP 4
		knownLocations.put("F4:CF:E2:54:E3:F0", new Position(72,42));	//ap2700-0090
		knownLocations.put("F4:CF:E2:54:E3:F1", new Position(72,42));	//ap2700-0090
		knownLocations.put("F4:CF:E2:54:E3:F2", new Position(72,42));	//ap2700-0090
		
		knownLocations.put("F4:CF:E2:33:3D:C0", new Position(72,6));	//ap2700-0104
		knownLocations.put("F4:CF:E2:33:3D:C1", new Position(72,6));	//ap2700-0104
		knownLocations.put("F4:CF:E2:33:3D:C2", new Position(72,6));	//ap2700-0104
		
		knownLocations.put("F4:CF:E2:82:FA:90", new Position(111,6));	//ap2700-0105
		knownLocations.put("F4:CF:E2:82:FA:91", new Position(111,6));	//ap2700-0105
		knownLocations.put("F4:CF:E2:82:FA:92", new Position(111,6));	//ap2700-0105
		
		knownLocations.put("F4:CF:E2:33:67:20", new Position(111,42));	//ap2700-0089
		knownLocations.put("F4:CF:E2:33:67:21", new Position(111,42));	//ap2700-0089
		knownLocations.put("F4:CF:E2:33:67:22", new Position(111,42));	//ap2700-0089
		
		return knownLocations;
	}

	public static HashMap<String, Position> getKnownLocations5GHz() {

		HashMap<String, Position> knownLocations = new HashMap<String, Position>();
		
		// APs SP 3
		knownLocations.put("F4:CF:E2:20:4C:1E", new Position(15,6));	//ap2700-0102
		knownLocations.put("F4:CF:E2:20:4C:1D", new Position(15,6));	//ap2700-0102
		knownLocations.put("F4:CF:E2:20:4C:1F", new Position(15,6));	//ap2700-0102
		
		knownLocations.put("F4:CF:E2:36:48:0E", new Position(15,42));	//ap2700-0086
		knownLocations.put("F4:CF:E2:36:48:0D", new Position(15,42));	//ap2700-0086
		knownLocations.put("F4:CF:E2:36:48:0F", new Position(15,42));	//ap2700-0086
		
		knownLocations.put("F4:CF:E2:33:2F:2E", new Position(52,6));	//ap2700-0103
		knownLocations.put("F4:CF:E2:33:2F:2D", new Position(52,6));	//ap2700-0103
		knownLocations.put("F4:CF:E2:33:2F:2F", new Position(52,6));	//ap2700-0103
		
		knownLocations.put("F4:CF:E2:83:15:7E", new Position(52,42));	//ap2700-0088
		knownLocations.put("F4:CF:E2:83:15:7D", new Position(52,42));	//ap2700-0088
		knownLocations.put("F4:CF:E2:83:15:7F", new Position(52,42));	//ap2700-0088
		
		// APs SP 4
		knownLocations.put("F4:CF:E2:54:E3:FE", new Position(72,42));	//ap2700-0090
		knownLocations.put("F4:CF:E2:54:E3:FD", new Position(72,42));	//ap2700-0090
		knownLocations.put("F4:CF:E2:54:E3:FF", new Position(72,42));	//ap2700-0090
		
		knownLocations.put("F4:CF:E2:33:3D:CE", new Position(72,6));	//ap2700-0104
		knownLocations.put("F4:CF:E2:33:3D:CD", new Position(72,6));	//ap2700-0104
		knownLocations.put("F4:CF:E2:33:3D:CF", new Position(72,6));	//ap2700-0104
		
		knownLocations.put("F4:CF:E2:82:FA:9E", new Position(111,6));	//ap2700-0105
		knownLocations.put("F4:CF:E2:82:FA:9D", new Position(111,6));	//ap2700-0105
		knownLocations.put("F4:CF:E2:82:FA:9F", new Position(111,6));	//ap2700-0105
		
		knownLocations.put("F4:CF:E2:33:67:2E", new Position(111,42));	//ap2700-0089
		knownLocations.put("F4:CF:E2:33:67:2D", new Position(111,42));	//ap2700-0089
		knownLocations.put("F4:CF:E2:33:67:2F", new Position(111,42));	//ap2700-0089
		
		return knownLocations;
	}
	

}