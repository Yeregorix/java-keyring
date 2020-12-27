package net.smoofyuniverse.keyring.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ServiceAccountPair {
	public final String service, account;

	public ServiceAccountPair(String service, String account) {
		this.service = service;
		this.account = account;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceAccountPair)
			return ((ServiceAccountPair) obj).service.equals(this.service) && ((ServiceAccountPair) obj).account.equals(this.account);
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.service.hashCode();
		hash = 31 * hash + this.account.hashCode();
		return hash;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(this.service);
		out.writeUTF(this.account);
	}

	public static ServiceAccountPair read(DataInputStream in) throws IOException {
		return new ServiceAccountPair(in.readUTF(), in.readUTF());
	}

	public static void validate(String service, String account) {
		if (service == null || service.isEmpty())
			throw new IllegalArgumentException("service");
		if (account == null)
			throw new IllegalArgumentException("account");
	}
}
