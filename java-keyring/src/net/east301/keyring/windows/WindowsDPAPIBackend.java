package net.east301.keyring.windows;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Crypt32Util;
import net.east301.keyring.KeyringBackend;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;
import net.east301.keyring.util.FileBasedLock;
import net.east301.keyring.util.LockException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keyring backend which uses Windows DPAPI
 */
public class WindowsDPAPIBackend extends KeyringBackend {

    /**
     * Returns true when the backend is supported
     */
    @Override
    public boolean isSupported() {
        return Platform.isWindows();
    }

    /**
     * Returns true if the backend directory uses some file to store passwords
     */
    @Override
    public boolean isKeyStorePathRequired() {
        return true;
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
    public String getPassword(String service, String account) throws LockException, PasswordRetrievalException {
        FileBasedLock fileLock = new FileBasedLock(getLockPath());

        try {
            fileLock.lock();

            PasswordEntry targetEntry = null;
            try {
                for (PasswordEntry entry : loadPasswordEntries()) {
                    if (entry.getService().equals(service) && entry.getAccount().equals(account)) {
                        targetEntry = entry;
                        break;
                    }
                }
            } catch (Exception e) {
                throw new PasswordRetrievalException("Failed to load password entries from the keystore", e);
            }

            if (targetEntry == null)
                throw new PasswordRetrievalException("Password related to the specified service and account is not found");

            byte[] decryptedBytes;

            try {
                decryptedBytes = Crypt32Util.cryptUnprotectData(targetEntry.getPassword());
            } catch (Exception ex) {
                throw new PasswordRetrievalException("Failed to decrypt password");
            }

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } finally {
            try {
                fileLock.release();
            } catch (Exception e) {
                Logger.getLogger("WindowsDPAPIBackend").log(Level.SEVERE, "Failed to release file lock", e);
            }
        }
    }

    /**
     * Sets password to key store
     *
     * @param service   Service name
     * @param account   Account name
     * @param password  Password
     *
     * @throws PasswordSaveException    Thrown when an error happened while saving the password
     */
    @Override
    public void setPassword(String service, String account, String password) throws LockException, PasswordSaveException {
        FileBasedLock fileLock = new FileBasedLock(getLockPath());

        try {
            fileLock.lock();

            byte[] encryptedBytes;
            try {
                encryptedBytes = Crypt32Util.cryptProtectData(password.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new PasswordSaveException("Failed to encrypt password", e);
            }

            ArrayList<PasswordEntry> entries;
            try {
                entries = loadPasswordEntries();
            } catch (Exception e) {
                throw new PasswordSaveException("Failed to load password entries from the keystore", e);
            }
            PasswordEntry targetEntry = null;

            for (PasswordEntry entry : entries) {
                if (entry.getService().equals(service) && entry.getAccount().equals(account)) {
                    targetEntry = entry;
                    break;
                }
            }

            if (targetEntry != null)
                targetEntry.setPassword(encryptedBytes);
            else
                entries.add(new PasswordEntry(service, account, encryptedBytes));

            try {
                savePasswordEntries(entries);
            } catch (Exception e) {
                throw new PasswordSaveException("Failed to save password entries to the keystore", e);
            }
        } finally {
            try {
                fileLock.release();
            } catch (Exception e) {
                Logger.getLogger("WindowsDPAPIBackend").log(Level.SEVERE, "Failed to release file lock", e);
            }
        }
    }

    /**
     * Gets backend ID
     */
    @Override
    public String getID() {
        return "WindowsDPAPI";
    }

    /**
     * Returns path to a file for lock
     */
    public Path getLockPath() {
        return this.keyStorePath.resolveSibling(this.keyStorePath.getFileName().toString() + ".lock");
    }

    /**
     * Loads password entries to a file.
     * This method is not thread/process safe.
     */
    private ArrayList<PasswordEntry> loadPasswordEntries() throws Exception {
        if (Files.exists(this.keyStorePath) && Files.size(this.keyStorePath) > 0) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(this.keyStorePath))) {
                return new ArrayList<>(Arrays.asList((PasswordEntry[]) in.readObject()));
            }
        }
        return new ArrayList<>();
    }

    /**
     * Saves password entries to a file
     * This method is not thread/process safe.
     *
     * @param entries   Password entries to be saved
     */
    private void savePasswordEntries(ArrayList<PasswordEntry> entries) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(this.keyStorePath))) {
            out.writeObject(entries.toArray(new PasswordEntry[0]));
            out.flush();
        }
    }

}
