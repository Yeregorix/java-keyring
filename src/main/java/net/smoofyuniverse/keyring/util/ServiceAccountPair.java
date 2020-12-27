package net.smoofyuniverse.keyring.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A service and account pair.
 */
public final class ServiceAccountPair {
	/**
	 * The service.
	 */
	public final String service;

	/**
	 * The account.
	 */
	public final String account;

	/**
	 * Creates a pair.
	 *
	 * @param service The service.
	 * @param account The account.
	 */
	public ServiceAccountPair(String service, String account) {
		this.service = service;
		this.account = account;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ServiceAccountPair) {
			ServiceAccountPair other = (ServiceAccountPair) o;
			return this.service.equals(other.service) && this.account.equals(other.account);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.service.hashCode();
		hash = 31 * hash + this.account.hashCode();
		return hash;
	}

	/**
	 * Writes the pair as two UTF-8 strings.
	 *
	 * @param out The output stream.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(this.service);
		out.writeUTF(this.account);
	}

	/**
	 * Reads a pair as two UTF-8 strings.
	 *
	 * @param in The input stream.
	 * @return The new pair.
	 * @throws IOException if an I/O error occurs.
	 */
	public static ServiceAccountPair read(DataInputStream in) throws IOException {
		return new ServiceAccountPair(in.readUTF(), in.readUTF());
	}

	/**
	 * Validates the service and the account.
	 *
	 * @param service The service.
	 * @param account The account.
	 */
	public static void validate(String service, String account) {
		if (service == null || service.isEmpty())
			throw new IllegalArgumentException("service");
		if (account == null)
			throw new IllegalArgumentException("account");
	}
}
