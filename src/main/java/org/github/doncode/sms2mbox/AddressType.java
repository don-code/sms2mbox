package org.github.doncode.sms2mbox;

/**
 * Determines the meaning of the "address" field in an SMS.
 */
public enum AddressType {
	INBOUND,
	OUTBOUND;
	
	private static AddressType[] allValues = values();
	public static AddressType fromOrdinal(int n) {
		return allValues[n-1];
	}
}
