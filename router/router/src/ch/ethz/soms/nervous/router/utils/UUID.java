package ch.ethz.soms.nervous.router.utils;

public class UUID {

	public static byte[] toByteArray(long huuid, long luuid) {
		byte[] uuid = new byte[16];
		// High bits
		uuid[0] = (byte) (huuid >>> 56);
		uuid[1] = (byte) (huuid >>> 48);
		uuid[2] = (byte) (huuid >>> 40);
		uuid[3] = (byte) (huuid >>> 32);
		uuid[4] = (byte) (huuid >>> 24);
		uuid[5] = (byte) (huuid >>> 16);
		uuid[6] = (byte) (huuid >>> 8);
		uuid[7] = (byte) (huuid >>> 0);
		// Low bits
		uuid[8] = (byte) (luuid >>> 56);
		uuid[9] = (byte) (luuid >>> 48);
		uuid[10] = (byte) (luuid >>> 40);
		uuid[11] = (byte) (luuid >>> 32);
		uuid[12] = (byte) (luuid >>> 24);
		uuid[13] = (byte) (luuid >>> 16);
		uuid[14] = (byte) (luuid >>> 8);
		uuid[15] = (byte) (luuid >>> 0);
		return uuid;
	}

	public static long getHighUUID(byte[] uuid) {
		return (((long) uuid[0]) << 56) | (((long) uuid[1]) << 48) | (((long) uuid[2]) << 40) | (((long) uuid[3]) << 32) | (((long) uuid[4]) << 24) | (((long) uuid[5]) << 16) | (((long) uuid[6]) << 8) | (((long) uuid[7]));
	}

	public static long getLowUUID(byte[] uuid) {
		return (((long) uuid[8]) << 56) | (((long) uuid[9]) << 48) | (((long) uuid[10]) << 40) | (((long) uuid[11]) << 32) | (((long) uuid[12]) << 24) | (((long) uuid[13]) << 16) | (((long) uuid[14]) << 8) | (((long) uuid[15]));
	}

}
