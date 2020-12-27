package net.smoofyuniverse.keyring.memory;

import net.smoofyuniverse.keyring.KeyringBackend;
import net.smoofyuniverse.keyring.util.ServiceAndAccount;

import java.util.HashMap;
import java.util.Map;

/**
 * On-memory key store
 */
public class UncryptedMemoryBackend extends KeyringBackend {
	private final Map<ServiceAndAccount, String> map = new HashMap<>();

	@Override
	public String getID() {
		return "UncryptedMemory";
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public boolean isKeyStorePathRequired() {
		return false;
	}

	@Override
	public String getPassword(String service, String account) {
		ServiceAndAccount.validate(service, account);
		return this.map.get(new ServiceAndAccount(service, account));
	}

	@Override
	public void setPassword(String service, String account, String password) {
		ServiceAndAccount.validate(service, account);
		this.map.put(new ServiceAndAccount(service, account), password);
	}
}
