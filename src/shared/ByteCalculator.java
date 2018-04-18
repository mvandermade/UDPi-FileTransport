package shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteCalculator {
	
	public static byte[] intToLeByteArray (Integer putInteger) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.putInt(putInteger);
		byte[] result = byteBuffer.array();
		return result;
	}
	
	public static int byteArrayToLeInt(byte[] b) {
	    final ByteBuffer bb = ByteBuffer.wrap(b);
	    bb.order(ByteOrder.LITTLE_ENDIAN);
	    return bb.getInt();
	}
	
	public static byte[] byteBufferToArray(ByteBuffer buffer) {

		byte[] bytes;
		if(buffer.hasArray()) {
		    bytes = buffer.array();
		} else {
		    bytes = new byte[buffer.remaining()];
		    buffer.get(bytes);
		}
		
		return bytes;
		
	}
	
	public static byte[] longToBytes(long crcLong) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(crcLong);
	    
	    return buffer.array();
	}

	public static long bytesToLong(byte[] crcBytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    
	    buffer.put(crcBytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}

}
