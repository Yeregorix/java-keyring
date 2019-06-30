package net.east301.keyring;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test of KeyringBackend class
 */
public class KeyringBackendTest {

    /**
     * Test of getKeyStorePath method, of class KeyringBackend.
     */
    @Test
    public void testGetKeyStorePath() {
        KeyringBackend instance = new KeyringBackendImpl();

        assertNull(instance.getKeyStorePath());

        instance.setKeyStorePath(Paths.get("/path/to/keystore"));
        assertEquals(Paths.get("/path/to/keystore"), instance.getKeyStorePath());
    }

    /**
     * Test of setKeyStorePath method, of class KeyringBackend.
     */
    @Test
    public void testSetKeyStorePath() {
        KeyringBackend instance = new KeyringBackendImpl();

        instance.setKeyStorePath(Paths.get("/path/to/keystore"));
        assertEquals(Paths.get("/path/to/keystore"), instance.getKeyStorePath());
    }

    public class KeyringBackendImpl extends KeyringBackend {

        @Override
        public boolean isSupported() {
            return false;
        }

        @Override
        public boolean isKeyStorePathRequired() {
            return false;
        }

        @Override
        public String getPassword(String service, String account) {
            return "";
        }

        @Override
        public void setPassword(String service, String account, String password) {
        }

        @Override
        public String getID() {
            return "";
        }
    }

}
