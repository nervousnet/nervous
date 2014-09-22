package ch.ethz.soms.nervous.utils;

import java.util.UUID;

public class UnsignedArithmetic {

	public static short upcastToShort(byte in) {
		// ............................0xHHLL
		return (short) (((short) in) & 0x00FF);
	}

	public static int upcastToInt(byte in) {
		// .................0xHHHHLLLL
		return ((int) in) & 0x000000FF;
	}

	public static long upcastToLong(byte in) {
		// ..................0xHHHHHHHHLLLLLLLL
		return ((long) in) & 0x00000000000000FFL;
	}

	public static int upcastToInt(short in) {
		// .................0xHHHHLLLL
		return ((int) in) & 0x0000FFFF;
	}

	public static long upcastToLong(short in) {
		// ..................0xHHHHHHHHLLLLLLLL
		return ((long) in) & 0x000000000000FFFFL;
	}

	public static long upcastToLong(int in) {
		// ..................0xHHHHHHHHLLLLLLLL
		return ((long) in) & 0x00000000FFFFFFFFL;
	}

	public static UUID toUUIDLittleEndian(byte[] data, int start, int stop) {
		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;
		int size = stop - start + 1;
		int msbsize = size - 8;
		int lsbsize = 8;
		for (int i = 0; i < msbsize; i++) {
			msb |= upcastToLong(data[stop - i]) << ((msbsize - 1 - i) * 8);
		}
		for (int i = 0; i < lsbsize; i++) {
			lsb |= upcastToLong(data[stop - msbsize - i]) << ((lsbsize - 1 - i) * 8);
		}
		return new UUID(msb, lsb);
	}

	public static UUID toUUIDBigEndian(byte[] data, int start, int stop) {
		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;
		int size = stop - start + 1;
		int msbsize = size - 8;
		int lsbsize = 8;
		for (int i = 0; i < msbsize; i++) {
			msb |= upcastToLong(data[start + i]) << ((msbsize - 1 - i) * 8);
		}
		for (int i = 0; i < lsbsize; i++) {
			lsb |= upcastToLong(data[start + msbsize + i]) << ((lsbsize - 1 - i) * 8);
		}
		return new UUID(msb, lsb);
	}

	public static long stringMacToLong(String mac) {
		long macLong = 0x0000000000000000L;
		String[] macSplit = mac.split(":");
		int length = macSplit.length;
		for (int i = 0; i < length; i++) {
			// Workaround for stupid sign extension that always is the case with Java
			macLong |= upcastToLong(Short.decode("0x" + macSplit[i])) << (length - 1 - i) * 8;
		}
		return macLong;
	}

}
