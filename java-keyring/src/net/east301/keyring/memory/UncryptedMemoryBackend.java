package net.east301.keyring.memory;

import net.east301.keyring.KeyringBackend;
import net.east301.keyring.PasswordRetrievalException;

import java.util.HashMap;
import java.util.Map;

/**
 * On-memory key store
 */
public class UncryptedMemoryBackend extends KeyringBackend {

    private final HashMap<String[], String> store = new HashMap<>();

    /**
     * Initializes an instance of UncryptedMemoryBackend
     */
    public UncryptedMemoryBackend() {}

    /**
     * Returns true when the backend is supported
     */
    @Override
    public boolean isSupported() {
        return true;
    }

    /**
     * Returns true if the backend directory uses some file to store passwords
     */
    @Override
    public boolean isKeyStorePathRequired() {
        return false;
    }

    /**
     * Gets password from key store
     *
     * @param service   Service name
     * @param account   Account name
     *
     * @return  Password related to specified service and account
     *
     * @throws PasswordRetrievalException   Thrown when an error happened while getting password
     */
    @Override
    public String getPassword(String service, String account) throws PasswordRetrievalException {
        synchronized (this.store) {
            for (Map.Entry<String[], String> entries : this.store.entrySet()) {
                String[] key = entries.getKey();

                if (key[0].equals(service) && key[1].equals(account))
                    return entries.getValue();
            }

            throw new PasswordRetrievalException("Password related to the specified service and account is not found");
        }
    }

    /**
     * Sets password to key store
     *
     * @param service   Service name
     * @param account   Account name
     * @param password  Password
     */
    @Override
    public void setPassword(String service, String account, String password) {
        synchronized (this.store) {
            String[] targetKey = null;

            for (Map.Entry<String[], String> entries : this.store.entrySet()) {
                String[] key = entries.getKey();

                if (key[0].equals(service) && key[1].equals(account)) {
                    targetKey = key;
                    break;
                }
            }

            if (targetKey == null)
                targetKey = new String[] { service, account };
            this.store.put(targetKey, password);
        }
    }

    /**
     * Gets backend ID
     */
    @Override
    public String getID() {
        return "UncryptedMemory";
    }
}
