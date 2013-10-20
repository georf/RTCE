package de.uni_rostock.rtce.rebeca;

import java.net.Inet4Address;
import java.net.InetAddress;

public class IpAddressDescriptor {

	private final InetAddress address;
	private final String interfaceName;
	private short type = -1;

	public IpAddressDescriptor(InetAddress add, String name) {
		address = add;
		interfaceName = name;
	}

	public String getNetworkDescription() {
		switch (getNetworkType()) {
		case 1:
			return "This machine";

		case 2:
			return "IPv6 network";

		case 3:
			return "Private network";

		default:
		case 4:
			return "Network";
		}
	}

	public short getNetworkType() {
		if (type != -1)
			return type;

		if (address instanceof Inet4Address) {
			String hostname = address == null ? "" : address.getHostAddress();
			
			if (hostname.startsWith("10.")) {
				type = 3;
			} else if (hostname.startsWith("127.")) {
				type = 1;
			} else if (hostname.startsWith("172.16.")) {
				type = 3;
			} else if (hostname.startsWith("192.")) {
				type = 3;
			} else {
				type = 4;
			}
		} else {
			type = 2;
		}

		return type;
	}

	public String getFriendlyName() {
		String hostname = address == null ? "" : address.getHostAddress() + ", ";
		return getNetworkDescription() + " (" + hostname
				+ interfaceName + ")";
	}

	public InetAddress getInetAddress() {
		return address;
	}

	public String getInterfaceName() {
		return interfaceName;
	}
}
