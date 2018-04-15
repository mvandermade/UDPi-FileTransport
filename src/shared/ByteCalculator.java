package shared;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
	
	public static byte[] byteBufferToArray(int bufSize, ByteBuffer buffer) {
		// Create a byte array
		byte[] bytes = new byte[bufSize];

		// Wrap a byte array into a buffer
		ByteBuffer buf = ByteBuffer.wrap(bytes);

		// Retrieve bytes between the position and limit
		// (see Putting Bytes into a ByteBuffer)
		bytes = new byte[buf.remaining()];

		// transfer bytes from this buffer into the given destination array
		buf.get(bytes, 0, bytes.length);

		// Retrieve all bytes in the buffer
		buf.clear();
		bytes = new byte[buf.capacity()];

		// transfer bytes from this buffer into the given destination array
		buf.get(bytes, 0, bytes.length);
		
		return bytes;
		
	}

}
