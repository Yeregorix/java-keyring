package net.east301.keyring.windows;

import java.io.Serializable;

/**
 * Password Entry
 */
class PasswordEntry implements Serializable {

    public static final long serialVersionUID = -3158477865959025220;

    /**
     * Service name
     */
    private String service;

    /**
     * Account name
     */
    private String account;

    /**
     * Password
     */
    private byte[] password;

    /**
     * Initializes an instance of PasswordEntry
     *
     * @param service   Service name
     * @param account   Account name
     * @param password  Password
     */
    public PasswordEntry(String service, String account, byte[] password) {
        this.service = service;
        this.account = account;
        this.password = password;
    }

    /**
     * Returns service name
     */
    public String getService() {
        return this.service;
    }

    /**
     * Sets service name
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Returns account name
     */
    public String getAccount() {
        return this.account;
    }

    /**
     * Sets account name
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Returns password
     */
    public byte[] getPassword() {
        return this.password;
    }

    /**
     * Sets password
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }

}
